package ru.nsu.shelbogashev.tdgserver.server.towerdefense.dto;

import org.jetbrains.annotations.NotNull;
import ru.nsu.shelbogashev.tdgserver.server.model.GameStart;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.model.Entity;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.model.Road;
import ru.nsu.shelbogashev.tdgserver.server.towerdefense.model.TowerCreate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TDMapper {
    public static @NotNull FieldDto toFieldDto(@NotNull Map<String, Road> data, Entity guildhall) {
        Map<String, List<EntityDto>> result = new HashMap<>();
        data.forEach((identifier, road) -> result.put(identifier, TDMapper.toRoadDto(road)));
        return FieldDto.builder()
                .guildhall(TDMapper.toEntityDto(guildhall))
                .roads(result)
                .build();
    }

    public static @NotNull List<EntityDto> toRoadDto(@NotNull Road data) {
        return data.getEntities().stream()
                .map(TDMapper::toEntityDto)
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

    public static GameStartDto toGameStartDto(GameStart gameStart) {
        return GameStartDto.builder()
                .length(gameStart.getLength())
                .build();
    }
}
