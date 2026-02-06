package com.blocknights.maps;

import com.blocknights.BlocknightsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FolderMapIO {

    private final BlocknightsPlugin plugin;
    private final File mapsFolder;

    public FolderMapIO(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        this.mapsFolder = new File(plugin.getDataFolder(), "maps");
        if (!mapsFolder.exists()) {
            mapsFolder.mkdirs();
        }
    }

    public void save(BnMap map) {
        File file = new File(mapsFolder, map.getId() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        config.set("id", map.getId());
        
        // Sauvegarde des Lanes (List<List<Location>>)
        // Structure YAML :
        // lanes:
        //   0: [world,x,y,z, world,x,y,z]
        //   1: [world,x,y,z]
        for (int i = 0; i < map.getLanes().size(); i++) {
            List<Location> lane = map.getLanes().get(i);
            List<String> serializedPoints = new ArrayList<>();
            
            for (Location loc : lane) {
                serializedPoints.add(serializeLoc(loc));
            }
            
            config.set("lanes." + i, serializedPoints);
        }

        try {
            config.save(file);
            plugin.getLogger().info("Map " + map.getId() + " sauvegardée !");
        } catch (IOException e) {
            plugin.getLogger().severe("Erreur lors de la sauvegarde de la map " + map.getId());
            e.printStackTrace();
        }
    }

    public BnMap load(String id) {
        File file = new File(mapsFolder, id + ".yml");
        if (!file.exists()) return null;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        BnMap map = new BnMap(id);

        if (config.contains("lanes")) {
            for (String key : config.getConfigurationSection("lanes").getKeys(false)) {
                try {
                    int laneIndex = Integer.parseInt(key);
                    List<String> points = config.getStringList("lanes." + key);
                    
                    for (String s : points) {
                        Location loc = deserializeLoc(s);
                        if (loc != null) {
                            map.addPoint(laneIndex, loc);
                        }
                    }
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Erreur de format lane index: " + key);
                }
            }
        }

        return map;
    }

    // --- Helpers de conversion String <-> Location ---
    
    private String serializeLoc(Location l) {
        // On arrondit pour éviter les virgules infinies (x.5)
        return l.getWorld().getName() + "," + 
               String.format("%.2f", l.getX()) + "," + 
               String.format("%.2f", l.getY()) + "," + 
               String.format("%.2f", l.getZ());
    }

    private Location deserializeLoc(String s) {
        try {
            String[] parts = s.split(",");
            if (parts.length < 4) return null;
            
            World w = Bukkit.getWorld(parts[0]);
            if (w == null) return null; // Monde non chargé ou inexistant
            
            double x = Double.parseDouble(parts[1].replace(",", ".")); // Sûreté Locale
            double y = Double.parseDouble(parts[2].replace(",", "."));
            double z = Double.parseDouble(parts[3].replace(",", "."));
            
            return new Location(w, x, y, z);
        } catch (Exception e) {
            return null;
        }
    }
}