package com.blocknights.data;

import org.bukkit.entity.EntityType;

public class WaveGroup {
    // On renomme enemyType -> mobType pour correspondre aux erreurs
    private EntityType mobType = EntityType.ZOMBIE; 
    private int count = 5;
    private double interval = 2.0;
    
    // Nouveaux champs pour corriger les erreurs MapBundleIO / WaveManager
    private double health = 20.0;
    private double speed = 0.23;
    private int laneIndex = 0;

    public WaveGroup() {}

    // --- Getters & Setters ---

    public EntityType getMobType() { return mobType; }
    public void setMobType(EntityType mobType) { this.mobType = mobType; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public double getInterval() { return interval; }
    public void setInterval(double interval) { this.interval = interval; }

    public double getHealth() { return health; }
    public void setHealth(double health) { this.health = health; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public int getLaneIndex() { return laneIndex; }
    public void setLaneIndex(int laneIndex) { this.laneIndex = laneIndex; }
}