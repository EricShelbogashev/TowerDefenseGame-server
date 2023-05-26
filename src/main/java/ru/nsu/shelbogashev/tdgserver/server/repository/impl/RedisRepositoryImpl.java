package ru.nsu.shelbogashev.tdgserver.server.repository.impl;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.server.model.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.server.repository.RedisRepository;

import java.util.concurrent.TimeUnit;

/* TODO: wrap all methods that use these operations in the context of synchronization in a special method
    with a lock by username and lobby, and pass runnable as an argument. */
@Component
public class RedisRepositoryImpl implements RedisRepository {
    final static long DAYS_UNTIL_DESTROY = 7;

    private final RedissonClient redissonClient;

    public RedisRepositoryImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void setUser(WebSocketUser user) {
        setUserSessionIdTranslation(user.getSessionId(), user.getUsername());
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

    @Override
    public void setLobby(Lobby lobby) {
        String key = LobbyKeyHelper.makeKey(lobby.getId());
        set(key, lobby);
    }

    @Override
    public void popUser(WebSocketUser user) {
        popUserSessionIdTranslation(user.getSessionId());
        String key = UserKeyHelper.makeKey(user.getUsername());
        pop(key);
    }

    @Override
    public void popLobby(String lobbyId) {
        String key = LobbyKeyHelper.makeKey(lobbyId);
        pop(key);
    }

    @Override
    public WebSocketUser getUserBySessionId(String sessionId) {
        String key = UserKeyHelper.makeKeyBySessionId(sessionId);
        RBucket<String> bucket = redissonClient.getBucket(key);
        return getUser(bucket.get());
    }

    private void setUserSessionIdTranslation(String sessionId, String username) {
        String key = UserKeyHelper.makeKeyBySessionId(sessionId);
        set(key, username);
    }

    private void popUserSessionIdTranslation(String sessionId) {
        String key = UserKeyHelper.makeKeyBySessionId(sessionId);
        pop(key);
    }

    @Override
    public WebSocketUser getUser(String username) {
        String key = UserKeyHelper.makeKey(username);
        RBucket<WebSocketUser> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    @Override
    public Lobby getLobby(String lobbyId) {
        String key = LobbyKeyHelper.makeKey(lobbyId);
        RBucket<Lobby> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    static class UserKeyHelper {
        private static final String KEY = "tdgserver:user:";
        private static final String SESSION_KEY = "tdgserver:session:";

        public static String makeKey(String username) {
            return KEY + username;
        }

        public static String makeKeyBySessionId(String sessionId) {
            return SESSION_KEY + sessionId;
        }
    }

    static class LobbyKeyHelper {
        private static final String KEY = "tdgserver:lobby:";

        public static String makeKey(String lobbyId) {
            return KEY + lobbyId;
        }
    }

}
