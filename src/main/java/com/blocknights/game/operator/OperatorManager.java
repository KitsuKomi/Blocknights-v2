package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.game.GamePlayer;
import com.blocknights.maps.BnMap;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import net.citizensnpcs.api.CitizensAPI; // Import Citizens
import net.citizensnpcs.api.npc.NPC; // Import Citizens
import net.citizensnpcs.trait.SkinTrait; // Import Citizens

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperatorManager {

    private final BlocknightsPlugin plugin;
    private final OperatorFileIO io;
    
    private final Map<String, OperatorDefinition> catalog = new HashMap<>();
    private final List<GameOperator> activeOperators = new ArrayList<>();

    public OperatorManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        this.io = new OperatorFileIO(plugin);
        
        reload();
        startGameLoop();
    }

    public void reload() {
        catalog.clear();
        catalog.putAll(io.loadAll());
        plugin.getLogger().info(catalog.size() + " opérateurs chargés.");
    }

    public Map<String, OperatorDefinition> getCatalog() { return catalog; }
    public List<GameOperator> getActiveOperators() { return activeOperators; }

    // --- NOUVELLE MÉTHODE (Celle qui te manquait) ---
    /**
     * Calcule le montant remboursé lors d'une retraite.
     * Centralisé ici pour être utilisé par le GUI et la logique de jeu.
     */
    public int getRefundAmount(OperatorDefinition def) {
        // Règle Arknights : 50% du coût initial
        return (int) (def.getCost() * 0.5);
    }

    public boolean placeOperator(Player p, String opId, Location loc) {
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
        
        if (gp.getMoney() < def.getCost()) {
            double missing = def.getCost() - gp.getMoney();
            plugin.getLang().send(p, "op-no-money", "{amount}", String.valueOf((int)missing));
            return false;
        }

        BnMap map = plugin.getMapManager().getActiveMap();
        
        if (!map.isBuildable(loc)) {
            plugin.getLang().send(p, "op-invalid-spot");
            p.playSound(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            return false;
        }

        // Vérification Collision (Citizens & Vanilla)
        for (GameOperator op : activeOperators) {
            if (op.getLocation().getBlock().equals(loc.getBlock())) {
                plugin.getLang().send(p, "op-spot-taken");
                return false;
            }
        }

        // --- DÉPLOIEMENT ---
        gp.removeMoney(def.getCost());

        // Création NPC Citizens
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(org.bukkit.entity.EntityType.PLAYER, def.getName());
        
        // Skin
        if (def.getSkinTexture() != null) {
            SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
            skinTrait.setTexture(def.getSkinTexture(), def.getSkinSignature());
        } else {
            npc.getOrAddTrait(SkinTrait.class).setSkinName(def.getSkinName());
        }

        // Position & Orientation
        Location spawnLoc = loc.getBlock().getLocation().add(0.5, 0, 0.5);
        Location mapSpawn = plugin.getMapManager().getSpawnPoint();
        if (mapSpawn != null) {
            spawnLoc.setDirection(mapSpawn.toVector().subtract(spawnLoc.toVector()));
        }
        
        npc.spawn(spawnLoc);
        npc.setProtected(true);

        GameOperator op = new GameOperator(def, npc);
        activeOperators.add(op);
        
        plugin.getLang().send(p, "op-placed", "{name}", def.getName());
        p.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        
        return true;
    }

    /**
     * Trouve l'opérateur correspondant à une entité cliquée
     */
    public GameOperator getOperatorByEntity(org.bukkit.entity.Entity entity) {
        for (GameOperator op : activeOperators) {
            if (op.getNPC().isSpawned() && op.getNPC().getEntity().getUniqueId().equals(entity.getUniqueId())) {
                return op;
            }
        }
        return null;
    }

    /**
     * Gère la retraite (remboursement + suppression)
     */
    public void retreatOperator(Player p, GameOperator op) {
        if (!activeOperators.contains(op)) return;

        // CORRECTION ICI : On utilise la nouvelle méthode getRefundAmount
        int refund = getRefundAmount(op.getDefinition());
        
        plugin.getSessionManager().getGamePlayer(p).addMoney(refund);

        plugin.getLang().send(p, "op-retreat", 
            "{name}", op.getDefinition().getName(), 
            "{amount}", String.valueOf(refund)
        );
        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 0.5f);

        op.remove(); 
        activeOperators.remove(op);
    }

    private void startGameLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getSessionManager().isRunning()) return;
                
                List<GameOperator> safeList = new ArrayList<>(activeOperators);
                for (GameOperator op : safeList) {
                    op.tick(plugin);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    public void clearAll() {
        for (GameOperator op : activeOperators) {
            op.remove();
        }
        activeOperators.clear();
    }
}