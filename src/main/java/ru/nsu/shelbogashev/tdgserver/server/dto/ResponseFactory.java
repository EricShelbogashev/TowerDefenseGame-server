package ru.nsu.shelbogashev.tdgserver.server.dto;

import ru.nsu.shelbogashev.tdgserver.generated.api.dto.AuthResponse;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.Message;

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
}
