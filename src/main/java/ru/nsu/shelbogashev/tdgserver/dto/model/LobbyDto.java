package ru.nsu.shelbogashev.tdgserver.dto.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LobbyDto implements Serializable {

    String id;

    Long createdAt;

    String adminSessionId;
}