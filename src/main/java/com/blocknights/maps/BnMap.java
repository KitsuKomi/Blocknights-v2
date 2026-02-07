package com.blocknights.maps;

import com.blocknights.data.WaveDefinition;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

public class BnMap {
    private final String id;
    private String displayName;
    
    // Données de chemin
    private final List<List<Location>> lanes = new ArrayList<>();
    
    // Données de vagues (Propre à cette map)
    private final List<WaveDefinition> waves = new ArrayList<>();

    public BnMap(String id) {
        this.id = id;
        this.displayName = id;
        lanes.add(new ArrayList<>()); // Lane 0 par défaut
    }

    // --- Gestion Wave ---
    public List<WaveDefinition> getWaves() { return waves; }
    public void addWave(WaveDefinition wave) { waves.add(wave); }

    // --- Gestion Path ---
    public void addPoint(int laneIndex, Location loc) {
        ensureLaneExists(laneIndex);
        lanes.get(laneIndex).add(loc);
    }

    public void removeLastPoint(int laneIndex) {
        if (laneIndex < lanes.size() && !lanes.get(laneIndex).isEmpty()) {
            lanes.get(laneIndex).remove(lanes.get(laneIndex).size() - 1);
        }
    }

    private void ensureLaneExists(int index) {
        while (lanes.size() <= index) {
            lanes.add(new ArrayList<>());
        }
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public List<List<Location>> getLanes() { return lanes; }
    public List<Location> getPath(int laneIndex) {
        if (laneIndex < lanes.size()) return lanes.get(laneIndex);
        return new ArrayList<>();
    }
}