package com.blocknights.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class InventoryGui implements InventoryHolder {

    protected final Inventory inventory;
    protected final Map<Integer, Consumer<InventoryClickEvent>> actions = new HashMap<>();

    public InventoryGui(int size, Component title) {
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public abstract void init();

    public void open(Player player) {
        init();
        player.openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    // Gestion du Clic (Appelé par GuiManager)
    public void handleClick(InventoryClickEvent e) {
        if (actions.containsKey(e.getSlot())) {
            actions.get(e.getSlot()).accept(e);
        }
    }

    // --- OUTILS DE CRÉATION D'ITEMS (Ceux qui causaient les erreurs) ---

    protected void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> action) {
        inventory.setItem(slot, item);
        if (action != null) {
            actions.put(slot, action);
        }
    }

    // Version Varargs (String...) pour accepter n'importe quel nombre de lignes de lore
    protected ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.displayName(Component.text(name));
            
            // Conversion Array -> List<Component>
            List<Component> loreList = Arrays.stream(lore)
                .map(line -> (Component) Component.text(line)) // Cast explicite pour éviter ambiguïté
                .toList();
                
            meta.lore(loreList);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }
}