package ru.nsu.shelbogashev.tdgserver.server.repository;

import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.server.model.ws.WebSocketUser;

public interface RedisRepository {
    void setUser(WebSocketUser user);

    void setLobby(Lobby lobby);

    WebSocketUser getUser(String username);

    Lobby getLobby(String lobbyId);

    void popUser(WebSocketUser user);

    void popLobby(String lobbyId);

    WebSocketUser getUserBySessionId(String sessionId);
}
