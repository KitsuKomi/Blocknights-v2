package com.blocknights.maps;

import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

public class BnMap {
    private final String id;
    // Une liste de listes (Chaque sous-liste est un chemin complet)
    private final List<List<Location>> lanes = new ArrayList<>();

    public BnMap(String id) {
        this.id = id;
        // On crée toujours la "Lane 0" par défaut
        lanes.add(new ArrayList<>());
    }

    public void addPoint(int laneIndex, Location loc) {
        ensureLaneExists(laneIndex);
        lanes.get(laneIndex).add(loc);
    }

    public void removeLastPoint(int laneIndex) {
        if (laneIndex < lanes.size() && !lanes.get(laneIndex).isEmpty()) {
            List<Location> lane = lanes.get(laneIndex);
            lane.remove(lane.size() - 1);
        }
    }

    // Crée les listes vides si on demande la Lane 5 alors qu'on en a que 1
    private void ensureLaneExists(int index) {
        while (lanes.size() <= index) {
            lanes.add(new ArrayList<>());
        }
    }

    public String getId() { return id; }
    
    public List<List<Location>> getLanes() { return lanes; }
    
    // Récupère un chemin spécifique (ou le premier par défaut)
    public List<Location> getPath(int laneIndex) {
        if (laneIndex < lanes.size()) return lanes.get(laneIndex);
        return new ArrayList<>();
    }
}