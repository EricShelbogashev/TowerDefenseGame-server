package ru.nsu.shelbogashev.tdgserver.service.handler;

import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;

public interface OnLobbyDestroyHandler {
    void handleLobbyDestroy(Lobby lobby);
}
