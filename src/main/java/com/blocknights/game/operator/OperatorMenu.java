package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.gui.InventoryGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.ArrayList;

public class OperatorMenu extends InventoryGui {

    private final BlocknightsPlugin plugin;
    private final GameOperator op;
    private final Player player;

    public OperatorMenu(BlocknightsPlugin plugin, Player player, GameOperator op) {
        // Titre dynamique via LangManager
        super(27, Component.text(
            plugin.getLang().get(player, "gui.operator.title")
                  .replace("{name}", op.getDefinition().getName())
        ));
        this.plugin = plugin;
        this.player = player;
        this.op = op;
    }

    /**
     * Remplit les bords du menu avec un item (vitre) pour faire joli.
     * Ne remplace pas les items déjà posés.
     */
    protected void fillBorders(ItemStack item) {
        int size = inventory.getSize();
        int rows = size / 9;

        for (int i = 0; i < size; i++) {
            // Logique : Si c'est la 1ère ligne, la dernière, la colonne de gauche ou de droite
            boolean isBorder = (i < 9) || (i >= size - 9) || (i % 9 == 0) || ((i + 1) % 9 == 0);

            if (isBorder) {
                // On ne remplace pas les boutons existants
                if (inventory.getItem(i) == null || inventory.getItem(i).getType() == org.bukkit.Material.AIR) {
                    inventory.setItem(i, item);
                }
            }
        }
    }
    
    @Override
    public void init() {
        // --- 1. INFO ITEM (Slot 11) ---
        double hp = op.getCurrentHealth();
        double maxHp = op.getDefinition().getMaxHealth();
        
        // Récupération des strings via LangManager
        String infoName = plugin.getLang().get(player, "gui.operator.info.name");
        String lblClass = plugin.getLang().get(player, "gui.operator.info.class")
                               .replace("{value}", op.getDefinition().getName());
        String lblHealth = plugin.getLang().get(player, "gui.operator.info.health")
                               .replace("{current}",String.valueOf((int)hp))
                               .replace("{max}", String.valueOf((int)maxHp));
        String lblAtk = plugin.getLang().get(player, "gui.operator.info.atk")
                               .replace("{value}", String.valueOf(op.getDefinition().getAtk()));
        String lblDef = plugin.getLang().get(player, "gui.operator.info.def")
                               .replace("{value}", String.valueOf(op.getDefinition().getDef()));

        // Création de l'item
        setItem(11, createItem(Material.PAPER, infoName, lblClass, lblHealth, lblAtk, lblDef), null);


        // --- 2. BOUTON RETRAITE (Slot 15) ---
        int refund = plugin.getOperatorManager().getRefundAmount(op.getDefinition());
        String retreatName = plugin.getLang().get(player, "gui.operator.retreat.name");
        
        // Construction du Lore (Liste)
        // Note: Si ton LangManager a une méthode getList(), utilise-la. 
        // Sinon, on fait une liste manuelle pour gérer le placeholder {amount}
        List<String> retreatLore = new ArrayList<>();
        // On suppose ici que tu récupères la liste brute depuis la config, ou ligne par ligne
        // Pour faire simple et robuste sans connaitre ton LangManager par cœur :
        retreatLore.add(plugin.getLang().get(player, "gui.operator.retreat.lore.0", "&7Renvoyer cet opérateur.")); // Fallback text
        retreatLore.add("");
        retreatLore.add(plugin.getLang().get(player, "gui.operator.retreat.lore.2", "&eRemboursement: &6+{amount}").replace("{amount}", String.valueOf(refund)));
        retreatLore.add(plugin.getLang().get(player, "gui.operator.retreat.lore.3", "&c⚠ Action irréversible !"));

        // Conversion List -> Array pour ta méthode createItem
        ItemStack sellItem = createItem(Material.RED_DYE, retreatName, retreatLore.toArray(new String[0]));

        setItem(15, sellItem, e -> {
            plugin.getOperatorManager().retreatOperator(player, op);
            player.closeInventory();
        });


        // --- 3. BOUTON FERMER (Slot 26) ---
        String closeName = plugin.getLang().get(player, "gui.operator.close");
        setItem(26, createItem(Material.BARRIER, closeName), e -> player.closeInventory());
        
        // Décoration
        fillBorders(createItem(Material.GRAY_STAINED_GLASS_PANE, " "));
    }
}