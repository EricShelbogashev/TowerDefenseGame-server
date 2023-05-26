package ru.nsu.shelbogashev.tdgserver.server.service.impl;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.nsu.shelbogashev.tdgserver.server.exception.InternalServerException;
import ru.nsu.shelbogashev.tdgserver.server.exception.UserException;
import ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.server.model.User;
import ru.nsu.shelbogashev.tdgserver.server.model.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.server.repository.RedisRepository;
import ru.nsu.shelbogashev.tdgserver.server.service.UserService;
import ru.nsu.shelbogashev.tdgserver.server.service.WebsocketService;
import ru.nsu.shelbogashev.tdgserver.server.service.handler.OnLobbyUpdateHandler;

import java.util.List;
import java.util.Objects;
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
    private final RedissonClient redissonClient;
    private final OnLobbyUpdateHandler onLobbyUpdateHandler;
    private final UserService userService;
    private final RedisRepository redisRepository;

    public WebsocketServiceImpl(
            RedissonClient redissonClient,
            @Lazy OnLobbyUpdateHandler onLobbyUpdateHandler,
            UserService userService,
            RedisRepository redisRepository
    ) {
        this.redissonClient = redissonClient;
        this.onLobbyUpdateHandler = onLobbyUpdateHandler;
        this.userService = userService;
        this.redisRepository = redisRepository;
    }

    @Override
    public void connectedTo(WebSocketUser user) {
        RLock lock = redissonClient.getFairLock(user.getUsername());
        lock.lock();
        try {
            redisRepository.setUser(user);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void disconnectedFrom(WebSocketUser user) {
        RLock userLock = redissonClient.getFairLock(user.getUsername());
        userLock.lock();
        user = redisRepository.getUser(user.getUsername());

        // If user is not lobby member.
        if (user.getLobbyId() == null) {
            log.info("disconnectedFrom() : not lobby member brunch : user" + user);
            try {
                redisRepository.popUser(user);
            } finally {
                userLock.unlock();
            }
            return;
        }

        // If user is lobby member.
        RLock lobbyLock = redissonClient.getFairLock(user.getLobbyId());
        lobbyLock.lock();
        Lobby lobby = redisRepository.getLobby(user.getLobbyId());

        try {
            // If user is admin of lobby.
            if (lobby.getAdminUsername().equals(user.getUsername())) {
                log.info("disconnectedFrom() : lobby admin brunch : user" + user);
                destroyLobby(lobby);
                return;
            }

            // If user isn't admin of lobby.
            log.info("disconnectedFrom() : lobby member brunch : user" + user);
            lobby.removeMember(user.getUsername());
            redisRepository.setLobby(lobby);
        } finally {
            redisRepository.popUser(user);
            lobbyLock.unlock();
            userLock.unlock();
            onLobbyUpdateHandler.handleLobbyUpdate(lobby);
        }
    }

    @Override
    public List<WebSocketUser> getOnlineFriends(User user) {
        List<User> friends = userService.getAllFriends(user);
        return friends.stream()
                .map(User::getUsername)
                .map(redisRepository::getUser)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public @NotNull Lobby createLobby(User user) {
        RLock userLock = redissonClient.getFairLock(user.getUsername());
        userLock.lock();
        WebSocketUser webSocketUser = redisRepository.getUser(user.getUsername());
        if (webSocketUser.getLobbyId() != null) {
            userLock.unlock();
            throw new UserException(ResponseMessage.SYSTEM_ALREADY_HAS_LOBBY_ERROR);
        }

        Lobby lobby = webSocketUser.createLobby();
        redisRepository.setLobby(lobby);
        redisRepository.setUser(webSocketUser);
        userLock.unlock();
        return lobby;
    }

    @Override
    public void destroyLobby(User user) {
        RLock userLock = redissonClient.getFairLock(user.getUsername());
        userLock.lock();
        WebSocketUser webSocketUser = redisRepository.getUser(user.getUsername());
        RLock lobbyLock = redissonClient.getFairLock(webSocketUser.getLobbyId());
        lobbyLock.lock();
        Lobby lobby = redisRepository.getLobby(webSocketUser.getLobbyId());
        try {
            destroyLobby(lobby);
        } finally {
            lobbyLock.unlock();
            userLock.unlock();
        }
    }

    // To invoke in a lock on lobby and lobby admin.
    private void destroyLobby(Lobby lobby) {
        List<RLock> locks = lobby.getMembers().stream().map(redissonClient::getFairLock).toList();
        locks.forEach(Lock::lock);

        try {
            lobby.getMembers(true).stream()
                    .map(redisRepository::getUser)
                    .filter(it -> it.getLobbyId() != null)
                    .peek(WebSocketUser::leaveLobby)
                    .forEach(redisRepository::setUser);
            redisRepository.popLobby(lobby.getId());
        } finally {
            locks.forEach(Lock::unlock);
        }
    }

    @Override
    public void acceptLobby(User user, Lobby lobby) {
        RLock userLock = redissonClient.getFairLock(user.getUsername());
        userLock.lock();
        WebSocketUser webSocketUser = redisRepository.getUser(user.getUsername());
        if (webSocketUser == null) {
            throw new InternalServerException("acceptLobby() : webSocketUser is null, database is might be broken");
        }

        if (webSocketUser.getLobbyId() != null) {
            userLock.unlock();
            throw new UserException(ResponseMessage.USER_IS_ALREADY_A_LOBBY_MEMBER);
        }

        RLock lobbyLock = redissonClient.getFairLock(lobby.getId());
        try {
            lobbyLock.lock();
            lobby = redisRepository.getLobby(lobby.getId());

            webSocketUser.joinLobby(lobby);

            redisRepository.setUser(webSocketUser);
            redisRepository.setLobby(lobby);
        } finally {
            lobbyLock.unlock();
            userLock.unlock();
        }
    }
}
