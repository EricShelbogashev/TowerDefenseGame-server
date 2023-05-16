package ru.nsu.shelbogashev.tdgserver.towerdefense.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EntityDto {
    long id;
    String name;
    int cell;
    int health;
}
