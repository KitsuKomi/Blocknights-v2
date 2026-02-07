package com.blocknights.waves;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.data.WaveGroup;
import com.blocknights.gui.InventoryGui;
import com.blocknights.maps.BnMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class MobSelectorGui extends InventoryGui {

    private final BlocknightsPlugin plugin;
    private final Player player;
    private final BnMap map;
    private final WaveGroup group;

    // Liste des mobs supportés (pour éviter de mettre l'Ender Dragon par erreur)
    private static final List<EntityType> VALID_MOBS = Arrays.asList(
        EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, 
        EntityType.CAVE_SPIDER, EntityType.BLAZE, EntityType.CREEPER, 
        EntityType.WITCH, EntityType.PIGLIN, EntityType.ZOMBIFIED_PIGLIN,
        EntityType.MAGMA_CUBE, EntityType.SLIME, EntityType.IRON_GOLEM
    );

    public MobSelectorGui(BlocknightsPlugin plugin, Player player, BnMap map, WaveGroup group) {
        super(27, Component.text("Choisir le Monstre"));
        this.plugin = plugin;
        this.player = player;
        this.map = map;
        this.group = group;
    }

    @Override
    public void init() {
        int slot = 0;
        for (EntityType type : VALID_MOBS) {
            Material icon = getIcon(type);
            ItemStack item = createItem(icon, "§e" + type.name());
            
            // Si c'est le type actuel, on le fait briller (enchantement visuel)
            if (type == group.getMobType()) {
                item.editMeta(meta -> meta.setEnchantmentGlintOverride(true));
            }

            setItem(slot++, item, e -> {
                // Action: Changer le type et revenir à l'éditeur précédent
                group.setMobType(type);
                plugin.getMapManager().saveActiveMap(); // Sauvegarde
                new WaveGroupEditorGui(plugin, player, map, group).open(player);
            });
        }

        // Bouton Retour
        setItem(26, createItem(Material.ARROW, "§cRetour"), e -> 
            new WaveGroupEditorGui(plugin, player, map, group).open(player));
    }

    // Petit helper pour avoir une icône sympa selon le mob
    private Material getIcon(EntityType type) {
        try {
            return Material.valueOf(type.name() + "_SPAWN_EGG");
        } catch (IllegalArgumentException e) {
            return Material.NAME_TAG; // Fallback si l'oeuf n'existe pas
        }
    }
}