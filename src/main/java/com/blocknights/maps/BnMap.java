package com.blocknights.maps;

import com.blocknights.data.WaveDefinition;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BnMap {
    private final String id;
    private String displayName;
    private int initialLives = 20;      // Vies du Nexus
    private double initialMoney = 1000; // LMD de départ

    // Données de chemin (Lanes)
    private final List<List<Location>> lanes = new ArrayList<>();
    
    // Données de vagues
    private final List<WaveDefinition> waves = new ArrayList<>();

    // Données de construction (Tuiles valides)
    private final Set<Location> buildableSpots = new HashSet<>();

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

    // --- Gestion des Spots (Zones Constructibles) ---
    public void addSpot(Location loc) {
        buildableSpots.add(getBlockCenter(loc));
    }

    public void removeSpot(Location loc) {
        buildableSpots.remove(getBlockCenter(loc));
    }

    public boolean isBuildable(Location loc) {
        return buildableSpots.contains(getBlockCenter(loc));
    }
    
    public Set<Location> getSpots() { return buildableSpots; }

    // Helper pour centrer (x.5, y.0, z.5)
    private Location getBlockCenter(Location loc) {
        return new Location(loc.getWorld(), 
            loc.getBlockX() + 0.5, 
            loc.getBlockY(), 
            loc.getBlockZ() + 0.5);
    }

    // --- Getters Standard ---
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public List<List<Location>> getLanes() { return lanes; }
    public List<Location> getPath(int laneIndex) {
        if (laneIndex < lanes.size()) return lanes.get(laneIndex);
        return new ArrayList<>();
    }
    public int getInitialLives() { return initialLives; }
    public void setInitialLives(int lives) { this.initialLives = lives; }

    public double getInitialMoney() { return initialMoney; }
    public void setInitialMoney(double money) { this.initialMoney = money; }
}