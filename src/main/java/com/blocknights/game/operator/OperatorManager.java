package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.game.GamePlayer;
import com.blocknights.maps.BnMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperatorManager {

    private final BlocknightsPlugin plugin;
    private final Map<String, OperatorDefinition> catalog = new HashMap<>();
    private final List<GameOperator> activeOperators = new ArrayList<>();

    public OperatorManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        initCatalog();
        startCombatLoop();
    }

    private void initCatalog() {
        // Tu peux ajouter d'autres opérateurs ici
        catalog.put("sniper", new OperatorDefinition("sniper", "Sniper", EntityType.SKELETON, 100.0, 7.0, 5.0, 20));
        catalog.put("caster", new OperatorDefinition("caster", "Caster", EntityType.WITCH, 250.0, 5.0, 12.0, 40));
    }

    public boolean placeOperator(Player p, String opId, Location loc) {
        if (!plugin.getSessionManager().isRunning()) {
            p.sendMessage(Component.text("Partie non lancée !", NamedTextColor.RED));
            return false;
        }

        OperatorDefinition def = catalog.get(opId);
        if (def == null) return false;

        GamePlayer gp = plugin.getSessionManager().getGamePlayer(p);
        
        // Check Argent
        if (gp.getMoney() < def.getCost()) {
            // Utilise ton système de Langue ici si tu veux
            p.sendMessage(Component.text("Pas assez d'argent !", NamedTextColor.RED));
            return false;
        }

        BnMap map = plugin.getMapManager().getActiveMap();
        
        // Check Zone Valide
        if (!map.isBuildable(loc)) {
            p.sendMessage(Component.text("Zone invalide !", NamedTextColor.RED));
            p.playSound(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            return false;
        }

        // Check Occupation (Nécessite GameOperator.getLocation())
        for (GameOperator op : activeOperators) {
            if (op.getLocation().getBlock().equals(loc.getBlock())) {
                p.sendMessage(Component.text("Déjà occupé !", NamedTextColor.RED));
                return false;
            }
        }

        // Déploiement
        gp.removeMoney(def.getCost());

        Location spawnLoc = loc.getBlock().getLocation().add(0.5, 0, 0.5);
        Location mapSpawn = plugin.getMapManager().getSpawnPoint();
        if (mapSpawn != null) {
            spawnLoc.setDirection(mapSpawn.toVector().subtract(spawnLoc.toVector()));
        }

        LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(spawnLoc, def.getEntityType());
        
        GameOperator op = new GameOperator(def, entity);
        activeOperators.add(op);
        
        p.sendMessage(Component.text("Opérateur déployé !", NamedTextColor.GREEN));
        return true;
    }

    private void startCombatLoop() {
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