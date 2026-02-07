package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class DeploymentListener implements Listener {

    private final BlocknightsPlugin plugin;
    private final NamespacedKey opKey;

    public DeploymentListener(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        this.opKey = new NamespacedKey(plugin, "op_id");
    }

    @EventHandler
    public void onDeploy(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        ItemStack item = e.getItem();
        if (item == null || item.getType() == Material.AIR) return;

        // Vérifier si c'est un item de déploiement (via PersistentDataContainer)
        if (!item.getItemMeta().getPersistentDataContainer().has(opKey, PersistentDataType.STRING)) {
            return;
        }

        e.setCancelled(true); // Ne pas poser le bloc/item vanilla

        String opId = item.getItemMeta().getPersistentDataContainer().get(opKey, PersistentDataType.STRING);

        // On tente de placer l'opérateur SUR le bloc cliqué
        boolean success = plugin.getOperatorManager().placeOperator(
            e.getPlayer(), 
            opId, 
            e.getClickedBlock().getLocation().add(0, 1, 0)
        );

        if (success) {
            // Si ça a marché, on retire l'item
            e.getPlayer().getInventory().setItemInMainHand(null);
        }
    }
}