package ru.nsu.shelbogashev.tdgserver.api.v0.ws;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.nsu.shelbogashev.tdgserver.dto.model.LobbyDto;
import ru.nsu.shelbogashev.tdgserver.service.ws.LobbyService;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class LobbyController {
    public static final String FETCH_LOBBY_CREATED = "/topic/lobby.created";
    public static final String API_INIT_LOBBY = "/api/lobby.init";
    public LobbyService lobbyService;

    @MessageMapping(value = API_INIT_LOBBY)
    @SendToUser(FETCH_LOBBY_CREATED)
    public LobbyDto fetchLobbyCreated() {
        return lobbyService.initLobby(SimpAttributesContextHolder.currentAttributes().getSessionId());
    }
}
