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

    public Map<String, OperatorDefinition> loadAll() {
        Map<String, OperatorDefinition> loaded = new HashMap<>();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        
        if (files == null) return loaded;

        for (File file : files) {
            try {
                OperatorDefinition def = loadSingle(file);
                loaded.put(def.getId(), def);
                plugin.getLogger().info("- Opérateur chargé: " + def.getName());
            } catch (Exception e) {
                plugin.getLogger().severe("Erreur dans " + file.getName() + ": " + e.getMessage());
            }
        }
        return loaded;
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