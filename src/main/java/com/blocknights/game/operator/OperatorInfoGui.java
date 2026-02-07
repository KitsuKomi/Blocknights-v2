package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.gui.InventoryGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OperatorInfoGui extends InventoryGui {

    private final BlocknightsPlugin plugin;
    private final Player player;
    private final GameOperator operator;

    public OperatorInfoGui(BlocknightsPlugin plugin, Player player, GameOperator operator) {
        super(27, Component.text("Opérateur : " + operator.getDefinition().getName()));
        this.plugin = plugin;
        this.player = player;
        this.operator = operator;
    }

    @Override
    public void init() {
        OperatorDefinition def = operator.getDefinition();

        // Sécurité PV
        double currentHp = 0;
        LivingEntity entity = operator.getEntity();
        if (entity != null && entity.isValid()) {
            currentHp = entity.getHealth();
        }

        // --- Slot 13 : INFO ---
        // On utilise txt() (helper vu précédemment) ou plugin.getLang().getRaw()
        setItem(13, createItem(Material.PLAYER_HEAD, 
            plugin.getLang().getRaw("gui-op-stats-name").replace("{name}", def.getName()),
            plugin.getLang().getRaw("gui-op-stats-cost").replace("{cost}", String.valueOf(def.getCost())),
            "",
            plugin.getLang().getRaw("gui-op-stats-hp")
                .replace("{current}", String.valueOf((int)currentHp))
                .replace("{max}", String.valueOf((int)def.getMaxHealth())),
            plugin.getLang().getRaw("gui-op-stats-atk").replace("{atk}", String.valueOf(def.getAtk())),
            plugin.getLang().getRaw("gui-op-stats-def").replace("{def}", String.valueOf(def.getDef())),
            plugin.getLang().getRaw("gui-op-stats-block")
                .replace("{current}", String.valueOf(operator.getBlockedEnemies().size()))
                .replace("{max}", String.valueOf(def.getBlockCount()))
        ), e -> {
            // Sécurité : même si l'action est vide, InventoryGui doit empêcher le vol.
            // e.setCancelled(true) est généralement géré par la classe parent, 
            // mais c'est bien de le savoir.
        });

        // --- Slot 15 : RETRAITE ---
        // Correction : On demande au manager combien on rembourse, on ne le calcule pas ici
        int refund = plugin.getOperatorManager().getRefundAmount(def);
        
        setItem(15, createItem(Material.RED_CONCRETE, 
            plugin.getLang().getRaw("gui-op-retreat-title"),
            plugin.getLang().getRaw("gui-op-retreat-lore1"),
            plugin.getLang().getRaw("gui-op-retreat-refund").replace("{amount}", String.valueOf(refund)),
            "",
            plugin.getLang().getRaw("gui-op-retreat-confirm")
        ), e -> {
            plugin.getOperatorManager().retreatOperator(player, operator);
            player.closeInventory();
        });

        // Fermer
        setItem(26, createItem(Material.ARROW, "§7Fermer"), e -> player.closeInventory());
    }
}