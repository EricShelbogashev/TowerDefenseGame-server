package ru.nsu.shelbogashev.tdgserver.server.dto;

import org.jetbrains.annotations.NotNull;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.*;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;
import ru.nsu.shelbogashev.tdgserver.server.model.User;
import ru.nsu.shelbogashev.tdgserver.server.security.jwt.JwtUser;
import ru.nsu.shelbogashev.tdgserver.server.model.ws.WebSocketUser;

import java.util.HashMap;
import java.util.Map;

public class Mapper {
    public static @NotNull LobbyDto toLobbyDto(@NotNull Lobby lobby) {
        LobbyDto lobbyDto = new LobbyDto();
        lobbyDto.setId(lobby.getId());
        lobbyDto.setAdminUsername(lobby.getAdminUsername());
        lobbyDto.setMembers(lobby.getMembers());
        lobbyDto.setCreatedAt(lobby.getCreatedAt().toString());
        return lobbyDto;
    }

    public static @NotNull WebSocketUserDto toWebSocketUserDto(@NotNull WebSocketUser user) {
        WebSocketUserDto webSocketUserDto = new WebSocketUserDto();
        webSocketUserDto.setSessionId(user.getSessionId());
        webSocketUserDto.setUsername(user.getUsername());
        webSocketUserDto.setStatus(user.getStatus().toString());
        return webSocketUserDto;
    }

    public static @NotNull User toUser(@NotNull AuthDto user) {
        return User.builder()
                .id(-1L)
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    public static @NotNull User toUser(@NotNull JwtUser user) {
        return User.builder()
                .id(user.id())
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    public static @NotNull UserDto toUserDto(@NotNull User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        return dto;
    }
/*
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
    }*/

    public static Map<String, Object> toMessageJsonMap(String message) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", message);
        return map;
    }

    public static @NotNull WebSocketUser toWebSocketUser(@NotNull JwtUser user, @NotNull String sessionId) {
        return WebSocketUser.builder()
                .username(user.getUsername())
                .sessionId(sessionId)
                .build();
    }

    public static Lobby toLobby(LobbyDto lobbyRequest) {
        return Lobby.builder()
                .id(lobbyRequest.getId())
                .createdAt(Long.valueOf(lobbyRequest.getCreatedAt()))
                .adminUsername(lobbyRequest.getAdminUsername())
                .members(lobbyRequest.getMembers())
                .build();
    }
}
