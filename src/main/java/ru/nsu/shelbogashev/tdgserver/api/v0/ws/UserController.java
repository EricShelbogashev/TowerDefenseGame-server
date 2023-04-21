package ru.nsu.shelbogashev.tdgserver.api.v0.ws;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import ru.nsu.shelbogashev.tdgserver.model.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.service.ws.WebSocketUserService;

import java.io.PrintStream;
import java.security.Principal;
import java.util.Objects;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class UserController {
    private WebSocketUserService webSocketUserService;

    @EventListener
    public void userJoinedToServer(SessionConnectedEvent event) {
        WebSocketUser webSocketUser = WebSocketUser.builder()
                .username(Objects.requireNonNull(event.getUser()).getName())
                .sessionId(SimpAttributesContextHolder.currentAttributes().getSessionId())
                .build();
        webSocketUserService.addWebSocketUser(webSocketUser);
    }
}
