package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.game.GamePlayer;
import com.blocknights.maps.BnMap;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait; // Import correct pour le skin
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class OperatorManager {

    private final BlocknightsPlugin plugin;
    private final OperatorLoader io;
    
    private final Map<String, OperatorDefinition> catalog;
    private final List<GameOperator> activeOperators;
    private final Map<UUID, OperatorDefinition> pendingPlacements = new HashMap<>();

    public OperatorManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        
        // 1. Initialisation des listes
        this.catalog = new HashMap<>();
        this.activeOperators = new ArrayList<>();
        
        // 2. Initialisation du loader
        this.io = new OperatorLoader(plugin);
        
        // 3. Chargement et Démarrage
        reload();
        startGameLoop();
    }

    public void reload() {
        // On vide et on recharge
        catalog.clear();
        io.loadAll(); // Cette méthode est void, elle remplit le catalogue en interne
        plugin.getLogger().info(catalog.size() + " opérateurs chargés.");
    }

    // --- GESTION DE LA SÉLECTION (GUI) ---

    public void selectOperatorForPlacement(Player p, OperatorDefinition def) {
        pendingPlacements.put(p.getUniqueId(), def);
        plugin.getLang().send(p, "deploy-selected", "{name}", def.getName());
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
    }

    public OperatorDefinition getPendingOperator(Player p) {
        return pendingPlacements.get(p.getUniqueId());
    }

    public void clearPending(Player p) {
        pendingPlacements.remove(p.getUniqueId());
    }

    // --- GETTERS & UTILITAIRES ---

    public Map<String, OperatorDefinition> getCatalog() { return catalog; }
    
    public List<GameOperator> getActiveOperators() { return activeOperators; }

    public void registerOperator(OperatorDefinition def) {
        this.catalog.put(def.getId(), def);
    }

    public int getRefundAmount(OperatorDefinition def) {
        return (int) (def.getCost() * 0.5); // Remboursement 50%
    }

    /**
     * Trouve l'opérateur correspondant à une entité Bukkit (pour les dégâts)
     */
    public GameOperator getOperatorByEntity(org.bukkit.entity.Entity entity) {
        for (GameOperator op : activeOperators) {
            // Vérification de sécurité pour éviter les NullPointerException
            if (op.getNPC().isSpawned() && op.getNPC().getEntity() != null) {
                if (op.getNPC().getEntity().getUniqueId().equals(entity.getUniqueId())) {
                    return op;
                }
            }
        }
        return null;
    }

    // --- LOGIQUE DE PLACEMENT (Le Gros Morceau) ---

    public boolean placeOperator(Player p, String opId, Location loc) {
        // 1. Jeu en cours ?
        if (!plugin.getSessionManager().isRunning()) {
            plugin.getLang().send(p, "game-not-running");
            return false;
        }

        OperatorDefinition def = catalog.get(opId);
        if (def == null) {
            plugin.getLang().send(p, "op-unknown", "{id}", opId);
            return false;
        }

        GamePlayer gp = plugin.getSessionManager().getGamePlayer(p);
        
        // 2. Argent ?
        if (gp.getMoney() < def.getCost()) {
            double missing = def.getCost() - gp.getMoney();
            plugin.getLang().send(p, "op-no-money", "{amount}", String.valueOf((int)missing));
            return false;
        }

        // 3. Terrain ?
        BnMap map = plugin.getMapManager().getActiveMap();
        if (!map.isBuildable(loc)) {
            plugin.getLang().send(p, "op-invalid-spot");
            p.playSound(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            return false;
        }

        // 4. Collision ?
        for (GameOperator op : activeOperators) {
            if (op.getLocation().getBlock().equals(loc.getBlock())) {
                plugin.getLang().send(p, "op-spot-taken");
                return false;
            }
        }

        // --- EXÉCUTION DU DÉPLOIEMENT ---
        
        gp.removeMoney(def.getCost());

        // Création NPC via Citizens
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(def.getEntityType(), def.getName());
        
        // Gestion du Skin (Seulement pour les Humains)
        if (def.getEntityType() == EntityType.PLAYER) {
            if (def.getSkinName() != null && !def.getSkinName().isEmpty()) {
                // Ici, l'import SkinTrait fonctionne car il est en haut du fichier
                SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
                skinTrait.setSkinName(def.getSkinName());
            }
        }

        // Positionnement
        Location spawnLoc = loc.getBlock().getLocation().add(0.5, 0, 0.5);
        Location enemySpawn = plugin.getMapManager().getSpawnPoint();
        
        // Orientation vers l'ennemi
        if (enemySpawn != null) {
            spawnLoc.setDirection(enemySpawn.toVector().subtract(spawnLoc.toVector()));
        } else {
            spawnLoc.setYaw(p.getLocation().getYaw());
        }
        
        npc.spawn(spawnLoc);
        npc.setProtected(true);

        // Enregistrement logique
        GameOperator op = new GameOperator(def, npc);
        activeOperators.add(op);
        
        plugin.getLang().send(p, "op-placed", "{name}", def.getName());
        p.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        
        return true;
    }

    // --- GESTION DE LA RETRAITE ---

    public void retreatOperator(Player p, GameOperator op) {
        if (!activeOperators.contains(op)) return;

        int refund = getRefundAmount(op.getDefinition());
        plugin.getSessionManager().getGamePlayer(p).addMoney(refund);

        plugin.getLang().send(p, "op-retreat", 
            "{name}", op.getDefinition().getName(), 
            "{amount}", String.valueOf(refund)
        );
        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 0.5f);

        op.remove(); // Assure-toi que cette méthode existe dans GameOperator
        activeOperators.remove(op);
    }

    // --- BOUCLE DE JEU (TICK) ---

    private void startGameLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getSessionManager().isRunning()) return;
                
                // Copie de sécurité pour éviter les erreurs si un opérateur meurt pendant la boucle
                List<GameOperator> safeList = new ArrayList<>(activeOperators);
                for (GameOperator op : safeList) {
                    op.tick(plugin); // Assure-toi que cette méthode existe dans GameOperator
                }
            }
        }.runTaskTimer(plugin, 0L, 2L); // Toutes les 0.1 secondes
    }

    public void clearAll() {
        for (GameOperator op : activeOperators) {
            op.remove();
        }
        activeOperators.clear();
    }
}