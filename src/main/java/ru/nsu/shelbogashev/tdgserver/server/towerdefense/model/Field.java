package ru.nsu.shelbogashev.tdgserver.server.towerdefense.model;

import lombok.extern.log4j.Log4j2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class Field {
    public static int DEFAULT_ROAD_LENGTH = 10;
    private final OnGameEndListenerNative listener;
    private final Entity guildhall;
    Map<String, Road> roads;

    public Field(List<String> identifiers, OnGameEndListenerNative listener) {
        roads = new HashMap<>(identifiers.size());
//        roads = Collections.synchronizedMap(roads);
        this.listener = listener;
        guildhall = Entities.DEFAULT_GUILDHALL.toEntity();
        identifiers.forEach(it -> roads.put(it, new Road(DEFAULT_ROAD_LENGTH, it)));
    }

    public Map<String, Road> update() {
        log.info("update()");
        roads.values().forEach(Road::updatePosition);
        int summaryDamage = roads.values().stream().mapToInt(Road::updateHealth).sum();
        guildhall.acceptDamage(summaryDamage);
        if (!guildhall.isAlive()) {
            listener.end();
        }
        return roads;
    }

    public Map<String, Road> getRoads() {
        log.info("getRoads()");
        return roads;
    }

    public Entity getGuildhall() {
        log.info("getGuildhall()");
        return guildhall;
    }
}
