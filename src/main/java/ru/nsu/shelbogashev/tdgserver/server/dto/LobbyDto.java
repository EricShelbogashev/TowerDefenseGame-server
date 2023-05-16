package ru.nsu.shelbogashev.tdgserver.server.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LobbyDto {
    String id;

    Long createdAt;

    String adminSessionId;

    @Builder.Default
    List<String> members = new ArrayList<>();
}