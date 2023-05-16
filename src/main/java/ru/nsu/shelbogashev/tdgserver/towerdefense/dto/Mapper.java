package ru.nsu.shelbogashev.tdgserver.towerdefense.dto;

import org.jetbrains.annotations.NotNull;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.Entity;
import ru.nsu.shelbogashev.tdgserver.towerdefense.model.Road;

import java.util.HashMap;
import java.util.Map;

public class Mapper {
    public static @NotNull FieldDto toFieldDto(@NotNull HashMap<String, Road> data) {
        Map<String, RoadDto> result = new HashMap<>();
        data.forEach((identifier, road) -> result.put(identifier, Mapper.toRoadDto(road)));
        return new FieldDto(result);
    }

    public static @NotNull RoadDto toRoadDto(@NotNull Road data) {
        return new RoadDto(data.getEntities().stream()
                .map(Mapper::toEntityDto)
                .toList());
    }

    public static @NotNull EntityDto toEntityDto(@NotNull Entity data) {
        return EntityDto.builder()
                .id(data.getId())
                .cell(data.getCell())
                .name(data.getName())
                .health(data.getHealth())
                .build();
    }
}
