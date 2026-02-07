package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class InteractionListener implements Listener {

    private final BlocknightsPlugin plugin;

    public InteractionListener(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        // Évite le double événement (Main + Offhand)
        if (e.getHand() != EquipmentSlot.HAND) return;

        // On cherche si l'entité cliquée est un Opérateur géré par notre plugin
        GameOperator op = plugin.getOperatorManager().getOperatorByEntity(e.getRightClicked());
        
        if (op != null) {
            // C'est un opérateur ! On ouvre le menu
            new OperatorInfoGui(plugin, e.getPlayer(), op).open(e.getPlayer());
        }
    }
}