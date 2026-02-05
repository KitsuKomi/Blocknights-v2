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
            // Message système en rouge
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
            case "path":
                plugin.getMapManager().addPathPoint(p.getLocation());
                p.sendMessage(Component.text("Point de chemin ajouté !", NamedTextColor.GREEN));
                break;
            case "op":
                plugin.getOperatorManager().placeOperator(p);
                break;
            case "editor":
            case "edit":
                plugin.getEditorManager().toggleEditor(p);
                break;
            default:
                sendHelp(p);
                break;
        }
        return true;
    }

    private void sendHelp(Player p) {
        p.sendMessage(Component.text("=== Blocknights V2 Commandes ===", NamedTextColor.GOLD));
        
        // Construction de ligne : "Commade - Description"
        p.sendMessage(Component.text("/bn start ", NamedTextColor.YELLOW)
                .append(Component.text("- Lancer la partie", NamedTextColor.WHITE)));
                
        p.sendMessage(Component.text("/bn stop ", NamedTextColor.YELLOW)
                .append(Component.text("- Arrêter la partie", NamedTextColor.WHITE)));
                
        p.sendMessage(Component.text("/bn path ", NamedTextColor.YELLOW)
                .append(Component.text("- Ajouter un point de passage", NamedTextColor.WHITE)));
                
        p.sendMessage(Component.text("/bn op ", NamedTextColor.YELLOW)
                .append(Component.text("- Placer une tour de test", NamedTextColor.WHITE)));
    }
}