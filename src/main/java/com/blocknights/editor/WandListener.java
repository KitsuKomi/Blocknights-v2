package com.blocknights.editor;

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
        // 1. Filtrage basique (Main principale, Item présent)
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getItem() == null || e.getItem().getType() != Material.BLAZE_ROD) return;

        // 2. Vérifier si le joueur est un éditeur actif
        if (!plugin.getEditorManager().isEditor(e.getPlayer())) return;

        e.setCancelled(true); // Annule l'action vanilla (casser/taper)

        // 3. Actions
        if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
            // Ajouter un point
            plugin.getMapManager().addPathPoint(e.getPlayer().getLocation());
            e.getPlayer().sendMessage(Component.text("Point ajouté !", NamedTextColor.GREEN));
        
        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            // Retirer le dernier point
            plugin.getMapManager().removeLastPoint();
            e.getPlayer().sendMessage(Component.text("Dernier point retiré.", NamedTextColor.YELLOW));
        }
    }
}