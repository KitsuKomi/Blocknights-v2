package com.blocknights.commands;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.game.operator.DeploymentGui; // On va le créer juste après
import com.blocknights.gui.WaveEditorGui; // Import Correct (package gui)
import com.blocknights.maps.BnMap;
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
        // Vérification Joueur
        if (!(sender instanceof Player player)) {
            plugin.getLang().send(sender, "cmd-player-only");
            return true;
        }

        // Aide par défaut
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            // --- JEU ---
            case "start":
                if (plugin.getSessionManager().isRunning()) {
                    plugin.getLang().send(player, "game-already-started");
                } else {
                    plugin.getSessionManager().startGame();
                }
                break;

            case "stop":
                plugin.getSessionManager().stopGame();
                break;

            // --- DEPLOIEMENT (Le Menu) ---
            case "menu":
            case "deploy":
            case "op":
                if (!plugin.getSessionManager().isRunning()) {
                    plugin.getLang().send(player, "game-not-started");
                    return true;
                }
                // On appelle le GUI (qu'on va coder proprement à l'étape suivante)
                new DeploymentGui(plugin, player).open(player);
                break;

            // --- ÉDITEUR & OUTILS ---
            case "editor":
            case "edit":
                plugin.getEditorManager().toggleEditor(player);
                break;

            case "lane":
                if (args.length < 2) {
                    plugin.getLang().send(player, "cmd-lane-usage");
                    return true;
                }
                try {
                    int lane = Integer.parseInt(args[1]);
                    plugin.getEditorManager().selectLane(player, lane);
                } catch (NumberFormatException e) {
                    plugin.getLang().send(player, "cmd-lane-invalid");
                }
                break;

            case "path":
                // On récupère la ligne active pour ajouter le point au bon endroit
                int currentLane = plugin.getEditorManager().getSelectedLane(player);
                plugin.getMapManager().addPathPoint(currentLane, player.getLocation());
                plugin.getLang().send(player, "editor-point-added", "{lane}", String.valueOf(currentLane));
                break;

            case "waves":
                BnMap map = plugin.getMapManager().getActiveMap();
                if (map == null) {
                    plugin.getLang().send(player, "map-none-active");
                    return true;
                }
                new WaveEditorGui(plugin, player, map).open(player);
                break;

            // --- GESTION DES MAPS ---
            case "map":
                handleMapCommand(player, args);
                break;

            // --- ADMIN ---
            case "reload":
                if (!player.hasPermission("blocknights.admin")) {
                    plugin.getLang().send(player, "cmd-no-permission");
                    return true;
                }
                plugin.getLang().loadMessages();
                plugin.getOperatorManager().reload();
                plugin.getLang().send(player, "cmd-reload-success");
                break;

            default:
                sendHelp(player);
                break;
        }
        return true;
    }

    private void handleMapCommand(Player p, String[] args) {
        if (args.length < 2) {
            plugin.getLang().send(p, "cmd-map-usage");
            return;
        }
        String action = args[1].toLowerCase();
        String mapName = args.length > 2 ? args[2] : "demo";

        if (action.equals("create")) {
            plugin.getMapManager().createMap(mapName);
            plugin.getLang().send(p, "cmd-map-created", "{name}", mapName);
        
        } else if (action.equals("load")) {
            if (plugin.getMapManager().loadMap(mapName)) {
                plugin.getLang().send(p, "cmd-map-loaded", "{name}", mapName);
            } else {
                plugin.getLang().send(p, "cmd-map-not-found");
            }
        }
    }

    private void sendHelp(Player p) {
        p.sendMessage(Component.text("=== Blocknights V2 ===", NamedTextColor.GOLD));
        p.sendMessage(Component.text("/bn editor", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/bn start", NamedTextColor.YELLOW));
        p.sendMessage(Component.text("/bn waves", NamedTextColor.YELLOW));
    }
}