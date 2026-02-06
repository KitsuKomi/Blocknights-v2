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
import org.bukkit.util.Vector; // Import important pour les maths

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EditorManager {

    private final BlocknightsPlugin plugin;
    private final Set<UUID> editors = new HashSet<>();

    public EditorManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        startVisualizer();
    }

    public void toggleEditor(Player player) {
        if (editors.contains(player.getUniqueId())) {
            editors.remove(player.getUniqueId());
            player.getInventory().remove(Material.BLAZE_ROD);
            player.sendMessage(Component.text("Mode Éditeur désactivé.", NamedTextColor.YELLOW));
        } else {
            editors.add(player.getUniqueId());
            giveWand(player);
            player.sendMessage(Component.text("Mode Éditeur activé !", NamedTextColor.GREEN));
        }
    }

    public boolean isEditor(Player player) {
        return editors.contains(player.getUniqueId());
    }

    private void giveWand(Player player) {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wand.getItemMeta();
        meta.displayName(Component.text("Baguette d'Éditeur", NamedTextColor.GOLD));
        meta.lore(List.of(
            Component.text("Clic Gauche: Ajouter un point", NamedTextColor.YELLOW),
            Component.text("Clic Droit: Retirer le dernier point", NamedTextColor.YELLOW)
        ));
        wand.setItemMeta(meta);
        player.getInventory().addItem(wand);
    }

    private void startVisualizer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (editors.isEmpty()) return;
                
                var map = plugin.getMapManager().getActiveMap();
                if (map == null || map.getPath().isEmpty()) return;

                List<Location> path = map.getPath();

                // Tracer les lignes
                for (int i = 0; i < path.size() - 1; i++) {
                    drawLine(path.get(i), path.get(i + 1));
                }
                
                // Marquer les points
                for (Location p : path) {
                    p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p, 5);
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    // C'EST ICI QUE J'AI CORRIGÉ LA LOGIQUE MATHÉMATIQUE
    private void drawLine(Location p1, Location p2) {
        double dist = p1.distance(p2);
        double step = 0.5; 
        
        // 1. On convertit les Locations en Vecteurs pour calculer la direction
        Vector direction = p2.toVector().subtract(p1.toVector()).normalize();

        for (double d = 0; d < dist; d += step) {
            // 2. On ajoute le vecteur directionnel multiplié par la distance 'd'
            // .clone() est vital car .multiply() modifie l'objet original
            Location loc = p1.clone().add(direction.clone().multiply(d));
            
            loc.getWorld().spawnParticle(Particle.END_ROD, loc, 1, 0, 0, 0, 0);
        }
    }
}