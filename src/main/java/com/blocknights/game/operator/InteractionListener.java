package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import net.citizensnpcs.api.CitizensAPI;
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
    public void onRightClickNPC(PlayerInteractEntityEvent e) {
        // On ne veut que la main principale
        if (e.getHand() != EquipmentSlot.HAND) return;

        // Est-ce un NPC Citizens ?
        if (CitizensAPI.getNPCRegistry().isNPC(e.getRightClicked())) {
            
            // Est-ce un de NOS opérateurs ?
            GameOperator op = plugin.getOperatorManager().getOperatorByEntity(e.getRightClicked());
            
            if (op != null) {
                // Bingo ! On ouvre le menu de gestion de l'unité
                new OperatorMenu(plugin, e.getPlayer(), op).open(e.getPlayer());
                
                // On annule l'événement pour pas que le joueur monte sur le cheval/parle au villageois
                e.setCancelled(true);
            }
        }
    }
}