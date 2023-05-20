package ru.nsu.shelbogashev.tdgserver.api.v0.delegate;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.shelbogashev.tdgserver.generated.api.UserApiDelegate;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.LobbyDto;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.UserInfoResponse;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.WebsocketUserInfoResponse;
import ru.nsu.shelbogashev.tdgserver.server.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.server.dto.ResponseFactory;
import ru.nsu.shelbogashev.tdgserver.server.exception.TowerDefenseException;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.server.rest.User;
import ru.nsu.shelbogashev.tdgserver.server.ws.Status;
import ru.nsu.shelbogashev.tdgserver.server.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.service.FriendshipService;
import ru.nsu.shelbogashev.tdgserver.service.LobbyService;
import ru.nsu.shelbogashev.tdgserver.service.UserService;
import ru.nsu.shelbogashev.tdgserver.service.WebSocketUserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage.UNEXPECTED_ERROR;

@Log4j2
@RestController
public class UserApiDelegateImpl implements UserApiDelegate {
    public static final String FETCH_LOBBY_DESTROYED = "/topic/lobby.{lobby_id}.destroyed";
    private final UserService userService;
    private final WebSocketUserService webSocketUserService;
    private final FriendshipService friendshipService;
    private final LobbyService lobbyService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public UserApiDelegateImpl(UserService userService, WebSocketUserService webSocketUserService, FriendshipService friendshipService, LobbyService lobbyService, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.webSocketUserService = webSocketUserService;
        this.friendshipService = friendshipService;
        this.lobbyService = lobbyService;
        this.messagingTemplate = messagingTemplate;
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
    public ResponseEntity<LobbyDto> lobbyCreate() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WebSocketUser webSocketUser = webSocketUserService.getWebSocketUserByUsername(principal.getUsername());
        Lobby lobby = lobbyService.initLobby(webSocketUser.getSessionId());
        return ResponseEntity.ok(Mapper.toLobbyDto(lobby));
    }

    @Override
    public ResponseEntity<Void> lobbyAccept(LobbyDto lobbyRequest) {
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
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WebSocketUser webSocketUser = webSocketUserService.getWebSocketUserByUsername(principal.getUsername());
        Lobby lobby = lobbyService.getLobby(webSocketUser.getLobbyId());
        lobbyService.destroyLobby(webSocketUser.getLobbyId());
        fetchLobbyDestroyed(Mapper.toLobbyDto(lobby));
        return null;
    }

    private void fetchLobbyDestroyed(LobbyDto lobbyDto) {
        messagingTemplate.convertAndSend(
                LobbyDestinationHelper.makeDestination(FETCH_LOBBY_DESTROYED, lobbyDto.getId()),
                lobbyDto
        );
    }
}
