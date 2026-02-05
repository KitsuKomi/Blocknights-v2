package com.blocknights.maps;

import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

public class BnMap {
    private final String id;
    private final List<Location> path = new ArrayList<>();

    public BnMap(String id) {
        this.id = id;
    }

    public void addPoint(Location loc) {
        path.add(loc);
    }

    public void removeLastPoint() {
        if (!path.isEmpty()) {
            path.remove(path.size() - 1);
        }
    }

    public String getId() { return id; }
    public List<Location> getPath() { return path; }
}