package ru.nsu.shelbogashev.tdgserver.towerdefense.dto;

import java.util.HashMap;
import java.util.Map;

public class FieldDto extends HashMap<String, RoadDto> {
    public FieldDto(Map<? extends String, ? extends RoadDto> m) {
        super(m);
    }
}
