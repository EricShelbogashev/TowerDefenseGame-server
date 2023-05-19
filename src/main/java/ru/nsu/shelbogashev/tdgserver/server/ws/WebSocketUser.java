package ru.nsu.shelbogashev.tdgserver.server.ws;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebSocketUser implements Serializable {
    String sessionId;
    String username;
    @Builder.Default
    Status status = Status.IN_MENU;
    @Builder.Default
    String lobbyId = null;
}
