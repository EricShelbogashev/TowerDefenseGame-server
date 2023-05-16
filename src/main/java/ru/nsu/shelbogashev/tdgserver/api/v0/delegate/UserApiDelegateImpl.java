package ru.nsu.shelbogashev.tdgserver.api.v0.delegate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.shelbogashev.tdgserver.FIXED_exception.TowerDefenseException;
import ru.nsu.shelbogashev.tdgserver.generated.api.UserApiDelegate;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.*;
import ru.nsu.shelbogashev.tdgserver.model.rest.User;
import ru.nsu.shelbogashev.tdgserver.model.ws.Status;
import ru.nsu.shelbogashev.tdgserver.model.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.FIXED_security.jwt.JwtTokenProvider;
import ru.nsu.shelbogashev.tdgserver.server.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.server.dto.ResponseFactory;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.service.FriendshipService;
import ru.nsu.shelbogashev.tdgserver.service.UserService;
import ru.nsu.shelbogashev.tdgserver.service.LobbyService;
import ru.nsu.shelbogashev.tdgserver.service.WebSocketUserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.nsu.shelbogashev.tdgserver.FIXED_message.ResponseMessage.*;

@RestController
@Slf4j
public class UserApiDelegateImpl implements UserApiDelegate {
    private final UserService userService;
    private final WebSocketUserService webSocketUserService;
    private final FriendshipService friendshipService;
    private final LobbyService lobbyService;

    @Autowired
    public UserApiDelegateImpl(UserService userService, WebSocketUserService webSocketUserService, FriendshipService friendshipService, LobbyService lobbyService) {
        this.userService = userService;
        this.webSocketUserService = webSocketUserService;
        this.friendshipService = friendshipService;
        this.lobbyService = lobbyService;
    }

    @Override
    public ResponseEntity<List<UserInfoResponse>> getFriends() {
        return ResponseEntity.ok(Mapper.toFriendResponse(getAllFriends()));
    }

    @Override
    public ResponseEntity<List<WebsocketUserInfoResponse>> getFriendsOnline() {
        List<User> allFriends = getAllFriends();
        List<WebSocketUser> onlineFriends = allFriends.stream().map(
                it -> webSocketUserService.getWebSocketUserByUsername(it.getUsername())
        ).filter(Objects::nonNull).toList();

        List<WebsocketUserInfoResponse> responses = onlineFriends.stream().map(it -> {
            WebsocketUserInfoResponse response = new WebsocketUserInfoResponse();
            response.setStatus(it.getStatus().name());
            response.setUsername(it.getUsername());
            response.setSessionId(it.getSessionId());
            return response;
        }).toList();
        return ResponseEntity.ok(responses);
    }

    private List<User> getAllFriends() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userService.findByUsername(principal.getUsername());
        if (user.isEmpty()) throw new TowerDefenseException(UNEXPECTED_ERROR);
        return friendshipService.getAllFriends(user.get());
    }

    @Override
    public ResponseEntity<LobbyCreateResponse> lobbyCreate() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WebSocketUser webSocketUser = webSocketUserService.getWebSocketUserByUsername(principal.getUsername());
        Lobby lobby = lobbyService.initLobby(webSocketUser.getSessionId());
        return ResponseEntity.ok(Mapper.toLobbyCreateResponse(lobby));
    }

    @Override
    public ResponseEntity<Void> lobbyAccept(LobbyRequest lobbyRequest) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WebSocketUser webSocketUser = webSocketUserService.getWebSocketUserByUsername(principal.getUsername());
        webSocketUser.setLobbyId(lobbyRequest.getId());
        webSocketUser.setStatus(Status.IN_LOBBY);
        webSocketUserService.setWebSocketUser(webSocketUser);
        lobbyService.acceptLobby(webSocketUser.getSessionId(), lobbyRequest.getId());
        return ResponseEntity.ok(ResponseFactory.EMPTY);
    }

    @Override
    public ResponseEntity<Void> lobbyDestroy() {
        return UserApiDelegate.super.lobbyDestroy();
    }
}