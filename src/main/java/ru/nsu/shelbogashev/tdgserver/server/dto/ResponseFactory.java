package ru.nsu.shelbogashev.tdgserver.server.dto;

import ru.nsu.shelbogashev.tdgserver.generated.api.dto.AuthResponse;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.Message;
import ru.nsu.shelbogashev.tdgserver.towerdefense.dto.EntityDto;
import ru.nsu.shelbogashev.tdgserver.towerdefense.dto.FieldDto;

import java.util.HashMap;

public class ResponseFactory {
    public static Void EMPTY = null;

    public static AuthResponse getAuthResponse(String token) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        return response;
    }

    public static Message getMessage(String message) {
        Message response = new Message();
        response.setMessage(message);
        return response;
    }

    public static FieldDto getGameEnd() {
        return FieldDto.builder()
                .guildhall(
                        EntityDto.builder()
                                .cell(0)
                                .id("0")
                                .name("guildhall")
                                .build())
                .roads(new HashMap<>())
                .build();
    }
}
