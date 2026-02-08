package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.gui.InventoryGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DeploymentMenu extends InventoryGui {

    private final BlocknightsPlugin plugin;
    private final Player player;

    public DeploymentMenu(BlocknightsPlugin plugin, Player player) {
        // Titre: "Recrutement"
        super(54, Component.text(plugin.getLang().get(player, "gui.deploy.title", "&8Centre de Recrutement")));
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void init() {
        // On récupère la liste de tous les opérateurs chargés
        List<OperatorDefinition> ops = new ArrayList<>(plugin.getOperatorManager().getCatalog().values());
        
        // On trie par coût (du moins cher au plus cher)
        ops.sort((a, b) -> Double.compare(a.getCost(), b.getCost()));

        int slot = 0;
        for (OperatorDefinition def : ops) {
            if (slot >= 53) break; // Sécurité si trop d'unités

            // Vérification argent (Vert si on peut acheter, Rouge sinon)
            double playerMoney = plugin.getSessionManager().getGamePlayer(player).getMoney();
            boolean canAfford = playerMoney >= def.getCost();
            
            String colorName = canAfford ? "&a" : "&c";
            String status = canAfford ? "&eCliquez pour déployer" : "&cPas assez d'argent";

            // Construction de l'item
            ItemStack icon = createItem(
                def.getIcon().getType(), // Utilise l'icône définie dans le YAML (ou défaut)
                colorName + def.getName(),
                "&7Classe: &f" + def.getId(), // Ou Rôle
                "&7Coût: &6" + (int)def.getCost() + " ⛃",
                "",
                "&7Santé: &a" + (int)def.getMaxHealth(),
                "&7Attaque: &c" + (int)def.getAtk(),
                "",
                status
            );

            // Action au clic
            setItem(slot, icon, event -> {
                if (canAfford) {
                    // On ferme le menu
                    player.closeInventory();
                    
                    // On dit au Manager : "Ce joueur veut poser ce truc"
                    plugin.getOperatorManager().selectOperatorForPlacement(player, def);
                    
                    // Message
                    player.sendMessage(plugin.getLang().get(player, "game.placement-mode", "&aMode placement activé ! Clic droit sur le sol."));
                } else {
                    player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
                }
            });

            slot++;
        }

        // Remplissage optionnel des trous
        // fillBorders(...); 
    }
}