package ru.nsu.shelbogashev.tdgserver.dto.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.nsu.shelbogashev.tdgserver.model.ws.Status;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebSocketUserDto {
    String sessionId;
    String username;
    Status status;
    String lobbyId;
}