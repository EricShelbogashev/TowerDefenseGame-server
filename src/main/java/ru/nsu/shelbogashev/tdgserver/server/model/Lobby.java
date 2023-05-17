package ru.nsu.shelbogashev.tdgserver.server.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lobby {
    @Builder.Default
    String id = String.valueOf(UUID.randomUUID());

    @Builder.Default
    Long createdAt = System.currentTimeMillis();

    String adminSessionId;

    @Builder.Default
    List<String> members = new ArrayList<>();
}