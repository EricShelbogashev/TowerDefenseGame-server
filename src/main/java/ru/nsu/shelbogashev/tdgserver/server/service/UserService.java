package ru.nsu.shelbogashev.tdgserver.server.service;

import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.server.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllFriends(User user);

    Lobby inviteFriend(String sessionId);

    String inviteFriendGetSession(String friendUsername);
}
