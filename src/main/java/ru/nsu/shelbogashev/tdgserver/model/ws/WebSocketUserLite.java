package ru.nsu.shelbogashev.tdgserver.model.ws;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebSocketUserLite implements Serializable {
    String sessionId;
    String username;
    @Builder.Default
    Status status = Status.IN_MENU;
}
