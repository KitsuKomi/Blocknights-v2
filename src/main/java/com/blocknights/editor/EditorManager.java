package com.blocknights.editor;

import com.blocknights.BlocknightsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

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
            // Désactiver
            editors.remove(player.getUniqueId());
            player.getInventory().remove(Material.BLAZE_ROD);
            player.sendMessage(Component.text("Mode Éditeur désactivé.", NamedTextColor.YELLOW));
        } else {
            // Activer
            editors.add(player.getUniqueId());
            giveWand(player);
            player.sendMessage(Component.text("Mode Éditeur activé !", NamedTextColor.GREEN));
            player.sendMessage(Component.text("Utilisez le Bâton pour tracer le chemin.", NamedTextColor.GRAY));
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

    // Affiche le chemin en particules temps réel pour les éditeurs
    private void startVisualizer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (editors.isEmpty()) return;
                
                List<Location> path = plugin.getMapManager().getPath();
                if (path.isEmpty()) return;

                // Dessiner des lignes entre les points
                for (int i = 0; i < path.size() - 1; i++) {
                    Location start = path.get(i);
                    Location end = path.get(i + 1);
                    drawLine(start, end);
                }
                
                // Marquer les points (Spawn = Vert, Fin = Rouge, Milieu = Jaune)
                for (int i = 0; i < path.size(); i++) {
                    Location p = path.get(i);
                    if (i == 0) p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p, 5); // Spawn
                    else if (i == path.size() - 1) p.getWorld().spawnParticle(Particle.FLAME, p, 5); // Fin
                    else p.getWorld().spawnParticle(Particle.WAX_ON, p, 1);
                }
            }
        }.runTaskTimer(plugin, 0L, 10L); // Toutes les 0.5 sec
    }

    private void drawLine(Location p1, Location p2) {
        double distance = p1.distance(p2);
        double points = distance * 2; // 2 particules par bloc
        for (int i = 0; i <= points; i++) {
            double ratio = i / points;
            Location loc = p1.clone().add(p2.clone().subtract(p1).multiply(ratio));
            loc.getWorld().spawnParticle(Particle.END_ROD, loc, 1, 0, 0, 0, 0);
        }
    }
}