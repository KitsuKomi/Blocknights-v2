package com.blocknights.gui;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.data.WaveDefinition;
import com.blocknights.data.WaveGroup;
import com.blocknights.maps.BnMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WaveEditorGui extends InventoryGui {

    private final BlocknightsPlugin plugin;
    private final Player player;
    private final BnMap map;

    public WaveEditorGui(BlocknightsPlugin plugin, Player player, BnMap map) {
        super(54, Component.text(plugin.getLang().getRaw("gui-wave-title")));
        this.plugin = plugin;
        this.player = player;
        this.map = map;
    }

    @Override
    public void init() {
        int slot = 0;
        for (WaveDefinition wave : map.getWaves()) {
            if (slot >= 45) break;

            ItemStack item = createItem(Material.CREEPER_HEAD, 
                txt("gui-wave-item-name", "{id}", String.valueOf(wave.getId())), 
                txt("gui-wave-item-infos", 
                    "{count}", String.valueOf(wave.getGroups().size()),
                    "{time}", String.valueOf(wave.getDelayBeforeNext())
                ),
                "",
                txt("gui-wave-action-edit"),
                txt("gui-wave-action-delete")
            );

            setItem(slot, item, e -> {
                if (e.isLeftClick()) {
                    if (!wave.getGroups().isEmpty()) {
                        // Ouvre le sous-menu (Assure-toi d'avoir créé WaveGroupEditorGui aussi !)
                        new WaveGroupEditorGui(plugin, player, map, wave.getGroups().get(0)).open(player);
                    } else {
                        plugin.getLang().send(player, "gui-wave-empty-error");
                    }
                } else if (e.isRightClick()) {
                    map.getWaves().remove(wave);
                    refresh();
                }
            });
            slot++;
        }

        // Bouton Ajouter
        setItem(49, createItem(Material.EMERALD_BLOCK, txt("gui-wave-add")), e -> {
            int nextId = map.getWaves().size() + 1;
            WaveDefinition newWave = new WaveDefinition(nextId);
            newWave.addGroup(new WaveGroup()); 
            map.addWave(newWave);
            refresh();
        });
        
        // Bouton Fermer
        setItem(53, createItem(Material.BARRIER, txt("gui-common-close")), e -> player.closeInventory());
    }

    private void refresh() {
        plugin.getMapManager().saveActiveMap();
        inventory.clear();
        actions.clear();
        init();
    }

    private String txt(String key, String... placeholders) {
        String msg = plugin.getLang().getRaw(key);
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            msg = msg.replace(placeholders[i], placeholders[i + 1]);
        }
        return msg.replace("&", "§");
    }
}