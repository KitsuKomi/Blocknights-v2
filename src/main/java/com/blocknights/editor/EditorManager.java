package com.blocknights.editor;

import com.blocknights.BlocknightsPlugin;
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

    private final BlocknightsPlugin plugin;
    // On stocke quelle ligne le joueur est en train d'éditer (Joueur -> Index Ligne)
    private final Map<UUID, Integer> playerLanes = new HashMap<>();

    public EditorManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        startVisualizer();
    }

    public void toggleEditor(Player player) {
        if (playerLanes.containsKey(player.getUniqueId())) {
            playerLanes.remove(player.getUniqueId());
            player.getInventory().remove(Material.BLAZE_ROD);
            player.sendMessage(Component.text("Mode Éditeur désactivé.", NamedTextColor.YELLOW));
        } else {
            playerLanes.put(player.getUniqueId(), 0); // Par défaut : Ligne 0
            giveWand(player);
            player.sendMessage(Component.text("Mode Éditeur activé (Ligne 0) !", NamedTextColor.GREEN));
        }
    }

    public void selectLane(Player p, int laneIndex) {
        if (!isEditor(p)) return;
        if (laneIndex < 0) laneIndex = 0;
        
        playerLanes.put(p.getUniqueId(), laneIndex);
        p.sendMessage(Component.text("Édition de la Ligne " + laneIndex, NamedTextColor.AQUA));
    }

    public int getSelectedLane(Player p) {
        return playerLanes.getOrDefault(p.getUniqueId(), 0);
    }

    public boolean isEditor(Player player) {
        return playerLanes.containsKey(player.getUniqueId());
    }

    private void giveWand(Player player) {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wand.getItemMeta();
        meta.displayName(Component.text("Baguette d'Éditeur", NamedTextColor.GOLD));
        meta.lore(List.of(Component.text("Clic G: Point | Clic D: Undo", NamedTextColor.YELLOW)));
        wand.setItemMeta(meta);
        player.getInventory().addItem(wand);
    }

    private void startVisualizer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (playerLanes.isEmpty()) return;
                
                var map = plugin.getMapManager().getActiveMap();
                if (map == null) return;

                List<List<Location>> lanes = map.getLanes();

                // On boucle sur TOUTES les lignes
                for (int i = 0; i < lanes.size(); i++) {
                    List<Location> path = lanes.get(i);
                    if (path.isEmpty()) continue;

                    // Couleur différente pour la ligne 0 (Principale) et les autres
                    Particle lineParticle = (i == 0) ? Particle.HAPPY_VILLAGER : Particle.WAX_ON;

                    // Tracer les segments
                    for (int j = 0; j < path.size() - 1; j++) {
                        drawLine(path.get(j), path.get(j + 1), lineParticle);
                    }
                    
                    // Marquer Spawn (Bleu) et Fin (Orange)
                    path.get(0).getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, path.get(0), 5, 0, 0, 0, 0);
                    path.get(path.size() - 1).getWorld().spawnParticle(Particle.LAVA, path.get(path.size() - 1), 5, 0, 0, 0, 0);
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private void drawLine(Location p1, Location p2, Particle particleType) {
        double dist = p1.distance(p2);
        double step = 0.5;
        Vector direction = p2.toVector().subtract(p1.toVector()).normalize();
        
        boolean blocked = false;

        for (double d = 0; d < dist; d += step) {
            Location loc = p1.clone().add(direction.clone().multiply(d));
            if (loc.getBlock().getType().isSolid()) blocked = true;
            
            // Rouge si bloqué, sinon la couleur de la ligne
            Particle p = blocked ? Particle.FLAME : particleType;
            loc.getWorld().spawnParticle(p, loc, 1, 0, 0, 0, 0);
        }
    }
}