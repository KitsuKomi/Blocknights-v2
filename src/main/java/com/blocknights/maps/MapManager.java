package com.blocknights.maps;

import com.blocknights.BlocknightsPlugin;
import org.bukkit.Location;

public class MapManager {

    private final BlocknightsPlugin plugin;
    private final FolderMapIO io;
    
    // C'est cette variable qui te manquait
    private BnMap activeMap; 

    public MapManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        this.io = new FolderMapIO(plugin);
        
        // On essaie de charger "demo", sinon on crée une nouvelle map
        this.activeMap = io.load("demo");
        if (this.activeMap == null) {
            this.activeMap = new BnMap("demo");
        }
    }

    // --- Gestion des Points (Utilisé par le WandListener) ---

    public void addPathPoint(Location loc) {
        if (activeMap == null) return;
        
        activeMap.addPathPoint(loc);
        plugin.getLogger().info("Point ajouté. Total: " + activeMap.getPath().size());
        
        saveActiveMap(); // Sauvegarde auto
    }

    public void removeLastPoint() {
        if (activeMap != null && !activeMap.getPath().isEmpty()) {
            // Retire le dernier élément de la liste
            activeMap.getPath().remove(activeMap.getPath().size() - 1);
            
            plugin.getLogger().info("Dernier point retiré via Wand.");
            saveActiveMap(); // Sauvegarde auto
        }
    }
    
    // --- Gestion des Fichiers ---

    public void saveActiveMap() {
        if (activeMap != null) io.save(activeMap);
    }
    
    public void loadMap(String id) {
        BnMap loaded = io.load(id);
        if (loaded != null) {
            this.activeMap = loaded;
            plugin.getLogger().info("Map " + id + " chargée !");
        }
    }
    
    public void createMap(String id) {
        this.activeMap = new BnMap(id);
        saveActiveMap();
    }

    // --- Getters ---

    public BnMap getActiveMap() { return activeMap; }
    
    // Raccourcis pour que le reste du plugin (GameManager, WaveManager) continue de marcher
    public java.util.List<Location> getPath() {
        return activeMap != null ? activeMap.getPath() : java.util.Collections.emptyList();
    }
    
    public Location getSpawnPoint() {
        return activeMap != null ? activeMap.getSpawnLocation() : null;
    }
}