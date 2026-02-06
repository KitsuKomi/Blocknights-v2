package com.blocknights.commands;

import com.blocknights.BlocknightsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BnCommands implements CommandExecutor {

    private final BlocknightsPlugin plugin;

    public BnCommands(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Seul un joueur peut utiliser cette commande.", NamedTextColor.RED));
            return true;
        }

        Player p = (Player) sender;

        if (args.length == 0) {
            sendHelp(p);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                plugin.getSessionManager().startGame();
                break;
            case "stop":
                plugin.getSessionManager().stopGame();
                break;
            case "editor":
            case "edit":
                plugin.getEditorManager().toggleEditor(p);
                break;
                
            // --- NOUVELLE COMMANDE POUR LES LIGNES ---
            case "lane":
                if (args.length < 2) {
                    p.sendMessage(Component.text("Usage: /bn lane <numéro>", NamedTextColor.RED));
                    return true;
                }
                try {
                    int lane = Integer.parseInt(args[1]);
                    plugin.getEditorManager().selectLane(p, lane);
                } catch (NumberFormatException e) {
                    p.sendMessage(Component.text("Numéro invalide.", NamedTextColor.RED));
                }
                break;
            // -----------------------------------------

            case "path":
                // Utilise la méthode de compatibilité qu'on vient de remettre dans MapManager
                plugin.getMapManager().addPathPoint(p.getLocation());
                p.sendMessage(Component.text("Point ajouté au chemin principal !", NamedTextColor.GREEN));
                break;
                
            case "op":
                plugin.getOperatorManager().placeOperator(p);
                break;
            case "map":
                if (args.length < 2) {
                    p.sendMessage(Component.text("Usage: /bn map <create/load> <nom>", NamedTextColor.RED));
                    return true;
                }
                String action = args[1].toLowerCase();
                String mapName = args.length > 2 ? args[2] : "demo";

                if (action.equals("create")) {
                    plugin.getMapManager().createMap(mapName);
                    p.sendMessage(Component.text("Map '" + mapName + "' créée !", NamedTextColor.GREEN));
                } else if (action.equals("load")) {
                    if (plugin.getMapManager().loadMap(mapName)) {
                        p.sendMessage(Component.text("Map '" + mapName + "' chargée !", NamedTextColor.GREEN));
                    } else {
                        p.sendMessage(Component.text("Map introuvable.", NamedTextColor.RED));
                    }
                }
                break;
            default:
                sendHelp(p);
                break;
        }
        return true;
    }

    private void sendHelp(Player p) {
        p.sendMessage(Component.text("=== Blocknights V2 Commandes ===", NamedTextColor.GOLD));
        p.sendMessage(Component.text("/bn editor ", NamedTextColor.YELLOW).append(Component.text("- Activer le mode construction (Baguette)", NamedTextColor.WHITE)));
        p.sendMessage(Component.text("/bn lane <0/1/2> ", NamedTextColor.YELLOW).append(Component.text("- Changer de ligne à tracer", NamedTextColor.WHITE)));
        p.sendMessage(Component.text("/bn start ", NamedTextColor.YELLOW).append(Component.text("- Lancer la partie", NamedTextColor.WHITE)));
    }
}