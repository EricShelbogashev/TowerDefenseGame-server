package ru.nsu.shelbogashev.tdgserver.api.v0;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.nsu.shelbogashev.tdgserver.model.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.server.dto.GameStartDto;
import ru.nsu.shelbogashev.tdgserver.towerdefense.dto.TowerCreateDto;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.service.GameService;
import ru.nsu.shelbogashev.tdgserver.service.LobbyService;
import ru.nsu.shelbogashev.tdgserver.service.WebSocketUserService;
import ru.nsu.shelbogashev.tdgserver.towerdefense.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.OnFieldUpdateListener;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.Road;

import java.util.HashMap;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class GameController implements OnFieldUpdateListener {
    private WebSocketUserService webSocketUserService;
    private LobbyService lobbyService;
    private GameService gameService = new GameService(this);
    public static final String API_GAME_START = "/api/game.start";
    public static final String API_TOWER_CREATE = "/api/game.create.tower";
    public static final String TOPIC_GAME_START = "/topic/game.{lobby_id}.start";
    public static final String TOPIC_FIELD_RECEIVED = "/topic/game.field.{lobby_id}.received";
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping(value = API_GAME_START)
    public void gameStartHandle() {
        WebSocketUser webSocketUser = webSocketUserService.getWebSocketUserBySessionId(SimpAttributesContextHolder.currentAttributes().getSessionId());
        Lobby lobby = lobbyService.getLobby(webSocketUser.getLobbyId());
        gameService.createGame(lobby);
        messagingTemplate.convertAndSend(
                GameDestinationHelper.makeDestination(TOPIC_GAME_START, lobby.getId()),
                GameStartDto.builder().build()
        );
    }

    @MessageMapping(value = API_TOWER_CREATE)
    public void towerCreateHandle(@Payload TowerCreateDto towerCreateDto) {
        WebSocketUser webSocketUser = webSocketUserService.getWebSocketUserBySessionId(SimpAttributesContextHolder.currentAttributes().getSessionId());
        Lobby lobby = lobbyService.getLobby(webSocketUser.getLobbyId());
        gameService.createTower(webSocketUser, lobby, towerCreateDto);
    }

    @Override
    public void updated(Lobby lobby, HashMap<String, Road> data) {
        messagingTemplate.convertAndSend(
                GameDestinationHelper.makeDestination(TOPIC_FIELD_RECEIVED, lobby.getId()),
                Mapper.toFieldDto(data)
        );
    }

    public static class GameDestinationHelper {
        static final String KEY = "{lobby_id}";

        public static String makeDestination(String destination, String lobbyId) {
            return destination.replace(KEY, lobbyId);
        }
    }
}
