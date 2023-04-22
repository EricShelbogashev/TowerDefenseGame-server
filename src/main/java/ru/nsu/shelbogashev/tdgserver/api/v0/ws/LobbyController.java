package ru.nsu.shelbogashev.tdgserver.api.v0.ws;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.nsu.shelbogashev.tdgserver.dto.model.LobbyDto;
import ru.nsu.shelbogashev.tdgserver.service.ws.LobbyService;

import java.security.Principal;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class LobbyController {
    public static final String API_INIT_LOBBY = "/api/lobby.init";
    public static final String API_DESTROY_LOBBY = "/api/lobby.destroy";

    public static final String FETCH_LOBBY_CREATED = "/topic/lobby.created";
    public static final String FETCH_LOBBY_DESTROYED = "/topic/lobby.{lobby_id}.destroyed";
    public LobbyService lobbyService;
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping(value = API_INIT_LOBBY)
    @SendToUser(FETCH_LOBBY_CREATED)
    public LobbyDto fetchLobbyCreated() {
        return lobbyService.initLobby(SimpAttributesContextHolder.currentAttributes().getSessionId());
    }

    @MessageMapping(value = API_DESTROY_LOBBY)
    public void fetchLobbyDestroyed() {
        LobbyDto lobbyDto = lobbyService.destroyLobby(SimpAttributesContextHolder.currentAttributes().getSessionId());
        messagingTemplate.convertAndSend(
                LobbyDestinationHelper.makeDestination(FETCH_LOBBY_DESTROYED, lobbyDto.getId()),
                lobbyDto
        );
    }

    protected static class LobbyDestinationHelper {
        static final String KEY = "{lobby_id}";

        static String makeDestination(String destination, String lobbyId) {
            /* TODO: обобщить ключ и API. Сделать общий класс для апи с валидаторами и утилитами
                 по обработке путей (подстановку id в них и тп) */
            return destination.replace(KEY, lobbyId);
        }
    }
}
