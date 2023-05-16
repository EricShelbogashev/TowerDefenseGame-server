package ru.nsu.shelbogashev.tdgserver.towerdefense.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TowerCreateDto {
    String sessionId;
    String towerName;
    int cellNumber;
}
