package com.blocknights.editor.tools;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum EditorTool {

    WAND_SPAWN(Material.BLAZE_ROD, ChatColor.GREEN + "Wand Spawn/End",
            Arrays.asList(ChatColor.WHITE + "Clic gauche : définir le spawn", ChatColor.WHITE + "Clic droit : définir l'arrivée")),
    WAND_PATH(Material.STICK, ChatColor.AQUA + "Wand Path",
            Arrays.asList(ChatColor.WHITE + "Clic gauche : ajouter un point", ChatColor.WHITE + "Clic droit : retirer le dernier")),
    WAND_OPERATOR_MELEE(Material.IRON_SWORD, ChatColor.RED + "Placement Opérateur Melee",
            Arrays.asList(ChatColor.WHITE + "Clic : enregistrer un point de placement")),
    WAND_OPERATOR_RANGED(Material.BOW, ChatColor.BLUE + "Placement Opérateur Ranged",
            Arrays.asList(ChatColor.WHITE + "Clic : enregistrer un point de placement")),
    WAND_OPERATOR_BOTH(Material.SHIELD, ChatColor.GOLD + "Placement Opérateur Hybride",
            Arrays.asList(ChatColor.WHITE + "Clic : enregistrer un point de placement")),
    FAWE_BOX(Material.IRON_AXE, ChatColor.DARK_GREEN + "Wand FAWE Box",
            Arrays.asList(ChatColor.WHITE + "pos1 / pos2", ChatColor.WHITE + "Sauvegarde schematic")),
    VISUALISER(Material.ENDER_EYE, ChatColor.LIGHT_PURPLE + "Visualiser", Arrays.asList(ChatColor.WHITE + "Afficher les hologrammes")),
    NETTOYER(Material.BARRIER, ChatColor.GRAY + "Nettoyer", Arrays.asList(ChatColor.WHITE + "Réinitialiser les points")),
    SAUVEGARDER(Material.BOOK, ChatColor.GREEN + "Sauvegarder la map", Arrays.asList(ChatColor.WHITE + "Persister la configuration")),
    CHARGER(Material.CHEST, ChatColor.YELLOW + "Charger une map", Arrays.asList(ChatColor.WHITE + "Charger depuis la persistance")),
    TESTER(Material.TNT, ChatColor.RED + "Tester la map", Arrays.asList(ChatColor.WHITE + "Lancer une vague de test"));

    private final Material material;
    private final String displayName;
    private final List<String> lore;

    EditorTool(Material material, String displayName, List<String> lore) {
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getPlainName() {
        return ChatColor.stripColor(displayName);
    }
}
