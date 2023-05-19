package ru.nsu.shelbogashev.tdgserver.service;

import com.badlogic.gdx.utils.ObjectMap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import ru.nsu.shelbogashev.tdgserver.server.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.towerdefense.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.towerdefense.dto.TowerCreateDto;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.*;

import java.util.HashMap;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameService implements OnGameEndListener, OnFieldUpdateListener {
    OnFieldUpdateListener updateListener;
    OnGameEndListener endListener;

    public GameService(OnFieldUpdateListener updateListener, OnGameEndListener endListener) {
        this.updateListener = updateListener;
        this.endListener = endListener;
    }

    ObjectMap<String, GameLoop> gameLoops = new ObjectMap<>();

    public void createGame(Lobby lobby) {
        GameLoop gameSession = new GameLoop(lobby, this, this);
        gameLoops.put(lobby.getId(), gameSession);
        gameSession.start();
    }

    public void createTower(WebSocketUser user, Lobby lobby, TowerCreateDto createDto) {
        gameLoops.get(lobby.getId()).createTower(user.getSessionId(), Mapper.toTowerCreate(createDto));
    }

    @Override
    public void updated(Lobby lobby, HashMap<String, Road> data, Entity guildhall) {
        updateListener.updated(lobby, data, guildhall);
    }

    @Override
    public void end(Lobby lobby) {
        gameLoops.remove(lobby.getId());
        endListener.end(lobby);
    }
}
