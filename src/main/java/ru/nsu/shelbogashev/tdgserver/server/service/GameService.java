package ru.nsu.shelbogashev.tdgserver.server.service;

import ru.nsu.shelbogashev.tdgserver.server.model.GameStart;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.dto.TowerCreateDto;

public interface GameService {
    GameStart createGame(String sessionId);

    void createTower(String sessionId, TowerCreateDto towerCreateDto);
}
