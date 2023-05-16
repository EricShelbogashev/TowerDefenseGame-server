package ru.nsu.shelbogashev.tdgserver.server.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lobby {
    String id;

    Long createdAt;

    String adminSessionId;

    @Builder.Default
    List<String> members = new ArrayList<>();
}