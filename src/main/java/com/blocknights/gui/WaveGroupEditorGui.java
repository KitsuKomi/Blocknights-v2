package com.blocknights.gui;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.data.WaveGroup;
import com.blocknights.maps.BnMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class WaveGroupEditorGui extends InventoryGui {

    private final BlocknightsPlugin plugin;
    private final Player player;
    private final BnMap map;
    private final WaveGroup group;

    // Liste des monstres valides pour un Tower Defense (On évite les Items, Bateaux, etc.)
    private static final List<EntityType> VALID_MOBS = Arrays.asList(
        EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, EntityType.CREEPER,
        EntityType.WITCH, EntityType.ENDERMAN, EntityType.BLAZE, EntityType.CAVE_SPIDER,
        EntityType.ZOMBIFIED_PIGLIN, EntityType.DROWNED, EntityType.PILLAGER, 
        EntityType.VINDICATOR, EntityType.RAVAGER, EntityType.WITHER_SKELETON,
        EntityType.HOGLIN, EntityType.ZOGLIN, EntityType.MAGMA_CUBE, EntityType.SLIME
    );

    public WaveGroupEditorGui(BlocknightsPlugin plugin, Player player, BnMap map, WaveGroup group) {
        super(27, Component.text(plugin.getLang().getRaw("gui-group-title")));
        this.plugin = plugin;
        this.player = player;
        this.map = map;
        this.group = group;
    }

    @Override
    public void init() {
        // 1. Type de Monstre (Slot 10)
        setItem(10, createItem(Material.ZOMBIE_HEAD, 
            txt("gui-group-type", "{type}", group.getMobType().name()),
            txt("gui-hint-cycle")
        ), e -> {
            // Logique de cycle dans la liste VALID_MOBS
            int index = VALID_MOBS.indexOf(group.getMobType());
            int nextIndex = (index + 1) % VALID_MOBS.size();
            group.setMobType(VALID_MOBS.get(nextIndex));
            refresh();
        });

        // 2. Quantité (Slot 12)
        setItem(12, createItem(Material.REDSTONE, 
            txt("gui-group-count", "{count}", String.valueOf(group.getCount())),
            txt("gui-hint-maths", "{small}", "1"),
            txt("gui-hint-maths-shift", "{big}", "5")
        ), e -> {
            int change = e.isShiftClick() ? 5 : 1;
            if (e.isRightClick()) change = -change;
            group.setCount(Math.max(1, group.getCount() + change));
            refresh();
        });

        // 3. Intervalle (Slot 14)
        setItem(14, createItem(Material.CLOCK, 
            txt("gui-group-interval", "{time}", String.format("%.1f", group.getInterval())),
            txt("gui-hint-maths", "{small}", "0.5")
        ), e -> {
            double change = e.isRightClick() ? -0.5 : 0.5;
            group.setInterval(Math.max(0.5, group.getInterval() + change));
            refresh();
        });

        // 4. PV (Slot 16)
        setItem(16, createItem(Material.GLISTERING_MELON_SLICE,
            txt("gui-group-hp", "{hp}", String.valueOf((int)group.getHealth())),
            txt("gui-hint-maths", "{small}", "5"),
            txt("gui-hint-maths-shift", "{big}", "20")
        ), e -> {
            double change = e.isShiftClick() ? 20.0 : 5.0;
            if (e.isRightClick()) change = -change;
            group.setHealth(Math.max(1.0, group.getHealth() + change));
            refresh();
        });

        // 5. Vitesse (Slot 19)
        setItem(19, createItem(Material.FEATHER,
            txt("gui-group-speed", "{speed}", String.format("%.2f", group.getSpeed())),
            txt("gui-hint-maths", "{small}", "0.05")
        ), e -> {
            double change = e.isRightClick() ? -0.05 : 0.05;
            // On borne la vitesse entre 0.05 (très lent) et 1.0 (très très vite)
            double newSpeed = Math.min(1.0, Math.max(0.05, group.getSpeed() + change));
            group.setSpeed(newSpeed);
            refresh();
        });

        // 6. Retour (Slot 22)
        setItem(22, createItem(Material.ARROW, txt("gui-group-return")), e -> {
            plugin.getMapManager().saveActiveMap();
            // Retour au menu parent
            new WaveEditorGui(plugin, player, map).open(player);
        });
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