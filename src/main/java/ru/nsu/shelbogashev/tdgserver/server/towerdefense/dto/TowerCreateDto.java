package ru.nsu.shelbogashev.tdgserver.server.towerdefense.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TowerCreateDto {
    String sessionId;
    String towerName;
    int cellNumber;
}
