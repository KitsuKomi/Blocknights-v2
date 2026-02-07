package com.blocknights.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class InventoryGui implements InventoryHolder {

    protected final Inventory inventory;
    // Map pour associer un slot à une action (Code executable)
    protected final Map<Integer, Consumer<InventoryClickEvent>> actions = new HashMap<>();

    public InventoryGui(int size, Component title) {
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public abstract void init(); // Où on place les items

    public void open(Player p) {
        init();
        p.openInventory(inventory);
    }

    public void handleClick(InventoryClickEvent e) {
        // Exécute l'action associée au slot cliqué
        Consumer<InventoryClickEvent> action = actions.get(e.getRawSlot());
        if (action != null) {
            action.accept(e);
        }
    }

    // --- Helpers pour construire le menu rapidement ---

    protected void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> action) {
        inventory.setItem(slot, item);
        if (action != null) actions.put(slot, action);
    }

    protected ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name));
        if (lore.length > 0) {
            // Conversion simple String -> Component pour l'exemple
            meta.lore(java.util.Arrays.stream(lore).map(Component::text).collect(java.util.stream.Collectors.toList()));
        }
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public @NotNull Inventory getInventory() { return inventory; }
}