package com.blocknights.gui;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.data.WaveGroup;
import com.blocknights.maps.BnMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class WaveGroupEditorGui extends InventoryGui {

    private final BlocknightsPlugin plugin;
    private final Player player;
    private final BnMap map;
    private final WaveGroup group;

    public WaveGroupEditorGui(BlocknightsPlugin plugin, Player player, BnMap map, WaveGroup group) {
        super(27, Component.text("Éditer le Groupe"));
        this.plugin = plugin;
        this.player = player;
        this.map = map;
        this.group = group;
    }

    @Override
    public void init() {
        // 1. Type d'ennemi (Slot 10)
        setItem(10, createItem(Material.ZOMBIE_HEAD, 
            "§eType: " + group.getEnemyType().name(),
            "§7Clic pour changer (Cycle)"
        ), e -> {
            // Cycle simple : ZOMBIE -> SKELETON -> SPIDER -> ZOMBIE
            EntityType current = group.getEnemyType();
            EntityType next = EntityType.ZOMBIE;
            if (current == EntityType.ZOMBIE) next = EntityType.SKELETON;
            else if (current == EntityType.SKELETON) next = EntityType.SPIDER;
            else if (current == EntityType.SPIDER) next = EntityType.ZOMBIE;
            
            group.setEnemyType(next);
            refresh();
        });

        // 2. Quantité (Slot 12)
        setItem(12, createItem(Material.REDSTONE, 
            "§cQuantité: " + group.getCount(),
            "§eClic G: +1 | Clic D: -1",
            "§6Shift: +/- 5"
        ), e -> {
            int change = e.isShiftClick() ? 5 : 1;
            if (e.isRightClick()) change = -change;
            group.setCount(Math.max(1, group.getCount() + change));
            refresh();
        });

        // 3. Intervalle (Slot 14)
        setItem(14, createItem(Material.CLOCK, 
            "§bIntervalle: " + group.getInterval() + "s",
            "§7Temps entre chaque spawn",
            "§eClic G: +0.5s | Clic D: -0.5s"
        ), e -> {
            double change = 0.5;
            if (e.isRightClick()) change = -change;
            group.setInterval(Math.max(0.5, group.getInterval() + change));
            refresh();
        });

        // 4. Retour / Sauvegarder (Slot 22)
        setItem(22, createItem(Material.ARROW, "§aSauvegarder et Retour"), e -> {
            plugin.getMapManager().saveActiveMap();
            // On revient au menu parent (WaveEditorGui)
            new WaveEditorGui(plugin, player, map).open(player);
        });
    }

    private void refresh() {
        plugin.getMapManager().saveActiveMap(); // Sauvegarde à chaque clic pour pas perdre de data
        init();
    }
}