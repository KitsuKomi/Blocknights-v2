package com.blocknights.editor.ui;

import com.blocknights.BlocknightsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class WandListener implements Listener {

    private final BlocknightsPlugin plugin;

    public WandListener(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getItem() == null || e.getItem().getType() != Material.BLAZE_ROD) return;

        // Vérifie si le joueur est en mode éditeur
        if (!plugin.getEditorManager().isEditor(e.getPlayer())) return;

        e.setCancelled(true); // Bloque l'action normale (taper/casser)

        var map = plugin.getMapManager().getActiveMap();
        if (map == null) return;

        if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
            // Ajouter
            map.addPoint(e.getPlayer().getLocation());
            e.getPlayer().sendMessage(Component.text("Point ajouté (" + map.getPath().size() + ")", NamedTextColor.GREEN));
        
        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            // Retirer
            map.removeLastPoint();
            e.getPlayer().sendMessage(Component.text("Dernier point retiré", NamedTextColor.YELLOW));
        }
    }
}