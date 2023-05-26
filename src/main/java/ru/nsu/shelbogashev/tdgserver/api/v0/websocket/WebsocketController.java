package ru.nsu.shelbogashev.tdgserver.api.v0.websocket;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.nsu.shelbogashev.tdgserver.api.API;
import ru.nsu.shelbogashev.tdgserver.api.v0.ApiHelper;
import ru.nsu.shelbogashev.tdgserver.api.v0.delegate.AuthenticatedController;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.LobbyDto;
import ru.nsu.shelbogashev.tdgserver.server.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.server.model.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.server.security.jwt.JwtUser;
import ru.nsu.shelbogashev.tdgserver.server.service.UserService;
import ru.nsu.shelbogashev.tdgserver.server.service.WebsocketService;
import ru.nsu.shelbogashev.tdgserver.server.service.handler.OnLobbyDestroyHandler;
import ru.nsu.shelbogashev.tdgserver.server.service.handler.OnLobbyUpdateHandler;

@Log4j2
@Controller
public class WebsocketController extends AuthenticatedController implements OnLobbyUpdateHandler, OnLobbyDestroyHandler {
    private final WebsocketService websocketService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    public WebsocketController(WebsocketService websocketService, UserDetailsService userDetailsService, SimpMessagingTemplate messagingTemplate, UserService userService) {
        super(userDetailsService);
        this.websocketService = websocketService;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
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

    @MessageMapping(value = API.USER.API_INVITE_FRIEND)
    public void fetchInviteFriend(@Payload String friendUsername) {
        log.info("fetchInviteFriend() : friendUsername=" + friendUsername);

        Lobby lobby = userService.inviteFriend(SimpAttributesContextHolder.currentAttributes().getSessionId());
        String user = userService.inviteFriendGetSession(friendUsername);

        LobbyDto lobbyDto = Mapper.toLobbyDto(lobby);
        messagingTemplate.convertAndSendToUser(user, API.USER.FETCH_INVITE_FRIEND, lobbyDto);
    }

    @Override
    public void handleLobbyUpdate(Lobby lobby) {
        LobbyDto lobbyDto = Mapper.toLobbyDto(lobby);
        messagingTemplate.convertAndSend(
                ApiHelper.makeLobbyDestination(API.STOMP.FETCH_LOBBY_DESTROYED, lobbyDto.getId()),
                lobbyDto
        );
    }

    @Override
    public void handleLobbyDestroy(Lobby lobby) {
        LobbyDto lobbyDto = Mapper.toLobbyDto(lobby);
        messagingTemplate.convertAndSend(
                ApiHelper.makeLobbyDestination(API.STOMP.FETCH_LOBBY_DESTROYED, lobbyDto.getId()),
                lobbyDto
        );
    }
}
