package ru.nsu.shelbogashev.tdgserver.server.towerdefense.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class FieldDto {
    EntityDto guildhall;
    Map<String, List<EntityDto>> roads;
}