package ru.nsu.shelbogashev.tdgserver.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.nsu.shelbogashev.tdgserver.server.exception.TowerDefenseException;
import ru.nsu.shelbogashev.tdgserver.server.ws.Status;
import ru.nsu.shelbogashev.tdgserver.server.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;

import java.util.List;

import static ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage.*;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class LobbyService {

    RedisTemplate<String, Lobby> lobbyRedisTemplate;
    WebSocketUserService userService;
    RedisUserLock userLock;

    public Lobby initLobby(String adminSessionId) {
        log.info("createLobby() : " + adminSessionId);

        // A mutex is required for the user. Fast requests to the server can create dangling lobbies.
        synchronized (userLock) {
            WebSocketUser user = userService.getWebSocketUserBySessionId(adminSessionId);
            if (user.getLobbyId() != null) {
                log.info("Exception in initLobby() for user with session id " + adminSessionId + " : " + SYSTEM_ALREADY_HAS_LOBBY_ERROR);
                throw new TowerDefenseException(SYSTEM_ALREADY_HAS_LOBBY_ERROR);
            }

            Lobby lobby = Lobby.builder().adminSessionId(adminSessionId).build();
            /* IMPORTANT */
            lobby.getMembers().add(adminSessionId);
            setLobby(lobby.getId(), lobby);

            user.setLobbyId(lobby.getId());
            user.setStatus(Status.IN_LOBBY);
            userService.setWebSocketUser(user);
            log.info("createLobby() : lobby.getId()=%s".formatted(lobby.getId()));
            return lobby;
        }
    }

    public Lobby destroyLobby(String adminSessionId) {
        WebSocketUser user = userService.getWebSocketUserBySessionId(adminSessionId);
        return destroyLobbyByAdmin(user);
    }

    public Lobby destroyLobbyByAdmin(WebSocketUser admin) {

        // TODO: is required to log all unexpected error.
        if (admin == null) throw new TowerDefenseException(UNEXPECTED_ERROR);
        if (admin.getLobbyId() == null) throw new TowerDefenseException(USER_NOT_IN_A_LOBBY);

        log.info("destroyLobbyByAdmin() : adminSessionId=" + admin.getSessionId());

        return destroyLobbyProcessing(admin);
    }

    private Lobby destroyLobbyProcessing(WebSocketUser admin) {
        log.info("destroyLobbyProcessing() : lobbyId=" + admin.getLobbyId());
        synchronized (userLock) {
            Lobby lobby = popLobby(admin.getLobbyId());

            if (lobby == null) throw new TowerDefenseException(UNEXPECTED_ERROR);
            if (!lobby.getAdminSessionId().equals(admin.getSessionId()))
                throw new TowerDefenseException(USER_NOT_AN_ADMIN);

            List<String> members = lobby.getMembers();
            members.forEach(userService::returnToMenu);
            userService.returnToMenu(lobby.getAdminSessionId());
            log.info("destroyLobbyProcessing() : lobby destroyed");
            return lobby;
        }
    }

    public Lobby getLobby(String lobbyId) {
        return lobbyRedisTemplate.opsForValue().get(LobbyKeyHelper.makeKey(lobbyId));
    }

    public Lobby popLobby(String lobbyId) {
        return lobbyRedisTemplate.opsForValue().getAndDelete(LobbyKeyHelper.makeKey(lobbyId));
    }

    public void setLobby(String lobbyId, Lobby lobby) {
        lobbyRedisTemplate.opsForValue().set(LobbyKeyHelper.makeKey(lobbyId), lobby);
    }

    public Lobby acceptLobby(String sessionId, String lobbyId) {
        Lobby lobby = getLobby(lobbyId);
        List<String> members = lobby.getMembers();
        members.add(sessionId);
        lobby.setMembers(members);
        setLobby(lobbyId, lobby);
        return lobby;
    }

    public Lobby inviteFriend(String sessionId) {
        WebSocketUser webSocketUser = userService.getWebSocketUserBySessionId(sessionId);
        return getLobby(webSocketUser.getLobbyId());
    }

    private static class LobbyKeyHelper {
        private static final String KEY = "tdgserver:lobbies:";

        public static String makeKey(String lobbyId) {
            return KEY + lobbyId;
        }
    }

}
