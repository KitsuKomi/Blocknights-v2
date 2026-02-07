package com.blocknights.data;

import org.bukkit.entity.EntityType;

public class WaveGroup {
    private EntityType mobType = EntityType.ZOMBIE;
    private int count = 5;
    private int interval = 20; // Ticks
    private double health = 20.0;
    private double speed = 0.25;
    private int laneIndex = 0; // Sur quelle ligne ils apparaissent

    // Constructeur vide pour l'éditeur (valeurs par défaut)
    public WaveGroup() {}

    public WaveGroup(EntityType type, int count, int interval, double hp, double spd, int lane) {
        this.mobType = type;
        this.count = count;
        this.interval = interval;
        this.health = hp;
        this.speed = spd;
        this.laneIndex = lane;
    }

    // Getters & Setters (Indispensable pour l'édition via GUI)
    public EntityType getMobType() { return mobType; }
    public void setMobType(EntityType mobType) { this.mobType = mobType; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public int getInterval() { return interval; }
    public void setInterval(int interval) { this.interval = interval; }

    public double getHealth() { return health; }
    public void setHealth(double health) { this.health = health; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public int getLaneIndex() { return laneIndex; }
    public void setLaneIndex(int laneIndex) { this.laneIndex = laneIndex; }
}