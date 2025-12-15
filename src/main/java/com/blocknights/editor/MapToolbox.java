package com.blocknights.editor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.editor.session.MapEditingSession;
import com.blocknights.editor.tools.EditorTool;

public class MapToolbox {

    public static final String INVENTORY_TITLE = ChatColor.AQUA + "Map Editor";

    private final BlocknightsPlugin plugin;

    public MapToolbox(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    public BlocknightsPlugin getPlugin() {
        return plugin;
    }

    public void openToolbox(Player player, MapEditingSession session) {
        Inventory inventory = Bukkit.createInventory(null, 27, INVENTORY_TITLE);
        List<EditorTool> tools = Arrays.asList(EditorTool.values());

        for (int slot = 0; slot < tools.size() && slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, createToolItem(tools.get(slot)));
        }

        player.openInventory(inventory);
        plugin.getLogger().info("Ouverture de la toolbox de l'éditeur pour " + player.getName());
        plugin.getLogger().info("Session courante : " + session.getSummary());
    }

    public ItemStack createToolItem(EditorTool tool) {
        ItemStack item = new ItemStack(tool.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(tool.getDisplayName());
            meta.setLore(tool.getLore());
            item.setItemMeta(meta);
        }
        return item;
    }

    public void giveTool(Player player, EditorTool tool) {
        ItemStack item = createToolItem(tool);
        player.getInventory().addItem(item);
        player.sendMessage(ChatColor.GREEN + "Outil " + ChatColor.stripColor(tool.getDisplayName())
                + " ajouté à votre inventaire.");
    }

    public Optional<EditorTool> identify(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return Optional.empty();
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return Optional.empty();
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        return Arrays.stream(EditorTool.values())
                .filter(tool -> ChatColor.stripColor(tool.getDisplayName()).equalsIgnoreCase(name))
                .findFirst();
    }
}
