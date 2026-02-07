package com.blocknights.maps;

import com.blocknights.BlocknightsPlugin;
import org.bukkit.Location;
import java.util.List;

public class MapManager {

    private final BlocknightsPlugin plugin;
    private final MapBundleIO io; // NOUVEAU IO
    private BnMap activeMap; 

    public MapManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        this.io = new MapBundleIO(plugin);
        
        // Charger la map "demo" si elle existe
        this.activeMap = io.load("demo");
        if (this.activeMap == null) {
            this.activeMap = new BnMap("demo");
        }
    }

    public void createMap(String id) {
        if (activeMap != null) io.save(activeMap);
        this.activeMap = new BnMap(id);
        io.save(activeMap);
    }

    public boolean loadMap(String id) {
        if (activeMap != null) io.save(activeMap);
        BnMap loaded = io.load(id);
        if (loaded != null) {
            this.activeMap = loaded;
            return true;
        }
        return false;
    }
    
    public void saveActiveMap() {
        if (activeMap != null) io.save(activeMap);
    }

    // --- Compatibilité existante (inchangée) ---
    public void addPathPoint(int lane, Location loc) {
        if (activeMap == null) return;
        activeMap.addPoint(lane, loc);
        saveActiveMap(); 
    }
    public void removeLastPoint(int lane) {
        if (activeMap != null) {
            activeMap.removeLastPoint(lane);
            saveActiveMap();
        }
    }
    public void addPathPoint(Location loc) { addPathPoint(0, loc); }
    public BnMap getActiveMap() { return activeMap; }
    public List<Location> getPath() { return activeMap != null ? activeMap.getPath(0) : List.of(); }
    public Location getSpawnPoint() { return (!getPath().isEmpty()) ? getPath().get(0) : null; }
}