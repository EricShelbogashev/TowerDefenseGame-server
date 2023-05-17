package ru.nsu.shelbogashev.tdgserver.towerdefense.dto;

import org.jetbrains.annotations.NotNull;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.Entity;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.Road;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.TowerCreate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapper {
    public static @NotNull FieldDto toFieldDto(@NotNull HashMap<String, Road> data, Entity guildhall) {
        Map<String, List<EntityDto>> result = new HashMap<>();
        data.forEach((identifier, road) -> result.put(identifier, Mapper.toRoadDto(road)));
        return FieldDto.builder()
                .guildhall(Mapper.toEntityDto(guildhall))
                .roads(result)
                .build();
    }

    public static @NotNull List<EntityDto> toRoadDto(@NotNull Road data) {
        return data.getEntities().stream()
                .map(Mapper::toEntityDto)
                .toList();
    }

    public static @NotNull EntityDto toEntityDto(@NotNull Entity data) {
        return EntityDto.builder()
                .id(String.valueOf(data.getId()))
                .cell(data.getCell())
                .name(data.getName())
                .health(data.getHealth())
                .build();
    }

    public static @NotNull TowerCreate toTowerCreate(@NotNull TowerCreateDto data) {
        return TowerCreate.builder()
                .sessionId(data.getSessionId())
                .towerName(data.getTowerName())
                .cellNumber(data.getCellNumber())
                .build();
    }
}
