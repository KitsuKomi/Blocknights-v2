package com.blocknights.editor;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.editor.EditorManager.EditorMode;
import com.blocknights.gui.MapSettingsGui; // Import pour le menu settings
import com.blocknights.maps.BnMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent; // Import pour le raccourci F
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class WandListener implements Listener {

    private final BlocknightsPlugin plugin;

    public WandListener(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    // --- 1. Gestion du Menu Rapide (Touche F) ---
    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent e) {
        if (e.getMainHandItem() == null || e.getMainHandItem().getType() != Material.BLAZE_ROD) return;
        if (!plugin.getEditorManager().isEditor(e.getPlayer())) return;

        e.setCancelled(true); // Annule l'échange d'item

        BnMap map = plugin.getMapManager().getActiveMap();
        if (map != null) {
            // Ouvre le menu des réglages (Vies, Argent, Nom)
            new MapSettingsGui(plugin, e.getPlayer(), map).open(e.getPlayer());
        } else {
            e.getPlayer().sendMessage(Component.text("Aucune map active !", NamedTextColor.RED));
        }
    }

    // --- 2. Gestion des Clics (Édition) ---
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getItem() == null || e.getItem().getType() != Material.BLAZE_ROD) return;
        if (!plugin.getEditorManager().isEditor(e.getPlayer())) return;
        
        e.setCancelled(true);

        // Changement de mode (Sneak + Clic dans le vide)
        if (e.getPlayer().isSneaking() && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            // Cas spécial : Mode REGION (Remplissage)
            if (plugin.getEditorManager().getMode(e.getPlayer()) == EditorMode.REGION) {
                 plugin.getEditorManager().fillRegion(e.getPlayer());
                 return;
            }
            // Sinon on change de mode
            plugin.getEditorManager().toggleMode(e.getPlayer());
            return;
        }

        BnMap map = plugin.getMapManager().getActiveMap();
        if (map == null) return;

        EditorMode mode = plugin.getEditorManager().getMode(e.getPlayer());

        // === MODE CHEMIN (PATH) ===
        if (mode == EditorMode.PATH) {
            int currentLane = plugin.getEditorManager().getSelectedLane(e.getPlayer());

            if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                // AJOUTER UN POINT
                Location newLoc = e.getPlayer().getLocation();
                
                // Sécurité pente
                List<Location> path = map.getPath(currentLane);
                if (!path.isEmpty()) {
                    Location lastLoc = path.get(path.size() - 1);
                    if (Math.abs(newLoc.getY() - lastLoc.getY()) > 1.5) {
                        e.getPlayer().sendMessage(Component.text("⚠ Trop pentu ! Max 1.5 blocs.", NamedTextColor.RED));
                        return;
                    }
                }

                plugin.getMapManager().addPathPoint(currentLane, newLoc);
                
                // <--- AJOUT ICI : On déplace les hologrammes (SPAWN/NEXUS)
                plugin.getEditorManager().refreshHolograms(); 
                
                e.getPlayer().sendMessage(Component.text("Point ajouté (Ligne " + currentLane + ")", NamedTextColor.GREEN));
            
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                // RETIRER LE DERNIER POINT
                plugin.getMapManager().removeLastPoint(currentLane);
                
                // <--- AJOUT ICI : On met à jour les hologrammes
                plugin.getEditorManager().refreshHolograms();
                
                e.getPlayer().sendMessage(Component.text("Dernier point retiré.", NamedTextColor.YELLOW));
            }
        }
        
        // === MODE TUILES (SPOTS) ===
        else if (mode == EditorMode.SPOTS) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                Location clicked = e.getClickedBlock().getLocation();
                Location spot = clicked.add(0, 1, 0);

                if (map.isBuildable(spot)) {
                    plugin.getLang().send(e.getPlayer(), "spot-already-valid");
                } else {
                    map.addSpot(spot);
                    plugin.getLang().send(e.getPlayer(), "spot-added");
                    spot.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, spot.clone().add(0.5, 0.5, 0.5), 5);
                    plugin.getMapManager().saveActiveMap();
                }
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location clicked = e.getClickedBlock().getLocation().add(0, 1, 0);
                if (map.isBuildable(clicked)) {
                    map.removeSpot(clicked);
                    plugin.getLang().send(e.getPlayer(), "spot-removed");
                    plugin.getMapManager().saveActiveMap();
                }
            }
        }

        // === MODE REGION (SELECTION) ===
        else if (mode == EditorMode.REGION) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                plugin.getEditorManager().setPos1(e.getPlayer(), e.getClickedBlock().getLocation());
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                plugin.getEditorManager().setPos2(e.getPlayer(), e.getClickedBlock().getLocation());
            }
        }
    }
}