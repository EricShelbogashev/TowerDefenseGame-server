package ru.nsu.shelbogashev.tdgserver.service.impl;

import lombok.extern.log4j.Log4j2;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.server.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.service.WebsocketService;
import ru.nsu.shelbogashev.tdgserver.service.handler.OnLobbyUpdateHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/*
 * Order priority for locks:
 *   0. User
 *   1. Lobby
 * All related entities are required to be locked in the order specified above,
 * if necessary, make changes to one of them.
 *
 * */

@Log4j2
@Service
public class WebsocketServiceImpl implements WebsocketService {
    private final static long DAYS_UNTIL_DESTROY = 7;
    private final RedissonClient redissonClient;
    private final OnLobbyUpdateHandler onLobbyUpdateHandler;

    public WebsocketServiceImpl(RedissonClient redissonClient, @Lazy OnLobbyUpdateHandler onLobbyUpdateHandler) {
        this.redissonClient = redissonClient;
        this.onLobbyUpdateHandler = onLobbyUpdateHandler;
    }

    @Override
    public void connectedTo(WebSocketUser user) {
        RLock lock = redissonClient.getFairLock(user.getUsername());
        lock.lock();
        try {
            // TODO убрать ----------------
            if (user.getUsername().equals("user1")) {
                Lobby lobby = user.createLobby();
                lobby.setId("123");
                user.setLobbyId("123");
                setLobby(lobby);
            } else {
                user.joinLobby("123");
                Lobby lobby = getLobby("123");
                List<String> members = lobby.getMembers();
                members.add(user.getUsername());
                setLobby(lobby);
            }
            // ----------------------------

            setUser(user);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void disconnectedFrom(WebSocketUser user) {
        RLock userLock = redissonClient.getFairLock(user.getUsername());
        userLock.lock();
        user = getUser(user.getUsername());

        // If user is not lobby member.
        if (user.getLobbyId() == null) {
            log.info("disconnectedFrom() : not lobby member brunch : user" + user);
            try {
                popUser(user.getUsername());
            } finally {
                userLock.unlock();
            }
            return;
        }

        // If user is lobby member.
        RLock lobbyLock = redissonClient.getFairLock(user.getLobbyId());
        lobbyLock.lock();
        Lobby lobby = getLobby(user.getLobbyId());
        List<RLock> locks = lobby.getMembers().stream().map(redissonClient::getFairLock).toList();
        locks.forEach(Lock::lock);

        try {
            // If user is admin of lobby.
            if (lobby.getAdminUsername().equals(user.getUsername())) {
                log.info("disconnectedFrom() : lobby admin brunch : user" + user);
                lobby.getMembers().stream()
                        .map(this::getUser)
                        .filter(it -> it.getLobbyId() != null)
                        .peek(WebSocketUser::leaveLobby)
                        .forEach(this::setUser);
                popLobby(lobby.getId());
                return;
            }

            // If user isn't admin of lobby.
            log.info("disconnectedFrom() : lobby member brunch : user" + user);
            lobby.removeMember(user.getUsername());
            setLobby(lobby);
        } finally {
            popUser(user.getUsername());
            lobbyLock.unlock();
            userLock.unlock();
            locks.forEach(Lock::unlock);
            onLobbyUpdateHandler.handleLobbyUpdate(lobby);
        }
    }





    private void setUser(WebSocketUser user) {
        String key = UserKeyHelper.makeKey(user.getUsername());
        set(key, user);
    }

    private <T> void set(String key, T value) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value, DAYS_UNTIL_DESTROY, TimeUnit.DAYS);
    }

    private void pop(String key) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.delete();
    }

    private void setLobby(Lobby lobby) {
        String key = LobbyKeyHelper.makeKey(lobby.getId());
        set(key, lobby);
    }

    private void popUser(String username) {
        String key = UserKeyHelper.makeKey(username);
        pop(key);
    }

    private void popLobby(String lobbyId) {
        String key = LobbyKeyHelper.makeKey(lobbyId);
        pop(key);
    }

    private WebSocketUser getUser(String username) {
        String key = UserKeyHelper.makeKey(username);
        RBucket<WebSocketUser> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    private Lobby getLobby(String lobbyId) {
        String key = LobbyKeyHelper.makeKey(lobbyId);
        RBucket<Lobby> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    private static class UserKeyHelper {
        private static final String KEY = "tdgserver:user:";

        public static String makeKey(String username) {
            return KEY + username;
        }
    }

    private static class LobbyKeyHelper {
        private static final String KEY = "tdgserver:lobby:";

        public static String makeKey(String lobbyId) {
            return KEY + lobbyId;
        }
    }
}
