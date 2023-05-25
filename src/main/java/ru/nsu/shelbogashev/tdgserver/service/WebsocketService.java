package ru.nsu.shelbogashev.tdgserver.service;

import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.server.model.User;
import ru.nsu.shelbogashev.tdgserver.server.model.ws.WebSocketUser;

import java.util.List;

public interface WebsocketService {
    void connectedTo(WebSocketUser webSocketUser);

    void disconnectedFrom(WebSocketUser webSocketUser);

    List<WebSocketUser> getOnlineFriends(User user);

    Lobby createLobby(User user);

    void destroyLobby(User user);

    void acceptLobby(User user, Lobby lobby);
}
