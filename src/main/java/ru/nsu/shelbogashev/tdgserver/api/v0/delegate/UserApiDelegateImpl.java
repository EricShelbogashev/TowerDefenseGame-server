package ru.nsu.shelbogashev.tdgserver.api.v0.delegate;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.shelbogashev.tdgserver.generated.api.UserApiDelegate;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.LobbyDto;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.UserDto;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.WebSocketUserDto;
import ru.nsu.shelbogashev.tdgserver.server.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.server.model.User;
import ru.nsu.shelbogashev.tdgserver.server.model.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.server.security.JwtUserDetailsService;
import ru.nsu.shelbogashev.tdgserver.service.UserService;
import ru.nsu.shelbogashev.tdgserver.service.WebsocketService;

import java.util.List;

@Log4j2
@RestController
public class UserApiDelegateImpl extends AuthenticatedController implements UserApiDelegate {
    private final UserService userService;
    private final WebsocketService websocketService;

    @Autowired
    public UserApiDelegateImpl(UserService userService, JwtUserDetailsService userDetailsService, WebsocketService websocketService) {
        super(userDetailsService);
        this.userService = userService;
        this.websocketService = websocketService;
    }

    @Override
    public ResponseEntity<List<UserDto>> getFriends() {
        List<UserDto> users = userService.getAllFriends(getCurrentUser()).stream().map(Mapper::toUserDto).toList();
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<List<WebSocketUserDto>> getFriendsOnline() {
        List<WebSocketUser> onlineFriends = websocketService.getOnlineFriends(getCurrentUser());
        List<WebSocketUserDto> response = onlineFriends.stream().map(Mapper::toWebSocketUserDto).toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LobbyDto> lobbyCreate() {
        User user = getCurrentUser();
        Lobby lobby = websocketService.createLobby(user);
        return ResponseEntity.ok(Mapper.toLobbyDto(lobby));
    }

    @Override
    public ResponseEntity<Void> lobbyAccept(LobbyDto lobbyRequest) {
        User user = getCurrentUser();
        websocketService.acceptLobby(user, Mapper.toLobby(lobbyRequest));
        return null;
    }

    @Override
    public ResponseEntity<Void> lobbyDestroy() {
        User user = getCurrentUser();
        websocketService.destroyLobby(user);
        return null;
    }
}
