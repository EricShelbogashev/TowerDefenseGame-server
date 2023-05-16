package ru.nsu.shelbogashev.tdgserver.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import ru.nsu.shelbogashev.tdgserver.FIXED_exception.TowerDefenseException;
import ru.nsu.shelbogashev.tdgserver.model.rest.User;
import ru.nsu.shelbogashev.tdgserver.model.ws.Status;
import ru.nsu.shelbogashev.tdgserver.model.ws.WebSocketUser;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.nsu.shelbogashev.tdgserver.FIXED_message.ResponseMessage.UNEXPECTED_ERROR;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class WebSocketUserService {
    RedisTemplate<String, WebSocketUser> webSocketRedisTemplate;
    StringRedisTemplate shadowRedisTemplate;
    RedisUserLock userLock;
    FriendshipService friendshipService;
    UserService userService;

    public WebSocketUser getWebSocketUserBySessionId(String sessionId) {
        return webSocketRedisTemplate.opsForValue().get(UserKeyHelper.makeKey(sessionId));
    }

    public WebSocketUser getWebSocketUserByUsername(String userName) {
        String sessionId = shadowRedisTemplate.opsForValue().get(UserKeyHelper.makeShadowKey(userName));
        return webSocketRedisTemplate.opsForValue().get(UserKeyHelper.makeKey(sessionId));
    }

    public WebSocketUser popWebSocketUser(WebSocketUser webSocketUser) {
        return popWebSocketUser(webSocketUser.getSessionId());
    }

    public List<WebSocketUser> getOnlineFriends(String sessionId) {
        WebSocketUser webSocketUser = getWebSocketUserBySessionId(sessionId);
        Optional<User> user = userService.findByUsername(webSocketUser.getUsername());
        if (user.isEmpty()) throw new TowerDefenseException(UNEXPECTED_ERROR);
        List<User> allFriends = friendshipService.getAllFriends(user.get());
        return allFriends.stream().map(
                it -> getWebSocketUserByUsername(it.getUsername())
        ).filter(Objects::nonNull).toList();
    }

    public WebSocketUser popWebSocketUser(String sessionId) {
        log.info("popWebSocketUser() : sessionId=" + sessionId);
        WebSocketUser webSocketUser = webSocketRedisTemplate.opsForValue().getAndDelete(UserKeyHelper.makeKey(sessionId));
        if (webSocketUser == null) return null;
        shadowRedisTemplate.opsForValue().getAndDelete(UserKeyHelper.makeShadowKey(webSocketUser.getUsername()));
        return webSocketUser;
    }

    public void returnToMenu(String sessionId) {
        WebSocketUser webSocketUser = webSocketRedisTemplate.opsForValue().get(UserKeyHelper.makeKey(sessionId));
        if (webSocketUser == null) {
            throw new TowerDefenseException(UNEXPECTED_ERROR);
        }
        webSocketUser.setLobbyId(null);
        webSocketUser.setStatus(Status.IN_MENU);
        setWebSocketUser(webSocketUser);
    }

    public void setWebSocketUser(WebSocketUser webSocketUser) {
        webSocketRedisTemplate.opsForValue().set(UserKeyHelper.makeKey(webSocketUser.getSessionId()), webSocketUser);
        shadowRedisTemplate.opsForValue().set(UserKeyHelper.makeShadowKey(webSocketUser.getUsername()), webSocketUser.getSessionId());
    }

    private static class UserKeyHelper {
        private static final String KEY = "tdgserver:users:";
        private static final String SHADOW_KEY = "tdgserver:users:usernames";

        public static String makeKey(String sessionId) {
            return KEY + sessionId;
        }

        private static String makeShadowKey(String sessionId) {
            return SHADOW_KEY + sessionId;
        }
    }

}
