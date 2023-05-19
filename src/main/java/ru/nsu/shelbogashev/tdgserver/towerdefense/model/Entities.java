package ru.nsu.shelbogashev.tdgserver.towerdefense.model;

public enum Entities {
    DEFAULT_GUILDHALL("guildhall", 200, 0, 0, 0, Team.ALLY, 0),
    DEFAULT_TOWER("default_tower", 20, 50, 500, 1, Team.ALLY, Long.MAX_VALUE),
    DEFAULT_ENEMY("default_enemy", 100, 1, 200, 1, Team.ENEMY, 2000);

    private final String name;
    private final int cell;
    private final int health;
    private final int damage;
    private final long coolDownTimeInMillis;
    private final int actionRadius;
    private final Team team;
    private final long stepTimeInMillis;
    private static long ID = Long.MIN_VALUE;

    Entities(
            String name,
            int health,
            int damage,
            long coolDownTimeInMillis,
            int actionRadius,
            Team team,
            long stepTimeInMillis
    ) {
        this.name = name;
        this.cell = -1;
        this.health = health;
        this.damage = damage;
        this.coolDownTimeInMillis = coolDownTimeInMillis;
        this.actionRadius = actionRadius;
        this.team = team;
        this.stepTimeInMillis = stepTimeInMillis;
    }

    Entity toEntity() {
        return Entity.builder()
                .id(ID++)
                .name(this.name)
                .damage(this.damage)
                .health(this.health)
                .cell(this.cell)
                .coolDownTimeInMillis(this.coolDownTimeInMillis)
                .actionRadius(this.actionRadius)
                .team(team)
                .stepTimeInMillis(this.stepTimeInMillis)
                .build();
    }
}