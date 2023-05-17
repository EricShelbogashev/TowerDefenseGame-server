package ru.nsu.shelbogashev.tdgserver.towerdefense.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TowerCreate {
    String sessionId;
    String towerName;
    int cellNumber;
}
