package com.blocknights.waves;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.data.WaveDefinition;
import com.blocknights.data.WaveGroup;
import com.blocknights.gui.InventoryGui;
import com.blocknights.maps.BnMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
public class WaveEditorGui extends InventoryGui {

    private final BlocknightsPlugin plugin;
    private final BnMap map;
    private final Player player;

    public WaveEditorGui(BlocknightsPlugin plugin, Player player, BnMap map) {
        super(54, Component.text("Édition des Vagues: " + map.getId()));
        this.plugin = plugin;
        this.player = player;
        this.map = map;
    }

    @Override
    public void init() {
        inventory.clear();
        actions.clear();

        // 1. Lister les vagues existantes
        int slot = 0;
        for (WaveDefinition wave : map.getWaves()) {
            ItemStack item = createItem(Material.CREEPER_HEAD, 
                "§eVague #" + wave.getId(), 
                "§7Groupes: " + wave.getGroups().size(),
                "§7Délai après: " + wave.getDelayBeforeNext() + "s",
                "",
                "§aClic G: Éditer",
                "§cClic D: Supprimer"
            );

            setItem(slot, item, e -> {
                if (e.isLeftClick()) {
                // On ouvre l'éditeur pour le PREMIER groupe de cette vague 
                // (Pour l'instant on gère 1 groupe par vague pour simplifier, 
                // ou alors on ouvre un menu intermédiaire "Liste des groupes")
                
                if (!wave.getGroups().isEmpty()) {
                    WaveGroup groupToEdit = wave.getGroups().get(0);
                    new WaveGroupEditorGui(plugin, player, map, groupToEdit).open(player);
                } else {
                    player.sendMessage("§cCette vague est vide (Bug étrange) !");
                }
                } else if (e.isRightClick()) {
                    // Supprimer
                    map.getWaves().remove(wave);
                    plugin.getMapManager().saveActiveMap(); // Sauvegarde direct
                    init(); // Rafraîchir
                }
            });
            slot++;
        }

        // 2. Bouton "Ajouter une Vague"
        setItem(49, createItem(Material.EMERALD_BLOCK, "§a+ Nouvelle Vague"), e -> {
            int nextId = map.getWaves().size() + 1;
            WaveDefinition newWave = new WaveDefinition(nextId);
            
            // On ajoute un groupe par défaut pour pas que ce soit vide
            newWave.addGroup(new com.blocknights.data.WaveGroup()); 
            
            map.addWave(newWave);
            plugin.getMapManager().saveActiveMap();
            init(); // Rafraîchir le menu
        });
        
        // 3. Bouton Fermer
        setItem(53, createItem(Material.BARRIER, "§cFermer"), e -> player.closeInventory());
    }
}