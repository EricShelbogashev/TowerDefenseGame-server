package ru.nsu.shelbogashev.tdgserver.validation.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.nsu.shelbogashev.tdgserver.model.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.service.ws.WebSocketUserService;
import ru.nsu.shelbogashev.tdgserver.validation.SpecificTopicSubscriptionInitValidator;

import static ru.nsu.shelbogashev.tdgserver.api.v0.ws.LobbyController.FETCH_LOBBY_DESTROYED;

@Component
public class LobbyDestroyedTopicValidator implements SpecificTopicSubscriptionInitValidator {
    final WebSocketUserService userService;
    private static int pathIdStartInx = FETCH_LOBBY_DESTROYED.indexOf("{");
    private static String pathStart = FETCH_LOBBY_DESTROYED.substring(0, FETCH_LOBBY_DESTROYED.indexOf("{"));
    private static String pathEnd = FETCH_LOBBY_DESTROYED.substring(FETCH_LOBBY_DESTROYED.indexOf("}") + 1);

    public LobbyDestroyedTopicValidator(@Lazy WebSocketUserService userService) {
        this.userService = userService;
    }
    /* TODO: Сделать валидатор менее зависимым от topic path
        путем вынесения информации о топике в отдельный класс и валидации там же.
     */

    @Override
    public String destination() {
        // TODO: get user destination prefix.
        return "/reply" + FETCH_LOBBY_DESTROYED;
    }

    @Override
    public Boolean validate(String sessionId, String topicDestination) {
        if (!topicDestination.startsWith(pathStart) || !topicDestination.endsWith(pathEnd)) return null;

        WebSocketUser webSocketUser = userService.getWebSocketUser(sessionId);
        String lobbyIdFromTopic = topicDestination.substring(pathIdStartInx, topicDestination.length() - pathEnd.length());
        return lobbyIdFromTopic.equals(webSocketUser.getLobbyId());
    }
}