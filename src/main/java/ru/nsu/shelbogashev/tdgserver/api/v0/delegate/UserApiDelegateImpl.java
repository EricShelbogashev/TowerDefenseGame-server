package ru.nsu.shelbogashev.tdgserver.api.v0.delegate;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.shelbogashev.tdgserver.generated.api.UserApiDelegate;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.UserDto;
import ru.nsu.shelbogashev.tdgserver.server.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.server.security.JwtUserDetailsService;
import ru.nsu.shelbogashev.tdgserver.service.UserService;

import java.util.List;

@Log4j2
@RestController
public class UserApiDelegateImpl extends AuthenticatedController implements UserApiDelegate {
    private final UserService userService;

    @Autowired
    public UserApiDelegateImpl(UserService userService, JwtUserDetailsService userDetailsService) {
        super(userDetailsService);
        this.userService = userService;
    }

    @Override
    public ResponseEntity<List<UserDto>> getFriends() {
        List<UserDto> users = userService.getAllFriends(getCurrentUser()).stream().map(Mapper::toUserDto).toList();
        return ResponseEntity.ok(users);
    }

//    @Override
//    public ResponseEntity<List<WebsocketUserInfoResponse>> getFriendsOnline() {
//        List<User> allFriends = getAllFriends();
//        List<WebSocketUser> onlineFriends = allFriends.stream().map(
//                it -> webSocketUserService.getWebSocketUserByUsername(it.getUsername())
//        ).filter(Objects::nonNull).toList();
//
//        List<WebsocketUserInfoResponse> responses = onlineFriends.stream().map(it -> {
//            WebsocketUserInfoResponse response = new WebsocketUserInfoResponse();
//            response.setStatus(it.getStatus().name());
//            response.setUsername(it.getUsername());
//            response.setSessionId(it.getSessionId());
//            return response;
//        }).toList();
//        return ResponseEntity.ok(responses);
//    }
//
//
//    @Override
//    public ResponseEntity<LobbyDto> lobbyCreate() {
//        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        WebSocketUser webSocketUser = webSocketUserService.getWebSocketUserByUsername(principal.getUsername());
//        Lobby lobby = lobbyService.initLobby(webSocketUser.getSessionId());
//        return ResponseEntity.ok(Mapper.toLobbyDto(lobby));
//    }
//
//    @Override
//    public ResponseEntity<Void> lobbyAccept(LobbyDto lobbyRequest) {
//        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        WebSocketUser webSocketUser = webSocketUserService.getWebSocketUserByUsername(principal.getUsername());
//        webSocketUser.setLobbyId(lobbyRequest.getId());
//        webSocketUser.setStatus(Status.IN_LOBBY);
//        webSocketUserService.setWebSocketUser(webSocketUser);
//        lobbyService.acceptLobby(webSocketUser.getSessionId(), lobbyRequest.getId());
//        return ResponseEntity.ok(ResponseFactory.EMPTY);
//    }
//
//    @Override
//    public ResponseEntity<Void> lobbyDestroy() {
//        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        WebSocketUser webSocketUser = webSocketUserService.getWebSocketUserByUsername(principal.getUsername());
//        Lobby lobby = lobbyService.getLobby(webSocketUser.getLobbyId());
//        lobbyService.destroyLobby(webSocketUser.getLobbyId());
//        fetchLobbyDestroyed(Mapper.toLobbyDto(lobby));
//        return null;
//    }
//
//    private void fetchLobbyDestroyed(LobbyDto lobbyDto) {
//        messagingTemplate.convertAndSend(
//                LobbyDestinationHelper.makeDestination(FETCH_LOBBY_DESTROYED, lobbyDto.getId()),
//                lobbyDto
//        );
//    }
}
