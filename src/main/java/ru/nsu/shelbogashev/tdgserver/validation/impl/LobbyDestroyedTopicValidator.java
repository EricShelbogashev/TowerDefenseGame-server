package ru.nsu.shelbogashev.tdgserver.validation.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.nsu.shelbogashev.tdgserver.server.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.service.WebSocketUserService;
import ru.nsu.shelbogashev.tdgserver.validation.SpecificTopicSubscriptionInitValidator;

import static ru.nsu.shelbogashev.tdgserver.api.v0.delegate.UserApiDelegateImpl.FETCH_LOBBY_DESTROYED;

@Component
public class LobbyDestroyedTopicValidator implements SpecificTopicSubscriptionInitValidator {
    final WebSocketUserService userService;
    private static final int pathIdStartInx = FETCH_LOBBY_DESTROYED.indexOf("{");
    private static final String pathStart = FETCH_LOBBY_DESTROYED.substring(0, FETCH_LOBBY_DESTROYED.indexOf("{"));
    private static final String pathEnd = FETCH_LOBBY_DESTROYED.substring(FETCH_LOBBY_DESTROYED.indexOf("}") + 1);

    public LobbyDestroyedTopicValidator(@Lazy WebSocketUserService userService) {
        this.userService = userService;
    }
    /* TODO: Make the validator less dependent on the topic path by extracting topic information into a separate class and performing validation there. */

    @Override
    public String destination() {
        // TODO: get user destination prefix.
        return "/reply" + FETCH_LOBBY_DESTROYED;
    }

    @Override
    public Boolean validate(String sessionId, String topicDestination) {
        if (!topicDestination.startsWith(pathStart) || !topicDestination.endsWith(pathEnd)) return null;

        WebSocketUser webSocketUser = userService.getWebSocketUserBySessionId(sessionId);
        String lobbyIdFromTopic = topicDestination.substring(pathIdStartInx, topicDestination.length() - pathEnd.length());
        return lobbyIdFromTopic.equals(webSocketUser.getLobbyId());
    }
}
