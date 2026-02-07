package com.blocknights.editor;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.gui.MapSettingsGui;
import com.blocknights.maps.BnMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

public class EditorManager {

    public enum EditorMode { PATH, SPOTS, REGION }

    private final BlocknightsPlugin plugin;
    
    // Joueurs en mode éditeur -> Ligne sélectionnée
    private final Map<UUID, Integer> playerLanes = new HashMap<>();
    // Joueurs -> Mode actuel (Chemin ou Tuiles)
    private final Map<UUID, EditorMode> editorModes = new HashMap<>();
    private final Map<UUID, Location> pos1 = new HashMap<>();
    private final Map<UUID, Location> pos2 = new HashMap<>();
    private final Set<UUID> renamers = new HashSet<>();
    private final List<TextDisplay> activeHolograms = new ArrayList<>();

    public EditorManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        startVisualizer();
    }

    // --- Gestion Activation ---
    public void toggleEditor(Player player) {
        if (playerLanes.containsKey(player.getUniqueId())) {
            // DÉSACTIVATION
            playerLanes.remove(player.getUniqueId());
            editorModes.remove(player.getUniqueId());
            player.getInventory().remove(Material.BLAZE_ROD);
            player.sendMessage(Component.text("Mode Éditeur désactivé.", NamedTextColor.YELLOW));
            
            // Nettoyage des hologrammes
            clearHolograms();
        } else {
            // ACTIVATION
            playerLanes.put(player.getUniqueId(), 0);
            editorModes.put(player.getUniqueId(), EditorMode.PATH);
            giveWand(player);
            player.sendMessage(Component.text("Mode Éditeur activé !", NamedTextColor.GREEN));
            
            // Création des hologrammes initiaux
            refreshHolograms();
        }
    }
    public void refreshHolograms() {
        // 1. On nettoie les anciens
        clearHolograms();
        
        // 2. On vérifie qu'on a une map
        BnMap map = plugin.getMapManager().getActiveMap();
        if (map == null) return;

        // 3. On crée les nouveaux
        List<List<Location>> lanes = map.getLanes();
        for (int i = 0; i < lanes.size(); i++) {
            List<Location> path = lanes.get(i);
            if (path.isEmpty()) continue;

            // SPAWN (Début)
            spawnHolo(path.get(0), "§c👹 SPAWN\n§7(Ligne " + i + ")", NamedTextColor.RED);

            // NEXUS (Fin)
            // On le met un peu plus haut pour qu'il soit bien visible
            spawnHolo(path.get(path.size() - 1), "§b💎 NEXUS\n§7(Vies: " + map.getInitialLives() + ")", NamedTextColor.AQUA);
        }
    }

    private void spawnHolo(Location loc, String text, NamedTextColor color) {
        // On spawn le texte 1 bloc au-dessus
        Location holoLoc = loc.clone().add(0, 1.5, 0);
        
        TextDisplay display = loc.getWorld().spawn(holoLoc, TextDisplay.class, entity -> {
            entity.text(Component.text(text));
            entity.setBillboard(Display.Billboard.CENTER); // Regarde toujours le joueur
            entity.setSeeThrough(true); // Visible à travers les murs
            entity.setBackgroundColor(org.bukkit.Color.fromARGB(100, 0, 0, 0)); // Fond semi-transparent
            entity.setShadowed(true);
            
            // On grossit un peu le texte (Scale 1.5)
            entity.setTransformation(new Transformation(
                new Vector3f(0, 0, 0), 
                new AxisAngle4f(0, 0, 0, 1), 
                new Vector3f(1.5f, 1.5f, 1.5f), // Scale
                new AxisAngle4f(0, 0, 0, 1)
            ));
            
            // Important : On le marque comme persistant temporaire pour pas qu'il soit sauvegardé par Bukkit
            entity.setPersistent(false); 
        });
        
        activeHolograms.add(display);
    }

    private void clearHolograms() {
        for (TextDisplay display : activeHolograms) {
            if (display.isValid()) display.remove();
        }
        activeHolograms.clear();
    }
    
    // N'oublie pas de nettoyer si le plugin s'éteint
    public void shutdown() {
        clearHolograms();
    }
    
    public void toggleMode(Player p) {
            EditorMode current = editorModes.getOrDefault(p.getUniqueId(), EditorMode.PATH);
            EditorMode next;
            
            // Cycle : PATH -> SPOTS -> REGION -> PATH
            switch (current) {
                case PATH: next = EditorMode.SPOTS; break;
                case SPOTS: next = EditorMode.REGION; break;
                default: next = EditorMode.PATH; break;
            }
            
            editorModes.put(p.getUniqueId(), next);
            
            // Messages i18n
            if (next == EditorMode.REGION) {
                p.sendMessage("§eMode: RÉGION (WorldEdit Style)");
                p.sendMessage("§7Clic G: Pos1 | Clic D: Pos2 | Shift+Clic: REMPLIR");
            } else if (next == EditorMode.SPOTS) {
                plugin.getLang().send(p, "editor-mode-spots");
            } else {
                plugin.getLang().send(p, "editor-mode-path");
            }
        }

        // Méthode de sélection
        public void setPos1(Player p, Location loc) {
            pos1.put(p.getUniqueId(), loc);
            p.sendMessage("§dPosition 1 définie.");
        }

        public void setPos2(Player p, Location loc) {
            pos2.put(p.getUniqueId(), loc);
            p.sendMessage("§dPosition 2 définie.");
        }

        // LA méthode magique qui remplit tout
        public void fillRegion(Player p) {
            Location p1 = pos1.get(p.getUniqueId());
            Location p2 = pos2.get(p.getUniqueId());

            if (p1 == null || p2 == null) {
                p.sendMessage("§cIl faut définir Pos1 et Pos2 d'abord !");
                return;
            }

            if (!p1.getWorld().equals(p2.getWorld())) return;

            BnMap map = plugin.getMapManager().getActiveMap();
            int count = 0;

            // Boucle Min/Max pour parcourir le cube
            int minX = Math.min(p1.getBlockX(), p2.getBlockX());
            int maxX = Math.max(p1.getBlockX(), p2.getBlockX());
            int minY = Math.min(p1.getBlockY(), p2.getBlockY());
            int maxY = Math.max(p1.getBlockY(), p2.getBlockY());
            int minZ = Math.min(p1.getBlockZ(), p2.getBlockZ());
            int maxZ = Math.max(p1.getBlockZ(), p2.getBlockZ());

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Location loc = new Location(p1.getWorld(), x, y, z);
                        // On ajoute le spot AU DESSUS du bloc sélectionné (comme le mode simple)
                        Location spot = loc.add(0, 1, 0);
                        
                        if (!map.isBuildable(spot) && !loc.getBlock().getType().isAir()) {
                            map.addSpot(spot);
                            count++;
                        }
                    }
                }
            }

            p.sendMessage("§a" + count + " tuiles ajoutées !");
            plugin.getMapManager().saveActiveMap();
            
            // Reset
            pos1.remove(p.getUniqueId());
            pos2.remove(p.getUniqueId());
        }

    public boolean isEditor(Player player) {
        return playerLanes.containsKey(player.getUniqueId());
    }

    // --- Gestion Modes & Lanes ---
    public void selectLane(Player p, int laneIndex) {
        if (!isEditor(p)) return;
        if (laneIndex < 0) laneIndex = 0;
        playerLanes.put(p.getUniqueId(), laneIndex);
        p.sendMessage(Component.text("Édition de la Ligne " + laneIndex, NamedTextColor.AQUA));
    }

    public int getSelectedLane(Player p) {
        return playerLanes.getOrDefault(p.getUniqueId(), 0);
    }

    public EditorMode getMode(Player p) {
        return editorModes.getOrDefault(p.getUniqueId(), EditorMode.PATH);
    }

    // --- Outils ---
    private void giveWand(Player player) {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wand.getItemMeta();
        meta.displayName(Component.text("Baguette d'Éditeur", NamedTextColor.GOLD));
        meta.lore(List.of(
            Component.text("Clic G: Ajouter | Clic D: Retirer", NamedTextColor.YELLOW),
            Component.text("Sneak + Clic D: Changer Mode", NamedTextColor.GRAY)
        ));
        wand.setItemMeta(meta);
        player.getInventory().addItem(wand);
    }

    // --- Visualiseur ---
    private void startVisualizer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (playerLanes.isEmpty()) return;
                
                var map = plugin.getMapManager().getActiveMap();
                if (map == null) return;

                // 1. Visualiser les Chemins (Lanes)
                List<List<Location>> lanes = map.getLanes();
                for (int i = 0; i < lanes.size(); i++) {
                    List<Location> path = lanes.get(i);
                    if (path.isEmpty()) continue;

                    Particle lineParticle = (i == 0) ? Particle.HAPPY_VILLAGER : Particle.WAX_ON;

                    for (int j = 0; j < path.size() - 1; j++) {
                        drawLine(path.get(j), path.get(j + 1), lineParticle);
                    }
                    
                    // Marqueurs
                    path.get(0).getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, path.get(0), 5, 0, 0, 0, 0);
                    path.get(path.size() - 1).getWorld().spawnParticle(Particle.LAVA, path.get(path.size() - 1), 5, 0, 0, 0, 0);
                }

                // 2. Visualiser les Tuiles (Spots)
                for (Location spot : map.getSpots()) {
                    // Particule au-dessus du bloc
                    spot.getWorld().spawnParticle(Particle.COMPOSTER, spot.clone().add(0, 1.2, 0), 1);
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    public void startRenaming(Player p) {
        renamers.add(p.getUniqueId());
        plugin.getLang().send(p, "editor-rename-start");
    }

    public boolean isRenaming(Player p) {
        return renamers.contains(p.getUniqueId());
    }

    public void finishRenaming(Player p, String newName) {
        renamers.remove(p.getUniqueId());
        
        if (newName.equalsIgnoreCase("cancel")) {
            plugin.getLang().send(p, "editor-rename-cancel");
            return;
        }

        BnMap map = plugin.getMapManager().getActiveMap();
        if (map != null) {
            // Support des codes couleurs avec '&'
            String colored = newName.replace("&", "§");
            map.setDisplayName(colored);
            plugin.getMapManager().saveActiveMap();
            plugin.getLang().send(p, "editor-rename-success", "{name}", colored);
            
            // On rouvre le menu
            new MapSettingsGui(plugin, p, map).open(p);
        }
    }

    private void drawLine(Location p1, Location p2, Particle particleType) {
        double dist = p1.distance(p2);
        double step = 0.5;
        Vector direction = p2.toVector().subtract(p1.toVector()).normalize();
        
        for (double d = 0; d < dist; d += step) {
            Location loc = p1.clone().add(direction.clone().multiply(d));
            loc.getWorld().spawnParticle(particleType, loc, 1, 0, 0, 0, 0);
        }
    }
}