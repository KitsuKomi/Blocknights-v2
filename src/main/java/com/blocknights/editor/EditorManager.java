package com.blocknights.editor;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.maps.BnMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class EditorManager {

    public enum EditorMode { PATH, SPOTS }

    private final BlocknightsPlugin plugin;
    
    // Joueurs en mode éditeur -> Ligne sélectionnée
    private final Map<UUID, Integer> playerLanes = new HashMap<>();
    // Joueurs -> Mode actuel (Chemin ou Tuiles)
    private final Map<UUID, EditorMode> editorModes = new HashMap<>();

    public EditorManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        startVisualizer();
    }

    // --- Gestion Activation ---
    public void toggleEditor(Player player) {
        if (playerLanes.containsKey(player.getUniqueId())) {
            playerLanes.remove(player.getUniqueId());
            editorModes.remove(player.getUniqueId());
            player.getInventory().remove(Material.BLAZE_ROD);
            player.sendMessage(Component.text("Mode Éditeur désactivé.", NamedTextColor.YELLOW));
        } else {
            playerLanes.put(player.getUniqueId(), 0);
            editorModes.put(player.getUniqueId(), EditorMode.PATH);
            giveWand(player);
            player.sendMessage(Component.text("Mode Éditeur activé !", NamedTextColor.GREEN));
        }
    }

    public boolean isEditor(Player player) {
        return playerLanes.containsKey(player.getUniqueId());
    }

    // --- Gestion Modes & Lanes ---
    public void selectLane(Player p, int laneIndex) {
        if (!isEditor(p)) return;
        if (laneIndex < 0) laneIndex = 0;
        playerLanes.put(p.getUniqueId(), laneIndex);
        p.sendMessage(Component.text("Édition de la Ligne " + laneIndex, NamedTextColor.AQUA));
    }

    public int getSelectedLane(Player p) {
        return playerLanes.getOrDefault(p.getUniqueId(), 0);
    }

    public void toggleMode(Player p) {
        EditorMode current = editorModes.getOrDefault(p.getUniqueId(), EditorMode.PATH);
        if (current == EditorMode.PATH) {
            editorModes.put(p.getUniqueId(), EditorMode.SPOTS);
            plugin.getLang().send(p, "editor-mode-spots");
        } else {
            editorModes.put(p.getUniqueId(), EditorMode.PATH);
            plugin.getLang().send(p, "editor-mode-path");
        }
    }

    public EditorMode getMode(Player p) {
        return editorModes.getOrDefault(p.getUniqueId(), EditorMode.PATH);
    }

    // --- Outils ---
    private void giveWand(Player player) {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wand.getItemMeta();
        meta.displayName(Component.text("Baguette d'Éditeur", NamedTextColor.GOLD));
        meta.lore(List.of(
            Component.text("Clic G: Ajouter | Clic D: Retirer", NamedTextColor.YELLOW),
            Component.text("Sneak + Clic D: Changer Mode", NamedTextColor.GRAY)
        ));
        wand.setItemMeta(meta);
        player.getInventory().addItem(wand);
    }

    // --- Visualiseur ---
    private void startVisualizer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (playerLanes.isEmpty()) return;
                
                var map = plugin.getMapManager().getActiveMap();
                if (map == null) return;

                // 1. Visualiser les Chemins (Lanes)
                List<List<Location>> lanes = map.getLanes();
                for (int i = 0; i < lanes.size(); i++) {
                    List<Location> path = lanes.get(i);
                    if (path.isEmpty()) continue;

                    Particle lineParticle = (i == 0) ? Particle.HAPPY_VILLAGER : Particle.WAX_ON;

                    for (int j = 0; j < path.size() - 1; j++) {
                        drawLine(path.get(j), path.get(j + 1), lineParticle);
                    }
                    
                    // Marqueurs
                    path.get(0).getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, path.get(0), 5, 0, 0, 0, 0);
                    path.get(path.size() - 1).getWorld().spawnParticle(Particle.LAVA, path.get(path.size() - 1), 5, 0, 0, 0, 0);
                }

                // 2. Visualiser les Tuiles (Spots)
                for (Location spot : map.getSpots()) {
                    // Particule au-dessus du bloc
                    spot.getWorld().spawnParticle(Particle.COMPOSTER, spot.clone().add(0, 1.2, 0), 1);
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private void drawLine(Location p1, Location p2, Particle particleType) {
        double dist = p1.distance(p2);
        double step = 0.5;
        Vector direction = p2.toVector().subtract(p1.toVector()).normalize();
        
        for (double d = 0; d < dist; d += step) {
            Location loc = p1.clone().add(direction.clone().multiply(d));
            loc.getWorld().spawnParticle(particleType, loc, 1, 0, 0, 0, 0);
        }
    }
}