package ru.nsu.shelbogashev.tdgserver.server.dto;

import ru.nsu.shelbogashev.tdgserver.generated.api.dto.LobbyCreateResponse;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.Message;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.UserInfoResponse;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.UserRequest;
import ru.nsu.shelbogashev.tdgserver.model.rest.User;
import ru.nsu.shelbogashev.tdgserver.model.ws.WebSocketUser;
import ru.nsu.shelbogashev.tdgserver.model.ws.WebSocketUserLite;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapper {
    public static LobbyDto toLobbyDto(Lobby lobby) {
        return LobbyDto.builder()
                .id(lobby.getId())
                .createdAt(lobby.getCreatedAt())
                .adminSessionId(lobby.getAdminSessionId())
                .members(lobby.getMembers())
                .build();
    }

    public static WebSocketUserLite toWebSocketUserLite(WebSocketUser user) {
        return WebSocketUserLite.builder()
                .username(user.getUsername())
                .sessionId(user.getSessionId())
                .status(user.getStatus())
                .build();
    }

    public static User toUser(UserRequest user) {
        return User.builder()
                .id(-1L)
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    public static UserInfoResponse toUserInfoResponse(User user) {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setUsername(user.getUsername());
        return userInfoResponse;
    }

    public static List<UserInfoResponse> toFriendResponse(List<User> friends) {
        return friends.stream().map(Mapper::toUserInfoResponse).toList();
    }

    public static LobbyCreateResponse toLobbyCreateResponse(Lobby lobby) {
        LobbyCreateResponse response = new LobbyCreateResponse();
        response.setLobbyId(lobby.getId());
        return response;
    }

    public static MessageDto toMessageDto(Message message) {
        return MessageDto.builder()
                .message(message.getMessage())
                .build();
    }

    public static Map<String, Object> toMessageJsonMap(String message) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", message);
        return map;
    }
}
