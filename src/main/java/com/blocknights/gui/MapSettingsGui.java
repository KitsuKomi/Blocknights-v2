package com.blocknights.gui;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.maps.BnMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MapSettingsGui extends InventoryGui {

    private final BlocknightsPlugin plugin;
    private final Player player;
    private final BnMap map;

    public MapSettingsGui(BlocknightsPlugin plugin, Player player, BnMap map) {
        // On récupère le titre via LangManager (sans préfixe)
        super(27, Component.text(plugin.getLang().getRaw("gui-map-settings-title")));
        this.plugin = plugin;
        this.player = player;
        this.map = map;
    }

    @Override
    public void init() {
        // --- 1. Renommer (Slot 10) ---
        setItem(10, createItem(Material.NAME_TAG, 
            txt("gui-map-name", "{name}", map.getDisplayName()),
            txt("gui-map-id", "{id}", map.getId()),
            "",
            txt("gui-map-rename-hint")
        ), e -> {
            player.closeInventory();
            plugin.getEditorManager().startRenaming(player);
        });

        // --- 2. Vies du Nexus (Slot 12) ---
        setItem(12, createItem(Material.RED_DYE, 
            txt("gui-map-lives", "{lives}", String.valueOf(map.getInitialLives())),
            txt("gui-map-lives-lore"),
            "",
            txt("gui-ctrl-int"),
            txt("gui-ctrl-int-shift")
        ), e -> {
            int change = e.isShiftClick() ? 5 : 1;
            if (e.isRightClick()) change = -change;
            
            int newVal = Math.max(1, map.getInitialLives() + change);
            map.setInitialLives(newVal);
            refresh();
        });

        // --- 3. Argent de départ (Slot 14) ---
        setItem(14, createItem(Material.GOLD_INGOT, 
            txt("gui-map-money", "{money}", String.valueOf((int)map.getInitialMoney())),
            txt("gui-map-money-lore"),
            "",
            txt("gui-ctrl-double"),
            txt("gui-ctrl-double-shift")
        ), e -> {
            double change = e.isShiftClick() ? 100.0 : 10.0;
            if (e.isRightClick()) change = -change;
            
            double newVal = Math.max(0, map.getInitialMoney() + change);
            map.setInitialMoney(newVal);
            refresh();
        });

        // --- 4. Sauvegarder & Retour (Slot 22) ---
        setItem(22, createItem(Material.WRITABLE_BOOK, txt("gui-map-save")), e -> {
            plugin.getMapManager().saveActiveMap();
            // Ici on utilise send() normal car c'est un message chat
            plugin.getLang().send(player, "gui-map-saved");
            player.closeInventory();
        });
    }

    private void refresh() {
        plugin.getMapManager().saveActiveMap();
        init();
    }
    
    // Petite méthode utilitaire pour éviter de taper plugin.getLang().getRaw... à chaque ligne
    private String txt(String key, String... placeholders) {
        String msg = plugin.getLang().getRaw(key);
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            msg = msg.replace(placeholders[i], placeholders[i + 1]);
        }
        return msg.replace("&", "§"); // Conversion rapide des couleurs pour les items
    }
}