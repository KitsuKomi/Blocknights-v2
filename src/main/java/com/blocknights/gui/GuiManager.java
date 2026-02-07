package com.blocknights.gui;

import com.blocknights.BlocknightsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GuiManager implements Listener {

    public GuiManager(BlocknightsPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof InventoryGui gui) {
            e.setCancelled(true); // Par défaut, on empêche de prendre les items
            gui.handleClick(e);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof InventoryGui) {
            e.setCancelled(true);
        }
    }
    
    // Optionnel : Gérer la fermeture pour nettoyer ou sauvegarder
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        // ...
    }
}