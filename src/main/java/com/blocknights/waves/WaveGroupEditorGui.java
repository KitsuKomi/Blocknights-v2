package com.blocknights.waves;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.data.WaveGroup;
import com.blocknights.gui.InventoryGui;
import com.blocknights.maps.BnMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WaveGroupEditorGui extends InventoryGui {

    private final BlocknightsPlugin plugin;
    private final Player player;
    private final BnMap map;
    private final WaveGroup group;

    public WaveGroupEditorGui(BlocknightsPlugin plugin, Player player, BnMap map, WaveGroup group) {
        super(45, Component.text("Édition du Groupe"));
        this.plugin = plugin;
        this.player = player;
        this.map = map;
        this.group = group;
    }

    @Override
    public void init() {
        // --- 1. Type de Monstre (Centre) ---
        setItem(13, createItem(Material.SPAWNER, "§6Type: " + group.getMobType().name(), "§7Cliquez pour changer"), e -> {
            new MobSelectorGui(plugin, player, map, group).open(player);
        });

        // --- 2. Quantité (Gauche) ---
        setItem(20, createItem(Material.EMERALD, "§aQuantité: " + group.getCount(), 
            "§7Nombre de mobs à spawn", "", getControlsInt()), e -> {
            
            int change = e.isShiftClick() ? 5 : 1;
            if (e.isRightClick()) change = -change;
            
            int newVal = Math.max(1, group.getCount() + change);
            group.setCount(newVal);
            refresh();
        });

        // --- 3. Intervalle (Gauche) ---
        setItem(29, createItem(Material.CLOCK, "§eIntervalle: " + group.getInterval() + " ticks", 
            "§7Temps entre chaque mob", "§7(20 ticks = 1 seconde)", "", getControlsInt()), e -> {
            
            int change = e.isShiftClick() ? 10 : 1;
            if (e.isRightClick()) change = -change;
            
            int newVal = Math.max(1, group.getInterval() + change);
            group.setInterval(newVal);
            refresh();
        });

        // --- 4. Vie (Droite) ---
        setItem(24, createItem(Material.RED_DYE, "§cVie: " + group.getHealth() + " PV", 
            "§7Points de vie des mobs", "", getControlsDouble()), e -> {
            
            double change = e.isShiftClick() ? 5.0 : 1.0;
            if (e.isRightClick()) change = -change;
            
            double newVal = Math.max(1.0, group.getHealth() + change);
            group.setHealth(newVal);
            refresh();
        });

        // --- 5. Vitesse (Droite) ---
        setItem(33, createItem(Material.FEATHER, "§fVitesse: " + String.format("%.2f", group.getSpeed()), 
            "§7Vitesse de marche", "§7(0.25 = Zombie normal)", "", getControlsDouble()), e -> {
            
            double change = e.isShiftClick() ? 0.05 : 0.01;
            if (e.isRightClick()) change = -change;
            
            double newVal = Math.max(0.01, group.getSpeed() + change);
            // Arrondi à 2 décimales pour éviter les 0.250000001
            newVal = Math.round(newVal * 100.0) / 100.0;
            group.setSpeed(newVal);
            refresh();
        });
        
        // --- 6. Ligne / Lane (Bas) ---
        setItem(40, createItem(Material.RAIL, "§bLigne (Lane): " + group.getLaneIndex(), 
             "§7Sur quel chemin ils spawnent", "", "§eClic G: +1 | Clic D: -1"), e -> {
             
             int change = e.isRightClick() ? -1 : 1;
             int newVal = Math.max(0, group.getLaneIndex() + change);
             group.setLaneIndex(newVal);
             refresh();
        });

        // --- Navigation ---
        setItem(44, createItem(Material.BARRIER, "§cRetour"), e -> {
            // Retour au menu principal des vagues
            new WaveEditorGui(plugin, player, map).open(player);
        });
    }

    private void refresh() {
        plugin.getMapManager().saveActiveMap(); // Sauvegarde auto à chaque clic
        init(); // Redessine les items avec les nouvelles valeurs
    }

    private String getControlsInt() {
        return "§eClic G: +1 | Clic D: -1\n§6Shift: +/- 5";
    }
    
    private String getControlsDouble() {
        return "§eClic G: ++ | Clic D: --\n§6Shift: Gros changement";
    }
}