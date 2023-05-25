package ru.nsu.shelbogashev.tdgserver.api.v0.websocket;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.nsu.shelbogashev.tdgserver.api.v0.delegate.AuthenticatedController;
import ru.nsu.shelbogashev.tdgserver.server.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.server.security.jwt.JwtUser;
import ru.nsu.shelbogashev.tdgserver.server.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.service.WebsocketService;
import ru.nsu.shelbogashev.tdgserver.service.handler.OnLobbyUpdateHandler;

@Log4j2
@Controller
public class WebsocketController extends AuthenticatedController implements OnLobbyUpdateHandler {
    //    public static final String API_ONLINE_FRIENDS = "/api/friend.online.all";
//    public static final String API_INVITE_FRIEND = "/api/lobby.invite.friend";
//    public static final String FETCH_ONLINE_FRIENDS = "/topic/friend.online.all";
//    public static final String FETCH_INVITE_FRIEND = "/topic/lobby.invitation";
//    public static final String FETCH_LOBBY_UPDATED = "/topic/lobby.{lobby_id}.updated";
//    private WebSocketUserService webSocketUserService;
//    private final RedisTemplate<String, Object> redisTemplate;
//    private LobbyService lobbyService;
//    private RedisUserLock userLock;
    private final WebsocketService websocketService;

    public WebsocketController(WebsocketService websocketService, UserDetailsService userDetailsService) {
        super(userDetailsService);
        this.websocketService = websocketService;
    }

    @EventListener
    public void userJoinedToServer(SessionConnectedEvent event) {
        WebSocketUser user = getUserFromEvent(event);
        if (user == null) {
            log.info("userJoinedToServer() : failed to load user, check upper log");
            return;
        }

        log.info("userJoinedToServer() : user=" + user);
        websocketService.connectedTo(user);
    }

    @EventListener
    public void userDisconnectedFromServer(SessionDisconnectEvent event) {
        WebSocketUser user = getUserFromEvent(event);

        if (user == null) {
            log.info("userJoinedToServer() : failed to load user, check upper log");
            return;
        }

        log.info("userDisconnectedFromServer() : user=" + user);
        websocketService.disconnectedFrom(user);
    }

    private WebSocketUser getUserFromEvent(AbstractSubProtocolEvent event) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) event.getUser();
        if (authentication == null) {
            log.error("getUser() : authentication is null, filter might be broken : event=" + event);
            return null;
        }

        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();

        if (jwtUser == null) {
            log.error("getUser() : user is null, unexpected incident : event=" + event);
            return null;
        }

        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");

        if (sessionId == null) {
            log.error("getUser() : simpSessionId in headers is null, filter might be broken : event.getMessage()=" + event.getMessage());
            return null;
        }

        return Mapper.toWebSocketUser(jwtUser, sessionId);
    }

    @Override
    public void handleLobbyUpdate(Lobby lobby) {

    }

//    @MessageMapping(value = API_ONLINE_FRIENDS)
//    @SendToUser(FETCH_ONLINE_FRIENDS)
//    public List<WebSocketUserLite> fetchOnlineFriends() {
//        String sessionId = SimpAttributesContextHolder.currentAttributes().getSessionId();
//        log.info("fetchOnlineFriends() : sessionId=" + sessionId);
//        List<WebSocketUser> friends = webSocketUserService.getOnlineFriends(sessionId);
//        List<WebSocketUserLite> list = friends.stream().map(Mapper::toWebSocketUserLite).toList();
//        log.info("fetchOnlineFriends() : sessionId=" + sessionId + " got friends=" + list);
//        return list;
//    }
//
//    @MessageMapping(value = API_INVITE_FRIEND)
//    public void fetchInviteFriend(@Payload String friendUsername) {
//        log.info("fetchInviteFriend() : friendUsername=" + friendUsername);
//        Lobby lobby = lobbyService.inviteFriend(SimpAttributesContextHolder.currentAttributes().getSessionId());
//        LobbyDto lobbyDto = Mapper.toLobbyDto(lobby);
//
//        messagingTemplate.convertAndSendToUser(friendUsername, FETCH_INVITE_FRIEND, lobbyDto);
//    }
//
//    private void fetchLobbyUpdated(LobbyDto lobbyDto) {
//        messagingTemplate.convertAndSend(
//                LobbyDestinationHelper.makeDestination(FETCH_LOBBY_UPDATED, lobbyDto.getId()),
//                lobbyDto
//        );
//    }
}
