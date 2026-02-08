package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.data.DamageType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class OperatorFileIO {

    private final BlocknightsPlugin plugin;
    private final File folder;

    public OperatorFileIO(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "operators");
        
        // Créer le dossier et un exemple si vide
        if (!folder.exists()) {
            folder.mkdirs();
            createExampleFile();
        }
    }

    public void loadAll() {
        File folder = new File(plugin.getDataFolder(), "operators");
        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                
                String id = file.getName().replace(".yml", "");
                String name = config.getString("name", "Unknown");
                double cost = config.getDouble("cost", 100);
                EntityType type = EntityType.valueOf(config.getString("type", "ZOMBIE"));
                
                // Création de l'objet
                OperatorDefinition def = new OperatorDefinition(id, name, type, cost);
                
                // Stats
                def.setMaxHealth(config.getDouble("stats.health", 100));
                def.setAtk(config.getDouble("stats.atk", 10));
                def.setDef(config.getDouble("stats.def", 0));
                def.setAttackSpeed(config.getDouble("stats.speed", 2.0));
                def.setRange(config.getDouble("stats.range", 2.0));
                def.setBlockCount(config.getInt("stats.block_count", 0));

                // Rôles
                def.setRanged(config.getBoolean("roles.is_ranged", false));
                def.setHealer(config.getBoolean("roles.is_healer", false));
                def.setProjectileType(config.getString("roles.projectile", "NONE"));

                // Enregistrement
                plugin.getOperatorManager().registerOperator(def);
                plugin.getLogger().info("Opérateur chargé : " + name);

            } catch (Exception e) {
                plugin.getLogger().severe("Erreur chargement " + file.getName() + ": " + e.getMessage());
            }
        }
    }
    
    private OperatorDefinition loadSingle(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String id = file.getName().replace(".yml", "");

        String name = config.getString("name", "Inconnu");
        String typeStr = config.getString("type", "ZOMBIE");
        EntityType type = EntityType.valueOf(typeStr.toUpperCase());
        
        double cost = config.getDouble("cost", 100);
        int redeploy = config.getInt("redeploy", 60);

        double hp = config.getDouble("stats.hp", 1000);
        double atk = config.getDouble("stats.atk", 100);
        double def = config.getDouble("stats.def", 0);
        int block = config.getInt("stats.block", 1);
        
        double range = config.getDouble("combat.range", 3.0);
        int speed = config.getInt("combat.speed", 20);
        String dmgTypeStr = config.getString("combat.damage_type", "PHYSICAL");
        DamageType dtype = DamageType.valueOf(dmgTypeStr.toUpperCase());
        
        String skinName = config.getString("skin.name", id);
        String texture = config.getString("skin.texture", null);
        String signature = config.getString("skin.signature", null);

        return new OperatorDefinition(id, name, type, cost, redeploy, hp, atk, def, block, range, speed, dtype, skinName, texture, signature);
    }

    private void createExampleFile() {
        File file = new File(folder, "sniper.yml");
        if (!file.exists()) {
            plugin.saveResource("operators/sniper.yml", false);
        }
    }
}