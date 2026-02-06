package com.blocknights.maps;

import com.blocknights.BlocknightsPlugin;
import org.bukkit.Location;
import java.util.List;

public class MapManager {

    private final BlocknightsPlugin plugin;
    private final FolderMapIO io; // Le système de sauvegarde
    private BnMap activeMap; 

    public MapManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        this.io = new FolderMapIO(plugin);
        
        // On essaie de charger "demo", sinon on crée une nouvelle map
        this.activeMap = io.load("demo");
        if (this.activeMap == null) {
            this.activeMap = new BnMap("demo");
        } else {
            plugin.getLogger().info("Map 'demo' chargée avec succès !");
        }
    }

    // --- Gestion des maps ---

    public void createMap(String id) {
        // Sauvegarde l'ancienne avant de changer
        if (activeMap != null) io.save(activeMap);
        
        this.activeMap = new BnMap(id);
        io.save(activeMap); // Crée le fichier tout de suite
    }

    public boolean loadMap(String id) {
        // Sauvegarde l'ancienne avant de changer
        if (activeMap != null) io.save(activeMap);

        BnMap loaded = io.load(id);
        if (loaded != null) {
            this.activeMap = loaded;
            plugin.getLogger().info("Map " + id + " chargée.");
            return true;
        }
        return false;
    }
    
    public void saveActiveMap() {
        if (activeMap != null) io.save(activeMap);
    }

    // --- Gestion des Points (Compatibilité Editor) ---

    // Utilisé par les commandes de compatibilité (Lane 0)
    public void addPathPoint(Location loc) {
        addPathPoint(0, loc);
    }

    // Vraie méthode utilisée par l'éditeur V2.1
    public void addPathPoint(int lane, Location loc) {
        if (activeMap == null) return;
        activeMap.addPoint(lane, loc);
        // Sauvegarde AUTO à chaque point (Sécurité maximale)
        saveActiveMap(); 
    }

    public void removeLastPoint(int lane) {
        if (activeMap != null) {
            activeMap.removeLastPoint(lane);
            saveActiveMap(); // Sauvegarde AUTO
        }
    }

    // --- Getters ---

    public BnMap getActiveMap() { return activeMap; }

    public Location getSpawnPoint() {
        if (activeMap == null) return null;
        List<Location> path = activeMap.getPath(0);
        if (path.isEmpty()) return null;
        return path.get(0);
    }

    public List<Location> getPath() {
        return activeMap != null ? activeMap.getPath(0) : List.of();
    }
}