package ru.nsu.shelbogashev.tdgserver.server.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EntityDto implements Serializable {
    String name;
    int health;
    String playerSessionId;
    int x;
}
