package ru.nsu.shelbogashev.tdgserver.api.v0;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.nsu.shelbogashev.tdgserver.server.dto.LobbyDto;
import ru.nsu.shelbogashev.tdgserver.server.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.service.LobbyService;
import ru.nsu.shelbogashev.tdgserver.service.WebSocketUserService;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class LobbyController {
    public static final String API_INIT_LOBBY = "/api/lobby.init";
    public static final String API_DESTROY_LOBBY = "/api/lobby.destroy";
    public static final String API_ACCEPT_LOBBY = "/api/lobby.accept";

    public static final String FETCH_LOBBY_CREATED = "/topic/lobby.created";
    public static final String FETCH_LOBBY_DESTROYED = "/topic/lobby.{lobby_id}.destroyed";
    public static final String FETCH_LOBBY_UPDATED = "/topic/lobby.{lobby_id}.updated";
    public static final String FETCH_LOBBY_ACCEPTED = "/topic/lobby.accepted";

    public LobbyService lobbyService;
    public WebSocketUserService userService;
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping(value = API_INIT_LOBBY)
    @SendToUser(FETCH_LOBBY_CREATED)
    public LobbyDto fetchLobbyCreated() {
        Lobby lobby = lobbyService.initLobby(SimpAttributesContextHolder.currentAttributes().getSessionId());
        return Mapper.toLobbyDto(lobby);
    }

    @MessageMapping(value = API_DESTROY_LOBBY)
    public void fetchLobbyDestroyed() {
        Lobby lobby = lobbyService.destroyLobby(SimpAttributesContextHolder.currentAttributes().getSessionId());
        LobbyDto lobbyDto = Mapper.toLobbyDto(lobby);
        messagingTemplate.convertAndSend(
                LobbyDestinationHelper.makeDestination(FETCH_LOBBY_DESTROYED, lobbyDto.getId()),
                lobbyDto
        );
    }

    public void fetchLobbyUpdated(LobbyDto lobbyDto) {
        messagingTemplate.convertAndSend(
                LobbyDestinationHelper.makeDestination(FETCH_LOBBY_UPDATED, lobbyDto.getId()),
                lobbyDto
        );
    }

    @MessageMapping(value = API_ACCEPT_LOBBY)
    @SendToUser(value = FETCH_LOBBY_ACCEPTED)
    public LobbyDto acceptLobby(@Payload String lobbyId) {
        Lobby lobby = lobbyService.acceptLobby(SimpAttributesContextHolder.currentAttributes().getSessionId(), lobbyId);
        LobbyDto lobbyDto = Mapper.toLobbyDto(lobby);
        fetchLobbyUpdated(lobbyDto);
        return lobbyDto;
    }

    protected static class LobbyDestinationHelper {
        static final String KEY = "{lobby_id}";

        static String makeDestination(String destination, String lobbyId) {
            return destination.replace(KEY, lobbyId);
        }
    }
}
