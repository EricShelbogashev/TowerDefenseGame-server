package ru.nsu.shelbogashev.tdgserver.server.dto;

import ru.nsu.shelbogashev.tdgserver.generated.api.dto.AuthDto;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.MessageDto;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.TokenDto;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.dto.EntityDto;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.dto.FieldDto;

import java.util.HashMap;

public class ResponseFactory {

    public static TokenDto toTokenDto(String token) {
        TokenDto response = new TokenDto();
        response.setToken(token);
        return response;
    }

    public static MessageDto toMessage(String message) {
        MessageDto dto = new MessageDto();
        dto.setMessage(message);
        return dto;
    }

    public static AuthDto toAuthDto(String username, String password) {
        AuthDto dto = new AuthDto();
        dto.setUsername(username);
        dto.setPassword(password);
        return dto;
    }

    public static FieldDto getGameEnd() {
        return FieldDto.builder()
                .guildhall(
                        EntityDto.builder()
                                .id("0")
                                .cell(0)
                                .name("guildhall")
                                .build())
                .roads(new HashMap<>())
                .build();
    }
}
