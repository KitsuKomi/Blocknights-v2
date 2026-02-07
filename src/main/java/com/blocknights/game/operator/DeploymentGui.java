package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.game.GamePlayer;
import com.blocknights.gui.InventoryGui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DeploymentGui extends InventoryGui {

    private final BlocknightsPlugin plugin;
    private final Player player;

    public DeploymentGui(BlocknightsPlugin plugin, Player player) {
        // Titre via LangManager
        super(54, Component.text(plugin.getLang().getRaw("gui-deploy-title")));
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void init() {
        GamePlayer gp = plugin.getSessionManager().getGamePlayer(player);
        int slot = 0;

        // On parcourt TOUS les opérateurs chargés
        for (OperatorDefinition def : plugin.getOperatorManager().getCatalog().values()) {
            if (slot >= 54) break;

            boolean canAfford = gp.getMoney() >= def.getCost();
            
            // État du lore (Vert ou Rouge selon l'argent)
            String statusLine = canAfford 
                ? plugin.getLang().getRaw("gui-deploy-select") 
                : plugin.getLang().getRaw("gui-deploy-locked");

            // Construction de l'item avec placeholders
            ItemStack item = createItem(
                def.getIcon().getType(), // Material
                txt("gui-deploy-name", "{name}", def.getName()), // Nom
                // Lore :
                txt("gui-deploy-class", "{class}", def.getEntityType().name()),
                txt("gui-deploy-stats", "{hp}", String.valueOf((int)def.getMaxHealth()), "{atk}", String.valueOf((int)def.getAtk())),
                "",
                txt("gui-deploy-cost", "{cost}", String.valueOf(def.getCost())),
                statusLine
            );

            setItem(slot, item, e -> {
                // 1. Vérification Argent
                if (!canAfford) {
                    double missing = def.getCost() - gp.getMoney();
                    plugin.getLang().send(player, "op-no-money", "{amount}", String.valueOf((int)missing));
                    return;
                }

                // 2. Logique de Sélection (On ne pose pas tout de suite, on sélectionne)
                plugin.getOperatorManager().selectOperatorForPlacement(player, def);
                player.closeInventory();
            });

            slot++;
        }
    }

    // Petit helper local pour simplifier les .replace() dans le GUI
    private String txt(String key, String... placeholders) {
        String msg = plugin.getLang().getRaw(key);
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            msg = msg.replace(placeholders[i], placeholders[i + 1]);
        }
        return msg.replace("&", "§");
    }
}