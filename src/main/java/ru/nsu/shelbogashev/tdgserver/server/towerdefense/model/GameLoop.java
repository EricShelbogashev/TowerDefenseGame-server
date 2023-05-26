package ru.nsu.shelbogashev.tdgserver.server.towerdefense.model;

import lombok.extern.log4j.Log4j2;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Log4j2
public class GameLoop implements OnGameEndListenerNative {
    private final Lobby lobby;
    private final Field field;
    private final OnFieldUpdateListener listener;
    private final OnGameEndListener endListener;
    private int state = 0;
    private ScheduledFuture<?> fieldUpdateTask = null;
    private ScheduledFuture<?> entitySpawnTask = null;
    private Runnable spawnEntity;
    private Runnable updateField;

    public GameLoop(Lobby lobby, OnFieldUpdateListener updateListener, OnGameEndListener endListener) {
        this.lobby = lobby;
        this.field = new Field(new ArrayList<>(lobby.getMembers(true)), this);
        this.listener = updateListener;
        this.endListener = endListener;
    }

    public void start() {
        if (state > 0) throw new IllegalStateException("game loop is already started");
        updateField = () -> {
            if (entitySpawnTask.isDone() || entitySpawnTask.isCancelled()) {
                errorEnd();
            }

            log.info("[Runnable] updateField()");

            try {
                this.listener.updated(lobby, field.update(), field.getGuildhall());
            } catch (Exception e) {
                log.fatal(e.getMessage());
            }
        };
        ScheduledExecutorService updateService = Executors.newScheduledThreadPool(2);
        this.fieldUpdateTask = updateService.scheduleWithFixedDelay(updateField, 2 * 1000 + 977, 300, TimeUnit.MILLISECONDS);

        spawnEntity = () -> field.getRoads().forEach((identifier, road) -> {
            if (fieldUpdateTask.isDone() || fieldUpdateTask.isCancelled()) {
                errorEnd();
            }

            log.info("[Runnable] spawnEntity(identifier=%s)".formatted(identifier));

            try {
                road.insert(Entities.DEFAULT_ENEMY, road.getLength() - 1);
            } catch (Exception e) {
                log.fatal(e.getMessage());
            }
        });
        ScheduledExecutorService spawnService = Executors.newScheduledThreadPool(2);
        entitySpawnTask = spawnService.scheduleWithFixedDelay(spawnEntity, 3, 5, TimeUnit.SECONDS);
        state = 1;
    }

    @Override
    public void end() {
        close();
        endListener.end(lobby);
    }

    public void errorEnd() {
        log.error("exception in game : lobby=" + lobby);
        end();
    }

    public void close() {
        if (state <= 0) throw new IllegalStateException("could not close ended or not started game");
        fieldUpdateTask.cancel(true);
        entitySpawnTask.cancel(true);
        state = -1;
    }

    public void createTower(String username, TowerCreate towerCreate) {
        Road road = field.roads.get(username);
        road.insert(Entities.valueOf(towerCreate.getTowerName()), towerCreate.getCellNumber());
    }
}
