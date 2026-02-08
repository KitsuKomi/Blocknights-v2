package com.blocknights.game.operator;

import org.bukkit.entity.EntityType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class OperatorDefinition {
    
    private final String id;
    private final String name;
    private final EntityType entityType; // C'est ça qui lie à Citizens (ZOMBIE, IRON_GOLEM, etc.)
    private final double cost;
    private String skinName;
    private String skinTexture;
    private String skinSignature;

    // Stats RPG
    private double maxHealth = 100;
    private double atk = 10;
    private double def = 0;
    private double attackSpeed = 2.0;
    private double range = 2.0;
    private int blockCount = 1;
    
    // Rôles
    private boolean isRanged = false;
    private boolean isHealer = false;
    private String projectileType = "NONE";

    // --- CONSTRUCTEUR MANQUANT ---
    public OperatorDefinition(String id, String name, EntityType entityType, double cost) {
        this.id = id;
        this.name = name;
        this.entityType = entityType;
        this.cost = cost;
    }

    // --- GETTERS ---
    public String getId() { return id; }
    public String getName() { return name; }
    public EntityType getEntityType() { return entityType; }
    public double getCost() { return cost; }
    public double getMaxHealth() { return maxHealth; }
    public double getAtk() { return atk; }
    public double getDef() { return def; }
    public double getAttackSpeed() { return attackSpeed; }
    public double getRange() { return range; }
    public int getBlockCount() { return blockCount; }
    public boolean isRanged() { return isRanged; }
    public boolean isHealer() { return isHealer; }
    public String getProjectileType() { return projectileType; }

    // --- SETTERS MANQUANTS ---
    public void setMaxHealth(double maxHealth) { this.maxHealth = maxHealth; }
    public void setAtk(double atk) { this.atk = atk; }
    public void setDef(double def) { this.def = def; }
    public void setAttackSpeed(double speed) { this.attackSpeed = speed; }
    public void setRange(double range) { this.range = range; }
    public void setBlockCount(int count) { this.blockCount = count; }
    public void setRanged(boolean ranged) { isRanged = ranged; }
    public void setHealer(boolean healer) { isHealer = healer; }
    public void setProjectileType(String type) { this.projectileType = type; }
    public String getSkinName() { return skinName; }
    public void setSkinName(String skinName) { this.skinName = skinName; }
    
    public ItemStack getIcon() {
        String matName = entityType.name() + "_SPAWN_EGG";
        Material mat = Material.getMaterial(matName);
        if (mat == null) mat = Material.PLAYER_HEAD;
        return new ItemStack(mat);
    }
}