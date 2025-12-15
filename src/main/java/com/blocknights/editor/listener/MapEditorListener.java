package com.blocknights.editor.listener;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.blocknights.editor.MapEditorManager;
import com.blocknights.editor.MapToolbox;
import com.blocknights.editor.session.MapEditingSession;
import com.blocknights.editor.tools.EditorTool;
import com.blocknights.editor.tools.OperatorPlacementType;

public class MapEditorListener implements Listener {

    private final MapEditorManager manager;

    public MapEditorListener(MapEditorManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!event.getView().getTitle().equals(MapToolbox.INVENTORY_TITLE)) {
            return;
        }

        event.setCancelled(true);
        ItemStack current = event.getCurrentItem();
        if (current == null || event.getWhoClicked() == null || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Optional<MapEditingSession> sessionOpt = manager.getSession(player);
        if (!sessionOpt.isPresent()) {
            player.sendMessage(ChatColor.RED + "Aucune session d'édition active.");
            player.closeInventory();
            return;
        }

        MapEditingSession session = sessionOpt.get();
        Optional<EditorTool> toolOpt = manager.getToolbox().identify(current);
        if (!toolOpt.isPresent()) {
            return;
        }

        EditorTool tool = toolOpt.get();
        switch (tool) {
        case NETTOYER:
            session.reset();
            player.sendMessage(ChatColor.GRAY + "Session nettoyée : tous les points ont été réinitialisés.");
            break;
        case VISUALISER:
            sendSummary(player, session);
            break;
        case SAUVEGARDER:
            if (session.getSchematicName() == null) {
                player.sendMessage(ChatColor.YELLOW + "Définissez un nom via /mapeditor save <nom> avant de sauvegarder.");
                return;
            }
            if (manager.saveSession(session, session.getSchematicName())) {
                player.sendMessage(ChatColor.GREEN + "Map sauvegardée : " + session.getSchematicName());
            } else {
                player.sendMessage(ChatColor.RED
                        + "Impossible de sauvegarder : complétez spawn/end/path/placements/FAWE et nom de schematic.");
            }
            break;
        case CHARGER:
            player.sendMessage(ChatColor.YELLOW + manager.listSavedMaps());
            break;
        case TESTER:
            if (session.isValid()) {
                player.sendMessage(ChatColor.RED + "Test virtuel prêt : toutes les données nécessaires sont en place.");
            } else {
                player.sendMessage(ChatColor.DARK_RED
                        + "Map incomplète. Vérifiez les points requis (spawn/end/path/placements/FAWE + schematic).");
            }
            break;
        default:
            manager.getToolbox().giveTool(player, tool);
            break;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        Optional<MapEditingSession> sessionOpt = manager.getSession(player);
        if (!sessionOpt.isPresent()) {
            return;
        }

        MapEditingSession session = sessionOpt.get();
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        Optional<EditorTool> toolOpt = manager.getToolbox().identify(item);
        if (!toolOpt.isPresent()) {
            return;
        }

        EditorTool tool = toolOpt.get();
        Action action = event.getAction();
        Location target = getTargetLocation(event, player);
        switch (tool) {
        case WAND_SPAWN:
            event.setCancelled(true);
            handleSpawnEnd(player, session, action, target);
            break;
        case WAND_PATH:
            event.setCancelled(true);
            handlePath(player, session, action, target);
            break;
        case WAND_OPERATOR_MELEE:
        case WAND_OPERATOR_RANGED:
        case WAND_OPERATOR_BOTH:
            event.setCancelled(true);
            handleOperatorPlacement(player, session, tool, target);
            break;
        case FAWE_BOX:
            event.setCancelled(true);
            handleFaweBox(player, session, action, target);
            break;
        default:
            break;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        manager.closeSession(event.getPlayer());
    }

    private void handleSpawnEnd(Player player, MapEditingSession session, Action action, Location location) {
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            session.setSpawn(location);
            player.sendMessage(ChatColor.GREEN + "Spawn défini en " + format(location));
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            session.setEnd(location);
            player.sendMessage(ChatColor.GREEN + "Arrivée définie en " + format(location));
        }
    }

    private void handlePath(Player player, MapEditingSession session, Action action, Location location) {
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            session.addPathPoint(location);
            player.sendMessage(ChatColor.AQUA + "Point ajouté : " + format(location) + " (" + session.getPathPoints().size()
                    + " au total)");
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (session.getPathPoints().isEmpty()) {
                player.sendMessage(ChatColor.RED + "Aucun point à retirer.");
                return;
            }
            session.removeLastPathPoint();
            player.sendMessage(ChatColor.AQUA + "Dernier point retiré. Restants : " + session.getPathPoints().size());
        }
    }

    private void handleOperatorPlacement(Player player, MapEditingSession session, EditorTool tool, Location location) {
        OperatorPlacementType type = OperatorPlacementType.BOTH;
        if (tool == EditorTool.WAND_OPERATOR_MELEE) {
            type = OperatorPlacementType.MELEE;
        } else if (tool == EditorTool.WAND_OPERATOR_RANGED) {
            type = OperatorPlacementType.RANGED;
        }
        session.addOperatorPlacement(type, location);
        player.sendMessage(ChatColor.GOLD + "Placement " + type.name() + " enregistré en " + format(location));
    }

    private void handleFaweBox(Player player, MapEditingSession session, Action action, Location location) {
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            session.setPos1(location);
            player.sendMessage(ChatColor.DARK_GREEN + "Pos1 enregistrée en " + format(location));
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            session.setPos2(location);
            player.sendMessage(ChatColor.DARK_GREEN + "Pos2 enregistrée en " + format(location));
        }
    }

    private String format(Location location) {
        return location.getWorld().getName() + " " + location.getBlockX() + ", " + location.getBlockY() + ", "
                + location.getBlockZ();
    }

    private Location getTargetLocation(PlayerInteractEvent event, Player player) {
        return event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : player.getLocation();
    }

    private void sendSummary(Player player, MapEditingSession session) {
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Résumé complet :");
        player.sendMessage(ChatColor.AQUA + "- " + session.getSummary());
        player.sendMessage(ChatColor.AQUA + "- Spawn : " + (session.getSpawn() != null ? format(session.getSpawn()) : "none"));
        player.sendMessage(ChatColor.AQUA + "- End : " + (session.getEnd() != null ? format(session.getEnd()) : "none"));
        player.sendMessage(ChatColor.AQUA + "- FAWE Pos1 : " + (session.getPos1() != null ? format(session.getPos1()) : "none"));
        player.sendMessage(ChatColor.AQUA + "- FAWE Pos2 : " + (session.getPos2() != null ? format(session.getPos2()) : "none"));
        player.sendMessage(ChatColor.AQUA + "- Points de path : " + session.getPathPoints().size());
        for (OperatorPlacementType type : OperatorPlacementType.values()) {
            player.sendMessage(ChatColor.AQUA + "- Placements " + type.name() + " : "
                    + session.getOperatorPlacements().get(type).size());
        }
    }
}
