package ru.nsu.shelbogashev.tdgserver.server.towerdefense.model;

import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;

import java.util.Map;

public interface OnFieldUpdateListener {
    void updated(Lobby lobby, Map<String, Road> data, Entity guildhall);
}
