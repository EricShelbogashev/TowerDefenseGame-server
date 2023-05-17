package ru.nsu.shelbogashev.tdgserver.api.v0;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.nsu.shelbogashev.tdgserver.model.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.server.dto.GameStartDto;
import ru.nsu.shelbogashev.tdgserver.server.dto.ResponseFactory;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.service.GameService;
import ru.nsu.shelbogashev.tdgserver.service.LobbyService;
import ru.nsu.shelbogashev.tdgserver.service.WebSocketUserService;
import ru.nsu.shelbogashev.tdgserver.towerdefense.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.towerdefense.dto.TowerCreateDto;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.Entity;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.OnFieldUpdateListener;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.OnGameEndListener;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.Road;

import java.util.HashMap;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class GameController implements OnFieldUpdateListener, OnGameEndListener {
    private WebSocketUserService webSocketUserService;
    private LobbyService lobbyService;
    private GameService gameService = new GameService(this, this);
    public static final String API_GAME_START = "/api/game.start";
    public static final String API_TOWER_CREATE = "/api/game.create.tower";
    public static final String TOPIC_GAME_START = "/topic/game.{lobby_id}.start";
    public static final String TOPIC_FIELD_RECEIVED = "/topic/game.field.{lobby_id}.received";
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping(value = API_GAME_START)
    public void gameStartHandle() {
        WebSocketUser webSocketUser = webSocketUserService.getWebSocketUserBySessionId(SimpAttributesContextHolder.currentAttributes().getSessionId());
        Lobby lobby = lobbyService.getLobby(webSocketUser.getLobbyId());
        log.info("gameStartHandle() : webSocketUser.getLobbyId()=%s, lobby.getId()=%s".formatted(webSocketUser.getLobbyId(), lobby.getId()));
        gameService.createGame(lobby);
        // TODO: IN_GAME
        messagingTemplate.convertAndSend(
                GameDestinationHelper.makeDestination(TOPIC_GAME_START, lobby.getId()),
                GameStartDto.builder().length(10).build()
        );
    }

    @MessageMapping(value = API_TOWER_CREATE)
    public void towerCreateHandle(@Payload TowerCreateDto towerCreateDto) {
        log.info("towerCreateHandle() : " + towerCreateDto);
        WebSocketUser webSocketUser = webSocketUserService.getWebSocketUserBySessionId(SimpAttributesContextHolder.currentAttributes().getSessionId());
        Lobby lobby = lobbyService.getLobby(webSocketUser.getLobbyId());
        gameService.createTower(webSocketUser, lobby, towerCreateDto);
    }

    @Override
    public void updated(Lobby lobby, HashMap<String, Road> data, Entity guildhall) {
        log.info("updated() : lobby.getId()=%s".formatted(lobby.getId()));
        messagingTemplate.convertAndSend(
                GameDestinationHelper.makeDestination(TOPIC_FIELD_RECEIVED, lobby.getId()),
                Mapper.toFieldDto(data, guildhall)
        );
    }

    @Override
    public void end(Lobby lobby) {
        log.info("end() : lobby.getId()=%s".formatted(lobby.getId()));
        messagingTemplate.convertAndSend(
                GameDestinationHelper.makeDestination(TOPIC_FIELD_RECEIVED, lobby.getId()),
                ResponseFactory.getGameEnd()
        );
    }

    public static class GameDestinationHelper {
        static final String KEY = "{lobby_id}";

        public static String makeDestination(String destination, String lobbyId) {
            return destination.replace(KEY, lobbyId);
        }
    }
}
