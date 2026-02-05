package com.blocknights.editor;

import com.blocknights.BlocknightsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Location;

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

    // Affiche le chemin en particules (Visualisation)
    private void startVisualizer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (editors.isEmpty()) return;
                
                // On récupère la map active
                var map = plugin.getMapManager().getActiveMap();
                if (map == null || map.getPath().isEmpty()) return;

                List<Location> path = map.getPath();

                // Tracer les lignes
                for (int i = 0; i < path.size() - 1; i++) {
                    Location start = path.get(i);
                    Location end = path.get(i + 1);
                    drawLine(start, end);
                }
                
                // Marquer les points
                for (Location p : path) {
                    p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p, 5);
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private void drawLine(Location p1, Location p2) {
        double dist = p1.distance(p2);
        double step = 0.5; // Une particule tous les 0.5 blocs
        for (double d = 0; d < dist; d += step) {
            Location loc = p1.clone().add(p2.clone().subtract(p1).normalize().multiply(d));
            loc.getWorld().spawnParticle(Particle.END_ROD, loc, 1, 0, 0, 0, 0);
        }
    }
}