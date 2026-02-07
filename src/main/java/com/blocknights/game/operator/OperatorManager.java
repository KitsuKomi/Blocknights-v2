package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.game.GamePlayer;
import com.blocknights.maps.BnMap;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;

public class OperatorManager {

    private final BlocknightsPlugin plugin;
    private final OperatorFileIO io;
    
    // Catalogue & Opérateurs actifs
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

    /**
     * Tente de placer un opérateur (100% i18n)
     */
    public boolean placeOperator(Player p, String opId, Location loc) {
        // 1. Check : Jeu lancé ?
        if (!plugin.getSessionManager().isRunning()) {
            plugin.getLang().send(p, "game-not-running");
            return false;
        }

        // 2. Check : Opérateur existe ?
        OperatorDefinition def = catalog.get(opId);
        if (def == null) {
            plugin.getLang().send(p, "op-unknown", "{id}", opId);
            return false;
        }

        GamePlayer gp = plugin.getSessionManager().getGamePlayer(p);
        
        // 3. Check : Argent (LMD)
        if (gp.getMoney() < def.getCost()) {
            // Calcul du manque pour l'affichage
            double missing = def.getCost() - gp.getMoney();
            plugin.getLang().send(p, "op-no-money", "{amount}", String.valueOf((int)missing));
            return false;
        }

        BnMap map = plugin.getMapManager().getActiveMap();
        
        // 4. Check : Zone Valide (Spots verts)
        if (!map.isBuildable(loc)) {
            plugin.getLang().send(p, "op-invalid-spot");
            p.playSound(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            return false;
        }

        // 5. Check : Collision (Déjà occupé ?)
        for (GameOperator op : activeOperators) {
            if (op.getLocation().getBlock().equals(loc.getBlock())) {
                plugin.getLang().send(p, "op-spot-taken");
                return false;
            }
        }

        // --- DÉPLOIEMENT ---
        
        // Paiement
        gp.removeMoney(def.getCost());

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(org.bukkit.entity.EntityType.PLAYER, def.getName());

        // 2. Application du Skin
        if (def.getSkinTexture() != null) {
            // Skin via Texture/Signature (Stable)
            SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
            skinTrait.setTexture(def.getSkinTexture(), def.getSkinSignature());
        } else {
            // Skin via Nom (Simple mais instable)
            npc.getOrAddTrait(SkinTrait.class).setSkinName(def.getSkinName());
        }

        // 3. Spawn Physique
        Location spawnLoc = loc.getBlock().getLocation().add(0.5, 0, 0.5);
        // Orientation vers le spawn
        Location mapSpawn = plugin.getMapManager().getSpawnPoint();
        if (mapSpawn != null) {
            spawnLoc.setDirection(mapSpawn.toVector().subtract(spawnLoc.toVector()));
        }
        
        npc.spawn(spawnLoc);
        
        // 4. Protection (Invulnérable, ne bouge pas)
        npc.setProtected(true); // Empêche de prendre des dégâts vanilla
        // On pourrait retirer le pathfinding ici si besoin, mais setAI(false) suffit souvent via Traits

        // 5. Création Objet Logique
        GameOperator op = new GameOperator(def, npc);
        activeOperators.add(op);
        
        plugin.getLang().send(p, "op-placed", "{name}", def.getName());
        return true;
    }

    private void startGameLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getSessionManager().isRunning()) return;
                
                // Copie défensive pour éviter les ConcurrentModificationException
                List<GameOperator> safeList = new ArrayList<>(activeOperators);
                for (GameOperator op : safeList) {
                    op.tick(plugin);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    public void clearAll() {
        for (GameOperator op : activeOperators) {
            op.remove(); // Ceci appelle npc.destroy() qui supprime le NPC du registre Citizens
        }
        activeOperators.clear();
    }
}