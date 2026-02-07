package com.blocknights.editor;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.editor.EditorManager.EditorMode;
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
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class WandListener implements Listener {

    private final BlocknightsPlugin plugin;

    public WandListener(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        // Sécurité de base
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getItem() == null || e.getItem().getType() != Material.BLAZE_ROD) return;
        if (!plugin.getEditorManager().isEditor(e.getPlayer())) return;
        
        e.setCancelled(true); // Empêcher de taper/casser

        // Changement de mode (Sneak + Clic Droit)
        if (e.getPlayer().isSneaking() && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            plugin.getEditorManager().toggleMode(e.getPlayer());
            return;
        }

        BnMap map = plugin.getMapManager().getActiveMap();
        if (map == null) return;

        EditorMode mode = plugin.getEditorManager().getMode(e.getPlayer());

        // --- MODE 1 : CHEMIN (PATH) ---
        if (mode == EditorMode.PATH) {
            int currentLane = plugin.getEditorManager().getSelectedLane(e.getPlayer());

            if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                Location newLoc = e.getPlayer().getLocation();
                
                // Sécurité Verticalité
                List<Location> path = map.getPath(currentLane);
                if (!path.isEmpty()) {
                    Location lastLoc = path.get(path.size() - 1);
                    if (Math.abs(newLoc.getY() - lastLoc.getY()) > 1.5) {
                        e.getPlayer().sendMessage(Component.text("⚠ Trop pentu ! Max 1.5 blocs.", NamedTextColor.RED));
                        return;
                    }
                }

                plugin.getMapManager().addPathPoint(currentLane, newLoc);
                e.getPlayer().sendMessage(Component.text("Point ajouté (Ligne " + currentLane + ")", NamedTextColor.GREEN));
            
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                plugin.getMapManager().removeLastPoint(currentLane);
                e.getPlayer().sendMessage(Component.text("Dernier point retiré.", NamedTextColor.YELLOW));
            }
        }
        
        // --- MODE 2 : TUILES (SPOTS) ---
        else if (mode == EditorMode.SPOTS) {
            
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                // AJOUTER
                Location clicked = e.getClickedBlock().getLocation();
                Location spot = clicked.add(0, 1, 0); // On valide l'air au-dessus du bloc

                if (map.isBuildable(spot)) {
                    plugin.getLang().send(e.getPlayer(), "spot-already-valid");
                } else {
                    map.addSpot(spot);
                    plugin.getLang().send(e.getPlayer(), "spot-added");
                    spot.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, spot.clone().add(0.5, 0.5, 0.5), 5);
                    plugin.getMapManager().saveActiveMap();
                }

            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                // RETIRER
                // On vérifie le bloc cliqué ET celui au-dessus pour être souple
                Location clicked = e.getClickedBlock().getLocation();
                Location above = clicked.clone().add(0, 1, 0);
                
                if (map.isBuildable(above)) {
                    map.removeSpot(above);
                    plugin.getLang().send(e.getPlayer(), "spot-removed");
                    plugin.getMapManager().saveActiveMap();
                } else if (map.isBuildable(clicked)) {
                    map.removeSpot(clicked);
                    plugin.getLang().send(e.getPlayer(), "spot-removed");
                    plugin.getMapManager().saveActiveMap();
                }
            }
        }
    }
}