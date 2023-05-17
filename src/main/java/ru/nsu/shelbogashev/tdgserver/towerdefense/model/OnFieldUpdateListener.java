package ru.nsu.shelbogashev.tdgserver.towerdefense.model;

import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;

import java.util.HashMap;

public interface OnFieldUpdateListener {
    void updated(Lobby lobby, HashMap<String, Road> data, Entity guildhall);
}
