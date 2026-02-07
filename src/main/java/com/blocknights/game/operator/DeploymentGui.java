package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.game.GamePlayer;
import com.blocknights.gui.InventoryGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class DeploymentGui extends InventoryGui {

    private final BlocknightsPlugin plugin;
    private final Player player;

    public DeploymentGui(BlocknightsPlugin plugin, Player player) {
        super(9, Component.text("Déploiement")); // Une seule ligne (Hotbar style)
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void init() {
        // Pour l'instant, on hardcode 2 opérateurs.
        // Plus tard, on récupérera le "Deck" du joueur depuis sa config.
        
        // Slot 0 : Sniper
        addOperatorButton(0, "sniper", Material.BOW, "Sniper", 100);
        
        // Slot 1 : Caster
        addOperatorButton(1, "caster", Material.POTION, "Caster", 250);
        
        // Slot 2 : Defender (Futur)
        addOperatorButton(2, "defender", Material.SHIELD, "Defender", 300);
    }

    private void addOperatorButton(int slot, String opId, Material icon, String name, double cost) {
        GamePlayer gp = plugin.getSessionManager().getGamePlayer(player);
        boolean canAfford = gp.getMoney() >= cost;

        // Couleur : Vert si on peut acheter, Rouge sinon
        String color = canAfford ? "§a" : "§c";
        
        ItemStack item = createItem(icon, 
            color + name, 
            "§7Coût: §e" + (int)cost + " ⛃",
            "",
            canAfford ? "§eClic pour sélectionner" : "§cPas assez de LMD"
        );

        setItem(slot, item, e -> {
            if (!canAfford) {
                plugin.getLang().send(player, "op-no-money", "{amount}", String.valueOf(cost));
                return;
            }

            // Donner l'item de déploiement au joueur
            giveDeploymentItem(player, opId, name, cost, icon);
            player.closeInventory();
        });
    }

    private void giveDeploymentItem(Player p, String opId, String name, double cost, Material icon) {
        ItemStack item = new ItemStack(icon);
        item.editMeta(meta -> {
            meta.displayName(Component.text("§aDéployer : " + name));
            
            // CORRECTION ICI : "lore" au lieu de "setLore"
            meta.lore(java.util.List.of(
                Component.text("§7Coût: §e" + (int)cost), 
                Component.text("§7Clic Droit sur une tuile verte")
            ));
            
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            
            // Stockage de l'ID (Opérateur)
            meta.getPersistentDataContainer().set(
                new org.bukkit.NamespacedKey(plugin, "op_id"), 
                PersistentDataType.STRING, 
                opId
            );
        });

        p.getInventory().setItemInMainHand(item);
        p.sendMessage(Component.text("§eSélectionnez une zone de déploiement (Particules Vertes)."));
    }
}