package com.blocknights.waves;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.data.WaveDefinition;
import com.blocknights.data.WaveGroup;
import com.blocknights.maps.BnMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class WaveManager {

    private final BlocknightsPlugin plugin;
    
    private final List<LivingEntity> activeEnemies = new ArrayList<>();
    private final Map<UUID, Integer> enemyPathIndex = new HashMap<>();
    private final Map<UUID, Integer> enemyLaneIndex = new HashMap<>();

    private int currentWaveIndex = 0;

    public WaveManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        startMoveLoop();
    }

    public void startNextWave() {
        BnMap map = plugin.getMapManager().getActiveMap();
        if (map == null) return;

        List<WaveDefinition> waves = map.getWaves();
        
        // Condition de Victoire
        if (currentWaveIndex >= waves.size()) {
            plugin.getSessionManager().victory(); // L'erreur disparaîtra grâce à l'étape 3
            return;
        }

        WaveDefinition wave = waves.get(currentWaveIndex);
        
        // CORRECTION I18N : Plus de texte en dur
        plugin.getLang().broadcast("game-wave-start", "{id}", String.valueOf(wave.getId()));
        
        startWaveLogic(wave);
        currentWaveIndex++;
    }

    private void startWaveLogic(WaveDefinition wave) {
        long currentDelayTicks = 0;

        for (WaveGroup group : wave.getGroups()) {
            long intervalTicks = (long) (group.getInterval() * 20L);

            for (int i = 0; i < group.getCount(); i++) {
                long spawnTime = currentDelayTicks + (i * intervalTicks);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!plugin.getSessionManager().isRunning()) {
                            this.cancel();
                            return;
                        }
                        spawnEnemy(group);
                    }
                }.runTaskLater(plugin, spawnTime);
            }
            currentDelayTicks += (long) (group.getCount() * intervalTicks);
            currentDelayTicks += 60L; 
        }
    }

    private void spawnEnemy(WaveGroup group) {
        BnMap map = plugin.getMapManager().getActiveMap();
        if (map == null) return;
        
        List<Location> path = map.getPath(group.getLaneIndex());
        if (path.isEmpty()) return;

        Location spawnLoc = path.get(0);
        LivingEntity enemy = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, group.getMobType());
        
        var maxHp = enemy.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHp != null) maxHp.setBaseValue(group.getHealth());
        enemy.setHealth(group.getHealth());

        var speed = enemy.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speed != null) speed.setBaseValue(group.getSpeed());

        activeEnemies.add(enemy);
        enemyPathIndex.put(enemy.getUniqueId(), 0);
        enemyLaneIndex.put(enemy.getUniqueId(), group.getLaneIndex());
    }

    private void startMoveLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getSessionManager().isRunning()) return;
                moveEnemies();
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void moveEnemies() {
        Iterator<LivingEntity> it = activeEnemies.iterator();
        BnMap map = plugin.getMapManager().getActiveMap();
        if (map == null) return;
        
        // CORRECTION : On s'assure que getOperatorManager existe
        var operators = plugin.getOperatorManager().getActiveOperators();

        while (it.hasNext()) {
            LivingEntity enemy = it.next();
            
            if (enemy.isDead() || !enemy.isValid()) {
                enemyPathIndex.remove(enemy.getUniqueId());
                enemyLaneIndex.remove(enemy.getUniqueId());
                it.remove();
                continue;
            }

            // -- BLOCAGE --
            boolean isBlocked = false;
            for (var op : operators) {
                if (op.getLocation().distanceSquared(enemy.getLocation()) < 1.0) {
                     if (op.getBlockedEnemies().contains(enemy) || op.canBlock()) {
                         if (!op.getBlockedEnemies().contains(enemy)) op.addBlockedEnemy(enemy);
                         isBlocked = true;
                         break;
                     }
                }
            }
            if (isBlocked) {
                enemy.setVelocity(new Vector(0,0,0));
                continue;
            }

            // -- MOUVEMENT --
            int lane = enemyLaneIndex.getOrDefault(enemy.getUniqueId(), 0);
            List<Location> path = map.getPath(lane);
            int targetIndex = enemyPathIndex.getOrDefault(enemy.getUniqueId(), 0);

            if (targetIndex >= path.size()) {
                plugin.getSessionManager().damageNexus(1);
                enemy.remove();
                enemyPathIndex.remove(enemy.getUniqueId());
                enemyLaneIndex.remove(enemy.getUniqueId());
                it.remove();
                continue;
            }

            Location target = path.get(targetIndex);
            Location current = enemy.getLocation();
            double speed = enemy.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();

            if (current.distance(target) < speed + 0.5) {
                enemyPathIndex.put(enemy.getUniqueId(), targetIndex + 1);
            } else {
                Vector dir = target.toVector().subtract(current.toVector()).normalize().multiply(speed);
                
                // CORRECTION ERREUR "getVelocity undefined" :
                // On récupère la vélocité de l'ENNEMI, pas de la Location (current)
                if (Math.abs(target.getY() - current.getY()) < 0.1) {
                    dir.setY(enemy.getVelocity().getY()); 
                }
                
                enemy.setVelocity(dir);
                Location look = current.clone().setDirection(dir);
                enemy.setRotation(look.getYaw(), look.getPitch());
            }
        }
    }
    // --- MÉTHODES MANQUANTES POUR LE SCOREBOARD & SESSION ---
    
    public void clearAll() { // Renommé de cleanup -> clearAll
        for (LivingEntity e : activeEnemies) e.remove();
        activeEnemies.clear();
        enemyPathIndex.clear();
        enemyLaneIndex.clear();
        currentWaveIndex = 0;
    }
    
    public int getCurrentWave() { return currentWaveIndex; }
    
    public int getTotalWaves() {
        BnMap map = plugin.getMapManager().getActiveMap();
        return (map != null) ? map.getWaves().size() : 0;
    }
    
    public int getEnemiesCount() { return activeEnemies.size(); }
    
    public List<LivingEntity> getEnemies() { return activeEnemies; }
}