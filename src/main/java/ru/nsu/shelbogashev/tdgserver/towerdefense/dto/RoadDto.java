package ru.nsu.shelbogashev.tdgserver.towerdefense.dto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class RoadDto extends ArrayList<EntityDto> {
    public RoadDto(@NotNull Collection<? extends EntityDto> c) {
        super(c);
    }
}
