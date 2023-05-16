package ru.nsu.shelbogashev.tdgserver.towerdefense.model;

import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;

public interface OnGameEndListener {
    void end(Lobby lobby);
}
