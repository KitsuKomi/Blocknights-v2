package com.blocknights.maps;

import com.blocknights.BlocknightsPlugin;
import org.bukkit.Location;

public class MapManager {

    private final BlocknightsPlugin plugin;
    private BnMap activeMap; // La map en cours d'édition

    public MapManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        // On crée une map "test" par défaut pour que l'éditeur fonctionne tout de suite
        this.activeMap = new BnMap("test");
    }

    // --- Gestion ---

    public void addPathPoint(Location loc) {
        if (activeMap == null) return;
        activeMap.addPoint(loc);
        plugin.getLogger().info("Point ajouté. Total: " + activeMap.getPath().size());
    }

    public void removeLastPoint() {
        if (activeMap != null) {
            activeMap.removeLastPoint();
            plugin.getLogger().info("Dernier point retiré via Wand.");
        }
    }

    // --- Getters ---

    public BnMap getActiveMap() { return activeMap; }
    
    // Raccourcis pour que le reste du plugin continue de marcher
    public java.util.List<Location> getPath() {
        return activeMap != null ? activeMap.getPath() : java.util.Collections.emptyList();
    }
    
    public Location getSpawnPoint() {
        return (activeMap != null && !activeMap.getPath().isEmpty()) ? activeMap.getPath().get(0) : null;
    }
}