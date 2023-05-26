package ru.nsu.shelbogashev.tdgserver.server.towerdefense.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Entity {
    long id;
    String name;
    @Builder.Default
    int cell = -1;
    int health;
    int damage;
    long coolDownTimeInMillis;
    long stepTimeInMillis;
    int actionRadius;
    Team team;

    @Builder.Default
    private long lastAttackUpdated = System.currentTimeMillis();
    @Builder.Default
    private long lastStepUpdated = System.currentTimeMillis();

    public int getDamage() {
        long current = System.currentTimeMillis();
        if (lastAttackUpdated + coolDownTimeInMillis > current) return 0;

        lastAttackUpdated = current;
        return damage;
    }

    public void acceptDamage(int value) {
        health -= value;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void makeStep() {
        long current = System.currentTimeMillis();
        if (lastStepUpdated + stepTimeInMillis > current) return;

        lastStepUpdated = current;
        cell = Integer.max(0, cell - 1);
    }
}
