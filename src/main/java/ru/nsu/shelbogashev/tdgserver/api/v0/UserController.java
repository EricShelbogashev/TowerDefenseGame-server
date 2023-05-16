package ru.nsu.shelbogashev.tdgserver.api.v0;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.nsu.shelbogashev.tdgserver.FIXED_exception.TowerDefenseException;
import ru.nsu.shelbogashev.tdgserver.model.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.model.ws.WebSocketUserLite;
import ru.nsu.shelbogashev.tdgserver.server.dto.LobbyDto;
import ru.nsu.shelbogashev.tdgserver.server.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.service.LobbyService;
import ru.nsu.shelbogashev.tdgserver.service.RedisUserLock;
import ru.nsu.shelbogashev.tdgserver.service.WebSocketUserService;

import java.util.List;
import java.util.Objects;

import static ru.nsu.shelbogashev.tdgserver.FIXED_message.ResponseMessage.UNEXPECTED_ERROR;

// TODO: FIX API!!!
@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class UserController {
    private WebSocketUserService webSocketUserService;
    private LobbyService lobbyService;
    private RedisUserLock userLock;
    private LobbyController lobbyController;
    public static final String API_ONLINE_FRIENDS = "api/friend.online.all";
    public static final String API_INVITE_FRIEND = "api/lobby.invite.friend";
    public static final String FETCH_ONLINE_FRIENDS = "topic/friend.online.all";
    public static final String FETCH_INVITE_FRIEND = "topic/lobby.invitation";
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void userJoinedToServer(SessionConnectedEvent event) {
        WebSocketUser webSocketUser = WebSocketUser.builder()
                .username(Objects.requireNonNull(event.getUser()).getName())
                .sessionId(SimpAttributesContextHolder.currentAttributes().getSessionId())
                .build();
        webSocketUserService.setWebSocketUser(webSocketUser);
    }

    @EventListener
    public void userDisconnectedFromServer(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        WebSocketUser webSocketUser = webSocketUserService.getWebSocketUserBySessionId(sessionId);

        if (webSocketUser == null) throw new TowerDefenseException(UNEXPECTED_ERROR);
        if (webSocketUser.getLobbyId() == null) {
            webSocketUserService.popWebSocketUser(sessionId);
            return;
        }

        Lobby lobby = lobbyService.destroyLobbyByAdmin(webSocketUser);
        synchronized (userLock) {
            webSocketUserService.popWebSocketUser(sessionId);
        }
        if (!sessionId.equals(lobby.getAdminSessionId())) {
            lobbyController.fetchLobbyUpdated(Mapper.toLobbyDto(lobby));
        }
    }

    @MessageMapping(value = API_ONLINE_FRIENDS)
    @SendToUser(FETCH_ONLINE_FRIENDS)
    public List<WebSocketUserLite> fetchOnlineFriends() {
        String sessionId = SimpAttributesContextHolder.currentAttributes().getSessionId();
        log.info("fetchOnlineFriends() : sessionId=" + sessionId);
        List<WebSocketUser> friends = webSocketUserService.getOnlineFriends(sessionId);
        List<WebSocketUserLite> list = friends.stream().map(Mapper::toWebSocketUserLite).toList();
        log.info("fetchOnlineFriends() : sessionId=" + sessionId + " got friends=" + list);
        return list;
    }

    @MessageMapping(value = API_INVITE_FRIEND)
    public void fetchInviteFriend(@Payload String friendUsername) {
        log.info("fetchInviteFriend() : friendUsername=" + friendUsername);
        Lobby lobby = lobbyService.inviteFriend(SimpAttributesContextHolder.currentAttributes().getSessionId());
        LobbyDto lobbyDto = Mapper.toLobbyDto(lobby);

        messagingTemplate.convertAndSendToUser(friendUsername, FETCH_INVITE_FRIEND, lobbyDto);
    }

}
