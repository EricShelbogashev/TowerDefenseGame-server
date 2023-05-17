package ru.nsu.shelbogashev.tdgserver.towerdefense.model;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class Road {
    private int[] fromAllyDamage;
    private int[] fromEnemyDamage;
    private final String identifier;
    private final int length;
    private List<Entity> enemies;
    private List<Entity> allies;
    private final Object monitor = new Object();

    public Road(int length, String identifier) {
        this.identifier = identifier;
        this.length = length;
        this.enemies = new ArrayList<>(8);
        this.allies = new ArrayList<>(8);
        prepareDamageHolders();
    }

    public synchronized void insert(Entities entity, int position) {
        log.info("insert(Entities=%s, position=%d)".formatted(entity.name(), position));
        if (position < 0 && position >= length) throw new IndexOutOfBoundsException();

        Entity tmp = entity.toEntity();
        tmp.setCell(position);
        synchronized (monitor) {
            switch (tmp.getTeam()) {
                case ALLY -> allies.add(tmp);
                case ENEMY -> enemies.add(tmp);
            }
        }
    }

    public List<Entity> getEntities() {
        log.info("getEntities()");
        List<Entity> result = new ArrayList<>(enemies.size() + allies.size());
        result.addAll(enemies);
        result.addAll(allies);
        return result;
    }

    public int updateHealth() {
        log.info("updateHealth()");
        synchronized (monitor) {
            prepareDamageHolders();
        }
        synchronized (monitor) {
            calculateDamage(enemies, fromEnemyDamage);
        }
        synchronized (monitor) {
            calculateDamage(allies, fromAllyDamage);
        }
        synchronized (monitor) {
            enemies.forEach(it -> it.acceptDamage(fromAllyDamage[it.getCell()]));
            List<Entity> list = enemies.stream().filter(Entity::isAlive).toList();
            enemies.clear();
            enemies.addAll(list);
        }
        synchronized (monitor) {
            allies.forEach(it -> it.acceptDamage(fromEnemyDamage[it.getCell()]));
            List<Entity> list = allies.stream().filter(Entity::isAlive).toList();
            allies.clear();
            allies.addAll(list);
        }
        return fromEnemyDamage[0];
    }

    public void updatePosition() {
        log.info("updatePosition()");
        synchronized (monitor) {
            enemies.forEach(Entity::makeStep);
        }
    }

    /* Optimization */
    private void prepareDamageHolders() {
        log.info("prepareDamageHolders()");
        this.fromAllyDamage = new int[length];
        this.fromEnemyDamage = new int[length];
    }

    /* [start, end - 1] == [start, end) */
    /* start from -infty to + infty */
    /* end from -infty to + infty */
    /* if start > end no effect*/
    private void sumOnRange(int start, int end, int value, int[] array) {
        log.info("sumOnRange(start=%d, end=%d, value=%d, array=%s)".formatted(start, end, value, Arrays.toString(array)));
        if (value == 0) return;
        if (start < 0) start = 0;
        if (end > length) end = length;
        if (start > end) return;

        for (int i = start; i < end; ++i) {
            array[i] += value;
        }
    }

    private void calculateDamage(List<Entity> entities, int[] damageHolder) {
//        log.debug("calculateDamage(entities=%s, damageHolder=%s)".formatted(entities, Arrays.toString(damageHolder)));
        entities.forEach(it -> sumOnRange(
                it.getCell() - it.getActionRadius(),
                it.getCell() + it.getActionRadius(),
                it.getDamage(),
                damageHolder
        ));
    }
    // TODO: чистить мертвые сущности

    public String getIdentifier() {
        log.info("getIdentifier()");
        return identifier;
    }

    public int getLength() {
        log.info("getLength()");
        return length;
    }

    @Override
    public String toString() {
        return "Road{" +
                "identifier='" + identifier + '\'' +
                ", length=" + length +
                ", enemies=" + enemies +
                ", allies=" + allies +
                '}';
    }
}
