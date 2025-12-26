package com.blocknights.editor.session;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;

import com.blocknights.editor.tools.OperatorPlacementType;

public class MapEditingSession {

    private final UUID owner;
    private Location spawn;
    private Location end;
    private Location pos1;
    private Location pos2;
    private final List<Location> pathPoints = new ArrayList<>();
    private final Map<OperatorPlacementType, List<Location>> operatorPlacements = new EnumMap<>(OperatorPlacementType.class);
    private String schematicName;

    public MapEditingSession(UUID owner) {
        this.owner = owner;
        for (OperatorPlacementType type : OperatorPlacementType.values()) {
            operatorPlacements.put(type, new ArrayList<>());
        }
    }

    public UUID getOwner() {
        return owner;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setEnd(Location end) {
        this.end = end;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getEnd() {
        return end;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void addPathPoint(Location point) {
        pathPoints.add(point);
    }

    public void removeLastPathPoint() {
        if (!pathPoints.isEmpty()) {
            pathPoints.remove(pathPoints.size() - 1);
        }
    }

    public List<Location> getPathPoints() {
        return pathPoints;
    }

    public void addOperatorPlacement(OperatorPlacementType type, Location location) {
        operatorPlacements.get(type).add(location);
    }

    public Map<OperatorPlacementType, List<Location>> getOperatorPlacements() {
        return operatorPlacements;
    }

    public void setSchematicName(String schematicName) {
        this.schematicName = schematicName;
    }

    public String getSchematicName() {
        return schematicName;
    }

    public void reset() {
        spawn = null;
        end = null;
        pos1 = null;
        pos2 = null;
        pathPoints.clear();
        operatorPlacements.values().forEach(List::clear);
        schematicName = null;
    }

    public boolean isValid() {
        return spawn != null && end != null && !pathPoints.isEmpty() && hasOperatorPlacement() && pos1 != null && pos2 != null
                && schematicName != null;
    }

    private boolean hasOperatorPlacement() {
        return operatorPlacements.values().stream().anyMatch(list -> !list.isEmpty());
    }

    public String getSummary() {
        return "Spawn=" + (spawn != null) + ", End=" + (end != null) + ", PathPoints=" + pathPoints.size()
                + ", Placements=" + operatorPlacements.values().stream().mapToInt(List::size).sum() + ", FAWE="
                + (pos1 != null && pos2 != null) + ", Schematic="
                + (schematicName != null ? schematicName : "aucune") + ", Valide=" + isValid();
    }
}
