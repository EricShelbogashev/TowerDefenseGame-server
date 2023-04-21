package ru.nsu.shelbogashev.tdgserver.dto;

import ru.nsu.shelbogashev.tdgserver.dto.model.AuthResponseDto;
import ru.nsu.shelbogashev.tdgserver.dto.model.LobbyDto;
import ru.nsu.shelbogashev.tdgserver.dto.model.MessageDto;
import ru.nsu.shelbogashev.tdgserver.dto.model.UserRequestDto;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.AuthResponse;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.Message;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.UserInfoResponse;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.UserRequest;
import ru.nsu.shelbogashev.tdgserver.model.rest.User;
import ru.nsu.shelbogashev.tdgserver.model.ws.Lobby;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapper {
    public static AuthResponse toAuthResponse(AuthResponseDto response) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.token(response.token());
        return authResponse;
    }

    public static Message toMessage(MessageDto response) {
        Message message = new Message();
        message.message(response.getMessage());
        return message;
    }

    public static Map<String, Object> toMessageJsonMap(MessageDto response) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", response.getMessage());
        return map;
    }

    // TODO: рефактор под ломбок билдеры.
    public static List<UserInfoResponse> toFriendResponse(List<User> friends) {
        return friends.stream().map(it -> {
            UserInfoResponse response = new UserInfoResponse();
            response.setNickname(it.getUsername());
            return response;
        }).toList();
    }

    public static UserRequestDto toUserRequestDto(UserRequest response) {
        return UserRequestDto.builder()
                .username(response.getUsername())
                .password(response.getPassword())
                .build();
    }

    public static LobbyDto toLobbyDto(Lobby lobby) {
        return LobbyDto.builder()
                .id(lobby.getId())
                .createdAt(lobby.getCreatedAt())
                .adminSessionId(lobby.getAdminSessionId())
                .build();
    }

    public static User toUser(UserRequest user) {
        return User.builder()
                .id(null)
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

}
