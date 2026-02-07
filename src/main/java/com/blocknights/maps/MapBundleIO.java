package com.blocknights.maps;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.data.WaveDefinition;
import com.blocknights.data.WaveGroup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapBundleIO {

    private final BlocknightsPlugin plugin;
    private final File mapsRoot;

    public MapBundleIO(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        this.mapsRoot = new File(plugin.getDataFolder(), "maps");
        if (!mapsRoot.exists()) mapsRoot.mkdirs();
    }

    public void save(BnMap map) {
        // 1. Créer le dossier de la map
        File mapFolder = new File(mapsRoot, map.getId());
        if (!mapFolder.exists()) mapFolder.mkdirs();

        // 2. Sauvegarder paths.yml
        savePaths(map, new File(mapFolder, "paths.yml"));

        // 3. Sauvegarder waves.yml
        saveWaves(map, new File(mapFolder, "waves.yml"));
        
        plugin.getLogger().info("Map Bundle '" + map.getId() + "' sauvegardé.");
    }

    public BnMap load(String id) {
        File mapFolder = new File(mapsRoot, id);
        if (!mapFolder.exists()) return null;

        BnMap map = new BnMap(id);
        
        // Charger les composants
        loadPaths(map, new File(mapFolder, "paths.yml"));
        loadWaves(map, new File(mapFolder, "waves.yml"));

        return map;
    }

    // --- PATHS ---
    private void savePaths(BnMap map, File file) {
        YamlConfiguration config = new YamlConfiguration();
        for (int i = 0; i < map.getLanes().size(); i++) {
            List<String> locs = new ArrayList<>();
            for (Location l : map.getLanes().get(i)) {
                locs.add(serializeLoc(l));
            }
            config.set("lanes." + i, locs);
        }
        try { config.save(file); } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadPaths(BnMap map, File file) {
        if (!file.exists()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.contains("lanes")) {
            for (String key : config.getConfigurationSection("lanes").getKeys(false)) {
                int lane = Integer.parseInt(key);
                for (String s : config.getStringList("lanes." + key)) {
                    Location l = deserializeLoc(s);
                    if (l != null) map.addPoint(lane, l);
                }
            }
        }
    }

    // --- WAVES ---
    private void saveWaves(BnMap map, File file) {
        YamlConfiguration config = new YamlConfiguration();
        for (WaveDefinition wave : map.getWaves()) {
            String path = "waves." + wave.getId();
            config.set(path + ".delay", wave.getDelayBeforeNext());
            
            List<String> groupsData = new ArrayList<>(); // On pourrait utiliser des sections, mais simplifions
            // Pour faire propre, on sauvegarde chaque groupe
            int gIdx = 0;
            for (WaveGroup g : wave.getGroups()) {
                String gPath = path + ".groups." + gIdx;
                config.set(gPath + ".type", g.getMobType().name());
                config.set(gPath + ".count", g.getCount());
                config.set(gPath + ".interval", g.getInterval());
                config.set(gPath + ".hp", g.getHealth());
                config.set(gPath + ".speed", g.getSpeed());
                config.set(gPath + ".lane", g.getLaneIndex());
                gIdx++;
            }
        }
        try { config.save(file); } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadWaves(BnMap map, File file) {
        if (!file.exists()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection wavesSec = config.getConfigurationSection("waves");
        if (wavesSec == null) return;

        for (String wKey : wavesSec.getKeys(false)) {
            int wId = Integer.parseInt(wKey);
            WaveDefinition wave = new WaveDefinition(wId);
            wave.setDelayBeforeNext(config.getInt("waves." + wKey + ".delay", 10));

            ConfigurationSection groupsSec = config.getConfigurationSection("waves." + wKey + ".groups");
            if (groupsSec != null) {
                for (String gKey : groupsSec.getKeys(false)) {
                    ConfigurationSection gSec = groupsSec.getConfigurationSection(gKey);
                    WaveGroup g = new WaveGroup();
                    g.setMobType(EntityType.valueOf(gSec.getString("type", "ZOMBIE")));
                    g.setCount(gSec.getInt("count", 5));
                    g.setInterval(gSec.getInt("interval", 20));
                    g.setHealth(gSec.getDouble("hp", 20));
                    g.setSpeed(gSec.getDouble("speed", 0.25));
                    g.setLaneIndex(gSec.getInt("lane", 0));
                    wave.addGroup(g);
                }
            }
            map.addWave(wave);
        }
    }

    // --- Helpers ---
    private String serializeLoc(Location l) {
        return l.getWorld().getName() + "," + String.format("%.2f", l.getX()) + "," + String.format("%.2f", l.getY()) + "," + String.format("%.2f", l.getZ());
    }
    private Location deserializeLoc(String s) {
        try {
            String[] p = s.split(",");
            return new Location(Bukkit.getWorld(p[0]), Double.parseDouble(p[1].replace(",", ".")), Double.parseDouble(p[2].replace(",", ".")), Double.parseDouble(p[3].replace(",", ".")));
        } catch (Exception e) { return null; }
    }
}