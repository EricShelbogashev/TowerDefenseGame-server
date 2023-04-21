package ru.nsu.shelbogashev.tdgserver.model.ws;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lobby {

    @Builder.Default
    String id = UUID.randomUUID().toString();

    @Builder.Default
    Long createdAt = Instant.now().toEpochMilli();

    String adminSessionId;
}
