package ru.nsu.shelbogashev.tdgserver.service.ws;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.nsu.shelbogashev.tdgserver.exception.TowerDefenseException;
import ru.nsu.shelbogashev.tdgserver.model.ws.Status;
import ru.nsu.shelbogashev.tdgserver.model.ws.WebSocketUser;

import static ru.nsu.shelbogashev.tdgserver.message.ResponseMessage.UNEXPECTED_ERROR;
import static ru.nsu.shelbogashev.tdgserver.message.ResponseMessage.USER_NOT_FOUND;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class WebSocketUserService {

    SimpMessagingTemplate messagingTemplate;
    RedisTemplate<String, WebSocketUser> webSocketRedisTemplate;
    RedisUserLock userLock;

    public WebSocketUser getWebSocketUser(String sessionId) {
        /* Предполагается, что сессия будет храниться в единственном экземпляре. */
        log.info("getWebSocketUser() : " + UserKeyHelper.makeKey(sessionId));
        return webSocketRedisTemplate.opsForValue().get(UserKeyHelper.makeKey(sessionId));
    }

    public WebSocketUser popWebSocketUser(String sessionId) {
        return webSocketRedisTemplate.opsForValue().getAndDelete(UserKeyHelper.makeKey(sessionId));
    }

    public void addWebSocketUser(WebSocketUser webSocketUser) {
        webSocketRedisTemplate.opsForValue().set(UserKeyHelper.makeKey(webSocketUser.getSessionId()), webSocketUser);
    }

    public void updateWebSocketUser(WebSocketUser webSocketUser) {
        webSocketRedisTemplate.opsForValue().set(UserKeyHelper.makeKey(webSocketUser.getSessionId()), webSocketUser);
    }

    public void returnToMenu(String sessionId) {
        // TODO: Нет атомарности.
        synchronized (userLock) {
            WebSocketUser webSocketUser = webSocketRedisTemplate.opsForValue().get(UserKeyHelper.makeKey(sessionId));
            if (webSocketUser == null) {
                throw new TowerDefenseException(UNEXPECTED_ERROR);
            }
            webSocketUser.setLobbyId(null);
            webSocketUser.setStatus(Status.IN_MENU);
            webSocketRedisTemplate.opsForValue().set(sessionId, webSocketUser);
        }
    }

    private static class UserKeyHelper {
        private static final String KEY = "tdgserver:users:";

        public static String makeKey(String sessionId) {
            return KEY + sessionId;
        }
    }

}
