package ru.nsu.shelbogashev.tdgserver.service;

import com.badlogic.gdx.utils.ObjectMap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.nsu.shelbogashev.tdgserver.model.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.towerdefense.dto.TowerCreateDto;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.GameLoop;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.OnFieldUpdateListener;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.OnGameEndListener;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.Road;

import java.util.HashMap;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameService implements OnGameEndListener, OnFieldUpdateListener {
    OnFieldUpdateListener updateListener;

    public GameService(OnFieldUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    ObjectMap<String, GameLoop> gameLoops = new ObjectMap<>();

    public void createGame(Lobby lobby) {
        GameLoop gameSession = new GameLoop(lobby, this, this);
        gameLoops.put(lobby.getId(), gameSession);
        gameSession.start();
    }

    public void createTower(WebSocketUser user, Lobby lobby, TowerCreateDto createDto) {
//        gameLoops.get(lobby.getId()).createTower(user.getSessionId(), createDto.getX());
    }

    @Override
    public void updated(Lobby lobby, HashMap<String, Road> data) {
        updateListener.updated(lobby, data);
    }

    @Override
    public void end(Lobby lobby) {
        gameLoops.remove(lobby.getId());
    }
}
