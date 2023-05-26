package ru.nsu.shelbogashev.tdgserver.api.v0.websocket;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.nsu.shelbogashev.tdgserver.api.API;
import ru.nsu.shelbogashev.tdgserver.server.dto.ResponseFactory;
import ru.nsu.shelbogashev.tdgserver.server.model.GameStart;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.server.service.GameService;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.dto.TDMapper;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.dto.TowerCreateDto;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.model.Entity;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.model.OnFieldUpdateListener;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.model.OnGameEndListener;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.model.Road;

import java.util.Map;

// TODO: illegal access (state) handle.
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller("GameControllerBean")
public class GameController implements OnFieldUpdateListener, OnGameEndListener {
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping(value = API.GAME.API_GAME_START)
    public void gameStartHandle() {
        String sessionId = SimpAttributesContextHolder.currentAttributes().getSessionId();

        GameStart gameStart = gameService.createGame(sessionId);
        messagingTemplate.convertAndSend(
                API.GAME.getTopicGameStart(gameStart.getLobby().getId()),
                TDMapper.toGameStartDto(gameStart)
        );
    }

    @MessageMapping(value = API.STOMP.API_TOWER_CREATE)
    public void towerCreateHandle(@Payload TowerCreateDto towerCreateDto) {
        log.info("towerCreateHandle() : " + towerCreateDto);

        String sessionId = SimpAttributesContextHolder.currentAttributes().getSessionId();
        gameService.createTower(sessionId, towerCreateDto);
    }

    @Override
    public void updated(Lobby lobby, Map<String, Road> data, Entity guildhall) {
        log.info("updated() : lobby.getId()=%s".formatted(lobby.getId()));
        messagingTemplate.convertAndSend(
                API.GAME.getTopicFieldReceived(lobby.getId()),
                TDMapper.toFieldDto(data, guildhall)
        );
    }

    @Override
    public void end(Lobby lobby) {
        log.info("end() : lobby.getId()=%s".formatted(lobby.getId()));
        messagingTemplate.convertAndSend(
                API.GAME.getTopicFieldReceived(lobby.getId()),
                ResponseFactory.getGameEnd()
        );
    }
}
