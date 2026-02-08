package com.blocknights.game.operator;

import com.blocknights.data.DamageType;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class OperatorDefinition {
    
    private final String id;           // ex: "sniper"
    private final String name;         // ex: "Sniper d'Élite"
    private final EntityType entityType; // Le skin (Squelette, Golem...)
    
    // Économie
    private final double cost;
    private final int redeployTime;

    // Stats de Combat
    private final double maxHealth;
    private final double atk;
    private final double def;
    private final int blockCount;      // Combien d'ennemis il arrête
    
    // Attaque
    private final int attackSpeed;     // Vitesse en ticks (20 = 1s)
    private final DamageType damageType;

    private final String skinName;    // Nom du joueur (ex: "Notch")
    private final String skinTexture; // Texture Base64 (Optionnel, pour skin fixe)
    private final String skinSignature;

    private double range = 1.0; // Portée en blocs (ex: 1.0 pour Tank, 5.0 pour Sniper)
    private boolean isRanged = false; // true = tire, false = tape au contact
    private String projectileType = "NONE"; // ARROW, SNOWBALL, FIREBALL, WITHER_SKULL, PARTICLE_BEAM

    public OperatorDefinition(String id, String name, EntityType type, double cost, int redeploy,
                              double hp, double atk, double def, int block,
                              double range, int speed, DamageType dtype, String skinName, String skinTexture, String skinSignature) {
        this.id = id;
        this.name = name;
        this.entityType = type;
        this.cost = cost;
        this.redeployTime = redeploy;
        this.maxHealth = hp;
        this.atk = atk;
        this.def = def;
        this.blockCount = block;
        this.range = range;
        this.attackSpeed = speed;
        this.damageType = dtype;
        this.skinName = skinName;
        this.skinTexture = skinTexture;
        this.skinSignature = skinSignature;
    }
    
    public ItemStack getIcon() {
        // Essaie de trouver l'oeuf de spawn correspondant (ex: ZOMBIE_SPAWN_EGG)
        Material mat = Material.getMaterial(entityType.name() + "_SPAWN_EGG");
        
        // Si pas d'oeuf (ex: PLAYER), on met une tête
        if (mat == null) {
            mat = Material.PLAYER_HEAD;
        }
        
        return new ItemStack(mat);
    }
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public EntityType getEntityType() { return entityType; }
    public double getCost() { return cost; }
    public int getRedeployTime() { return redeployTime; }
    public double getMaxHealth() { return maxHealth; }
    public double getAtk() { return atk; }
    public double getDef() { return def; }
    public int getBlockCount() { return blockCount; }
    public double getRange() { return range; }
    public int getAttackSpeed() { return attackSpeed; }
    public DamageType getDamageType() { return damageType; }
    public String getSkinName() { return skinName; }
    public String getSkinTexture() { return skinTexture; }
    public String getSkinSignature() { return skinSignature; }
    public void setRange(double range) { this.range = range; }
    public boolean isRanged() { return isRanged; }
    public void setRanged(boolean ranged) { isRanged = ranged; }
    public String getProjectileType() { return projectileType; }
    public void setProjectileType(String type) { this.projectileType = type; }
}