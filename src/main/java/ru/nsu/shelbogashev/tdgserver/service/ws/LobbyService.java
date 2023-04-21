package ru.nsu.shelbogashev.tdgserver.service.ws;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.nsu.shelbogashev.tdgserver.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.dto.model.LobbyDto;
import ru.nsu.shelbogashev.tdgserver.exception.TowerDefenseException;
import ru.nsu.shelbogashev.tdgserver.model.ws.Lobby;
import ru.nsu.shelbogashev.tdgserver.model.ws.Status;
import ru.nsu.shelbogashev.tdgserver.model.ws.WebSocketUser;

import static ru.nsu.shelbogashev.tdgserver.message.ResponseMessage.SYSTEM_ALREADY_HAS_LOBBY_ERROR;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class LobbyService {

    RedisTemplate<String, Lobby> redisTemplate;
    WebSocketUserService userService;

    public LobbyDto initLobby(String adminSessionId) {
        log.info("createLobby() : " + adminSessionId);

        // Необходим мьютекс на пользователя. Быстрые запросы к серверу могут создать dangling лобби.
        WebSocketUser user = userService.getWebSocketUser(adminSessionId);
        if (user.getLobbyId() != null) {
            log.info("Exception in initLobby() for user with session id " + adminSessionId + " : " + SYSTEM_ALREADY_HAS_LOBBY_ERROR);
            throw new TowerDefenseException(SYSTEM_ALREADY_HAS_LOBBY_ERROR);
        }

        Lobby lobby = Lobby.builder().adminSessionId(adminSessionId).build();
        redisTemplate.opsForValue().set(LobbyKeyHelper.makeKey(lobby.getId()), lobby);

        user.setLobbyId(lobby.getId());
        user.setStatus(Status.IN_LOBBY);
        userService.updateWebSocketUser(user);
        return Mapper.toLobbyDto(lobby);
    }

    private static class LobbyKeyHelper {
        private static final String KEY = "tdgserver:lobbies:";

        public static String makeKey(String lobbyId) {
            return KEY + lobbyId;
        }
    }

}
