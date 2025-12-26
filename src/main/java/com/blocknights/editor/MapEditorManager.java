package com.blocknights.editor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.editor.session.MapEditingSession;

public class MapEditorManager {

    private final Map<UUID, MapEditingSession> sessions = new HashMap<>();

    private final MapToolbox toolbox;
    private final BlocknightsPlugin plugin;
    private final File mapsDirectory;

    public MapEditorManager(MapToolbox toolbox) {
        this.plugin = toolbox.getPlugin();
        this.toolbox = toolbox;
        this.mapsDirectory = new File(plugin.getDataFolder(), "maps");
        if (!mapsDirectory.exists()) {
            mapsDirectory.mkdirs();
        }
    }

    public MapEditorManager(com.blocknights.BlocknightsPlugin plugin) {
        this(new MapToolbox(plugin));
    }

    public MapEditingSession openSession(Player player) {
        MapEditingSession session = new MapEditingSession(player.getUniqueId());
        sessions.put(player.getUniqueId(), session);
        toolbox.openToolbox(player, session);
        return session;
    }

    public Optional<MapEditingSession> getSession(Player player) {
        return Optional.ofNullable(sessions.get(player.getUniqueId()));
    }

    public void closeSession(Player player) {
        sessions.remove(player.getUniqueId());
    }

    public MapToolbox getToolbox() {
        return toolbox;
    }

    public boolean saveSession(MapEditingSession session, String mapName) {
        if (!session.isValid()) {
            return false;
        }

        session.setSchematicName(mapName);
        File file = new File(mapsDirectory, mapName + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        writeLocation(config, "spawn", session.getSpawn());
        writeLocation(config, "end", session.getEnd());
        writeLocation(config, "fawe.pos1", session.getPos1());
        writeLocation(config, "fawe.pos2", session.getPos2());

        int index = 0;
        for (Location pathPoint : session.getPathPoints()) {
            writeLocation(config, "path." + index, pathPoint);
            index++;
        }

        for (Map.Entry<com.blocknights.editor.tools.OperatorPlacementType, List<Location>> entry : session
                .getOperatorPlacements().entrySet()) {
            int pos = 0;
            for (Location location : entry.getValue()) {
                writeLocation(config, "placements." + entry.getKey().name().toLowerCase() + "." + pos, location);
                pos++;
            }
        }

        try {
            config.save(file);
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder la map " + mapName + " : " + e.getMessage());
            return false;
        }
    }

    public Optional<MapEditingSession> loadSession(Player player, String mapName) {
        File file = new File(mapsDirectory, mapName + ".yml");
        if (!file.exists()) {
            return Optional.empty();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        MapEditingSession session = new MapEditingSession(player.getUniqueId());
        session.setSchematicName(mapName);

        session.setSpawn(readLocation(config, "spawn"));
        session.setEnd(readLocation(config, "end"));
        session.setPos1(readLocation(config, "fawe.pos1"));
        session.setPos2(readLocation(config, "fawe.pos2"));

        ConfigurationSection pathSection = config.getConfigurationSection("path");
        if (pathSection != null) {
            for (String key : pathSection.getKeys(false)) {
                Location location = readLocation(config, "path." + key);
                if (location != null) {
                    session.addPathPoint(location);
                }
            }
        }

        ConfigurationSection placements = config.getConfigurationSection("placements");
        if (placements != null) {
            for (String typeKey : placements.getKeys(false)) {
                try {
                    com.blocknights.editor.tools.OperatorPlacementType type = com.blocknights.editor.tools.OperatorPlacementType
                            .valueOf(typeKey.toUpperCase());
                    ConfigurationSection section = placements.getConfigurationSection(typeKey);
                    if (section == null) {
                        continue;
                    }
                    for (String key : section.getKeys(false)) {
                        Location location = readLocation(config, "placements." + typeKey + "." + key);
                        if (location != null) {
                            session.addOperatorPlacement(type, location);
                        }
                    }
                } catch (IllegalArgumentException ignored) {
                    plugin.getLogger().warning("Type de placement opérateur inconnu dans " + mapName + " : " + typeKey);
                }
            }
        }

        sessions.put(player.getUniqueId(), session);
        return Optional.of(session);
    }

    public String listSavedMaps() {
        String[] files = mapsDirectory.list((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) {
            return "Aucune map sauvegardée.";
        }
        StringBuilder builder = new StringBuilder("Maps disponibles : ");
        for (int i = 0; i < files.length; i++) {
            String baseName = files[i].replaceFirst("\\.yml$", "");
            builder.append(baseName);
            if (i < files.length - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    private void writeLocation(YamlConfiguration config, String path, Location location) {
        if (location == null) {
            return;
        }
        config.set(path + ".world", location.getWorld().getName());
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        config.set(path + ".yaw", location.getYaw());
        config.set(path + ".pitch", location.getPitch());
    }

    private Location readLocation(YamlConfiguration config, String path) {
        String worldName = config.getString(path + ".world");
        if (worldName == null || plugin.getServer().getWorld(worldName) == null) {
            return null;
        }
        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        float yaw = (float) config.getDouble(path + ".yaw");
        float pitch = (float) config.getDouble(path + ".pitch");
        return new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
    }
}
