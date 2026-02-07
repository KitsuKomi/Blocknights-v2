package com.blocknights.game.operator;

import org.bukkit.entity.EntityType;

public class OperatorDefinition {
    
    private final String id;
    private final String name;
    private final EntityType entityType;
    private final double cost;
    private final double range;
    private final double damage;
    private final int attackSpeed; // Ticks

    public OperatorDefinition(String id, String name, EntityType type, double cost, double range, double dmg, int speed) {
        this.id = id;
        this.name = name;
        this.entityType = type;
        this.cost = cost;
        this.range = range;
        this.damage = dmg;
        this.attackSpeed = speed;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public EntityType getEntityType() { return entityType; }
    public double getCost() { return cost; }
    public double getRange() { return range; }
    public double getDamage() { return damage; }
    public int getAttackSpeed() { return attackSpeed; }
}