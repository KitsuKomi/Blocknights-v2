package com.blocknights.editor;

import com.blocknights.BlocknightsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class WandListener implements Listener {

    private final BlocknightsPlugin plugin;

    public WandListener(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getItem() == null || e.getItem().getType() != Material.BLAZE_ROD) return;

        if (!plugin.getEditorManager().isEditor(e.getPlayer())) return;
        e.setCancelled(true);

        var map = plugin.getMapManager().getActiveMap();
        if (map == null) return;

        // Récupérer la ligne sélectionnée par le joueur
        int currentLane = plugin.getEditorManager().getSelectedLane(e.getPlayer());

        if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
            Location newLoc = e.getPlayer().getLocation();
            
            // --- SÉCURITÉ VERTICALITÉ ---
            List<Location> path = map.getPath(currentLane);
            if (!path.isEmpty()) {
                Location lastLoc = path.get(path.size() - 1);
                double yDiff = Math.abs(newLoc.getY() - lastLoc.getY());

                // Seuil : 1.5 blocs (permet les dalles/escaliers, bloque les sauts de 2 blocs)
                if (yDiff > 1.5) {
                    e.getPlayer().sendMessage(Component.text("⚠ Trop pentu ! Différence Y max : 1.5 blocs.", NamedTextColor.RED));
                    return; // On annule l'ajout
                }
            }
            // ----------------------------

            // On passe par le Manager pour déclencher la sauvegarde auto
            plugin.getMapManager().addPathPoint(currentLane, newLoc);
            e.getPlayer().sendMessage(Component.text("Point ajouté à la Ligne " + currentLane, NamedTextColor.GREEN));
        
        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            map.removeLastPoint(currentLane);
            e.getPlayer().sendMessage(Component.text("Dernier point retiré (Ligne " + currentLane + ")", NamedTextColor.YELLOW));
        }
    }
}