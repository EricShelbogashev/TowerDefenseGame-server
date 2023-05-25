package ru.nsu.shelbogashev.tdgserver.service;

import ru.nsu.shelbogashev.tdgserver.server.ws.WebSocketUser;

public interface WebsocketService {
    void connectedTo(WebSocketUser user);

    void disconnectedFrom(WebSocketUser user);
}
