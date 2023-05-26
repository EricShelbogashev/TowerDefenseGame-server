package ru.nsu.shelbogashev.tdgserver.server.service.handler;

import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;

public interface OnLobbyUpdateHandler {
    void handleLobbyUpdate(Lobby lobby);
}
