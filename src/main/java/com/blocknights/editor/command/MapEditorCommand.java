package com.blocknights.editor.command;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.blocknights.editor.MapEditorManager;
import com.blocknights.editor.session.MapEditingSession;

public class MapEditorCommand implements CommandExecutor {

    private final MapEditorManager manager;

    public MapEditorCommand(MapEditorManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Commande réservée aux joueurs.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            openToolbox(player);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
        case "reset":
            Optional<MapEditingSession> toReset = manager.getSession(player);
            if (!toReset.isPresent()) {
                player.sendMessage(ChatColor.YELLOW + "Aucune session. Ouverture d'une nouvelle.");
                openToolbox(player);
                return true;
            }
            toReset.get().reset();
            player.sendMessage(ChatColor.GRAY + "Session réinitialisée.");
            break;
        case "save":
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Précisez un nom : /mapeditor save <nom>");
                return true;
            }
            MapEditingSession sessionToSave = manager.getSession(player).orElseGet(() -> manager.openSession(player));
            String mapName = args[1];
            sessionToSave.setSchematicName(mapName);
            if (manager.saveSession(sessionToSave, mapName)) {
                player.sendMessage(ChatColor.GREEN + "Map sauvegardée sous " + mapName + ".");
            } else {
                player.sendMessage(ChatColor.RED
                        + "Impossible de sauvegarder : données manquantes. Vérifiez spawn, end, path, placements et FAWE box.");
            }
            break;
        case "load":
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Précisez un nom : /mapeditor load <nom>");
                return true;
            }
            String name = args[1];
            Optional<MapEditingSession> loaded = manager.loadSession(player, name);
            if (loaded.isPresent()) {
                player.sendMessage(ChatColor.GREEN + "Map " + name + " chargée. Toolbox ouverte.");
                manager.getToolbox().openToolbox(player, loaded.get());
            } else {
                player.sendMessage(ChatColor.RED + "Map " + name + " introuvable.");
            }
            break;
        case "summary":
            Optional<MapEditingSession> summary = manager.getSession(player);
            if (summary.isPresent()) {
                player.sendMessage(ChatColor.AQUA + "Résumé : " + summary.get().getSummary());
            } else {
                player.sendMessage(ChatColor.RED + "Aucune session active.");
            }
            break;
        default:
            openToolbox(player);
            break;
        }
        return true;
    }

    private void openToolbox(Player player) {
        Optional<MapEditingSession> existing = manager.getSession(player);
        MapEditingSession session = existing.orElseGet(() -> manager.openSession(player));
        manager.getToolbox().openToolbox(player, session);
        player.sendMessage(ChatColor.GREEN + "Toolbox de l'éditeur de maps ouverte.");
    }
}
