package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;

public class OperatorLoader {

    private final BlocknightsPlugin plugin;

    public OperatorLoader(BlocknightsPlugin plugin) {
        this.plugin = plugin;
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
                def.setSkinName(config.getString("skin.name", null));

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
}