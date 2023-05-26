package ru.nsu.shelbogashev.tdgserver.server.service.impl;

import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.nsu.shelbogashev.tdgserver.server.model.GameStart;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.server.model.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.server.repository.RedisRepository;
import ru.nsu.shelbogashev.tdgserver.server.service.GameService;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.dto.TDMapper;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.dto.TowerCreateDto;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.model.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

@Log4j2
@Service
public class GameServiceImpl implements GameService, OnGameEndListener, OnFieldUpdateListener {
    private final RedissonClient redissonClient;
    private final OnFieldUpdateListener updateListener;
    private final OnGameEndListener endListener;
    private final Map<String, GameLoop> gameLoops = new ConcurrentHashMap<>();
    private final RedisRepository redisRepository;

    public GameServiceImpl(
            RedissonClient redissonClient,
            @Qualifier("GameControllerBean") @Lazy OnFieldUpdateListener updateListener,
            @Qualifier("GameControllerBean") @Lazy OnGameEndListener endListener,
            RedisRepository redisRepository
    ) {
        this.redissonClient = redissonClient;
        this.updateListener = updateListener;
        this.endListener = endListener;
        this.redisRepository = redisRepository;
    }

    public GameStart createGame(String sessionId) {
        WebSocketUser user = redisRepository.getUserBySessionId(sessionId);

        RLock userLock = redissonClient.getFairLock(user.getUsername());
        userLock.lock();
        WebSocketUser webSocketUser = redisRepository.getUser(user.getUsername());
        RLock lobbyLock = redissonClient.getFairLock(webSocketUser.getLobbyId());
        lobbyLock.lock();
        Lobby lobby = redisRepository.getLobby(webSocketUser.getLobbyId());
        List<RLock> locks = lobby.getMembers().stream().map(redissonClient::getFairLock).toList();
        locks.forEach(Lock::lock);

        try {
            GameLoop gameSession = new GameLoop(lobby, this, this);
            gameLoops.put(lobby.getId(), gameSession);

            lobby.getMembers(true).stream()
                    .map(redisRepository::getUser)
                    .filter(it -> it.getLobbyId() != null)
                    .peek(WebSocketUser::joinGame)
                    .forEach(redisRepository::setUser);

            gameSession.start();
            return GameStart.builder()
                    .length(Field.DEFAULT_ROAD_LENGTH)
                    .lobby(lobby)
                    .build();
        } finally {
            lobbyLock.unlock();
            locks.forEach(Lock::unlock);
            userLock.unlock();
        }
    }

    @Override
    public void createTower(String sessionId, TowerCreateDto createDto) {
        WebSocketUser user = redisRepository.getUserBySessionId(sessionId);
        Lobby lobby = redisRepository.getLobby(user.getLobbyId());

        gameLoops.get(lobby.getId()).createTower(user.getUsername(), TDMapper.toTowerCreate(createDto));
    }

    @Override
    public void updated(Lobby lobby, Map<String, Road> data, Entity guildhall) {
        updateListener.updated(lobby, data, guildhall);
    }

    @Override
    public void end(Lobby lobby) {
        gameLoops.remove(lobby.getId());
        endListener.end(lobby);
        endGame(lobby);
    }

    private void endGame(Lobby lobby) {
        RLock lobbyLock = redissonClient.getFairLock(lobby.getId());
        lobby = redisRepository.getLobby(lobby.getId());
        List<RLock> locks = lobby.getMembers(true).stream().map(redissonClient::getFairLock).toList();
        locks.forEach(Lock::lock);

        try {
            lobby.getMembers(true).stream()
                    .map(redisRepository::getUser)
                    .filter(it -> it.getLobbyId() != null)
                    .peek(WebSocketUser::leaveGame)
                    .forEach(redisRepository::setUser);
        } finally {
            lobbyLock.unlock();
            locks.forEach(Lock::unlock);
        }
    }
}
