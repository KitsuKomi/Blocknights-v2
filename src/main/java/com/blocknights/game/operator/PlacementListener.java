package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.maps.BnMap;
import org.bukkit.Location;
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
        // On ne veut que le clic droit de la main principale
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // 1. Est-ce que le joueur a sélectionné un opérateur dans le menu ?
        OperatorDefinition def = plugin.getOperatorManager().getPendingOperator(e.getPlayer());
        if (def == null) return; // Non, il ne veut rien poser

        e.setCancelled(true); // On annule l'interaction vanilla (ne pas poser de bloc/ouvrir de coffre)

        // 2. Vérification de la Map et du Spot
        BnMap map = plugin.getMapManager().getActiveMap();
        if (map == null) return;

        // On vérifie le bloc AU-DESSUS du clic (là où l'opérateur va spawn)
        // Ou le bloc cliqué lui-même, selon comment tu as codé `isBuildable`.
        // Généralement : On clique sur la laine verte -> L'opérateur spawn dessus.
        Location clickedLoc = e.getClickedBlock().getLocation().add(0, 1, 0);

        // 3. Tentative de placement via le Manager
        // (La méthode placeOperator gère déjà : Argent, Collision, Zone Valide, Message Erreur)
        boolean success = plugin.getOperatorManager().placeOperator(e.getPlayer(), def.getId(), clickedLoc);

        if (success) {
            // Si ça a marché, on vide la sélection pour ne pas en poser 50 d'un coup
            plugin.getOperatorManager().clearPending(e.getPlayer());
        }
    }
}