package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.maps.BnMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlacementListener implements Listener {

    private final BlocknightsPlugin plugin;

    public PlacementListener(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        
        // CAS 1 : OUVRIR LE MENU (Nether Star)
        if (e.getItem() != null && e.getItem().getType() == Material.NETHER_STAR) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                new DeploymentMenu(plugin, e.getPlayer()).open(e.getPlayer());
                e.setCancelled(true);
                return;
            }
        }

        // CAS 2 : POSER L'UNITÉ (Clic sur un bloc)
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            OperatorDefinition selected = plugin.getOperatorManager().getPendingOperator(e.getPlayer());
            
            // Si le joueur a sélectionné une unité via le menu
            if (selected != null) {
                // On tente de la poser via notre Manager
                boolean success = plugin.getOperatorManager().placeOperator(
                    e.getPlayer(), 
                    selected.getId(), 
                    e.getClickedBlock().getLocation().add(0, 1, 0) // Au dessus du bloc
                );

                if (success) {
                    // On retire la sélection (il faut recliquer dans le menu pour en poser un autre)
                    // Ou on laisse pour en poser plusieurs d'affilée (Choix de design)
                    plugin.getOperatorManager().clearPending(e.getPlayer());
                }
                
                e.setCancelled(true); // Empêche de poser des blocs vanilla par erreur
            }
        }
    }
}