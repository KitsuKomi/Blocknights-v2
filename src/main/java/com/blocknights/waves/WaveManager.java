package com.blocknights.waves;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.data.WaveDefinition;
import com.blocknights.data.WaveGroup;
import com.blocknights.game.operator.GameOperator;
import com.blocknights.maps.BnMap;
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

        double enemyAtk = 10.0 + (currentWaveIndex * 2); // Ex: Vague 1 = 12 dmg, Vague 5 = 20 dmg
        double enemyDef = 2.0 + currentWaveIndex;        // Ex: Vague 1 = 3 def
        
        enemy.setMetadata("bn_atk", new org.bukkit.metadata.FixedMetadataValue(plugin, enemyAtk));
        enemy.setMetadata("bn_def", new org.bukkit.metadata.FixedMetadataValue(plugin, enemyDef));
        
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
        
        // On récupère la liste des opérateurs vivants
        List<GameOperator> operators = plugin.getOperatorManager().getActiveOperators();

        while (it.hasNext()) {
            LivingEntity enemy = it.next();
            
            // Nettoyage si mort
            if (enemy.isDead() || !enemy.isValid()) {
                cleanEnemyData(enemy);
                it.remove();
                continue;
            }

            // --- 1. LOGIQUE DE BLOCAGE (LE CŒUR DU SYSTÈME) ---
            boolean isBlocked = false;
            GameOperator blocker = null;

            // Est-ce que cet ennemi est DÉJÀ bloqué par quelqu'un ?
            for (GameOperator op : operators) {
                if (op.getBlockedEnemies().contains(enemy)) {
                    isBlocked = true;
                    blocker = op;
                    break;
                }
            }

            // Sinon, est-ce qu'il entre dans la zone d'un opérateur ?
            if (!isBlocked) {
                for (GameOperator op : operators) {
                    // Distance de contact (0.8 bloc = très proche)
                    if (op.getLocation().distanceSquared(enemy.getLocation()) < 0.8) {
                        if (op.canBlock()) {
                            op.addBlockedEnemy(enemy);
                            isBlocked = true;
                            blocker = op;
                            break; // Bloqué par le premier trouvé
                        }
                    }
                }
            }

            // SI BLOQUÉ : On arrête le mouvement et on tape l'opérateur
            if (isBlocked && blocker != null) {
                // Vélocité 0 pour qu'il reste sur place
                enemy.setVelocity(new Vector(0, 0, 0));
                
                // On oriente l'ennemi vers l'opérateur (visuel)
                Location lookAt = blocker.getLocation().clone();
                lookAt.setDirection(lookAt.toVector().subtract(enemy.getLocation().toVector()));
                enemy.setRotation(lookAt.getYaw(), lookAt.getPitch());

                // L'ennemi tape l'opérateur (tous les 20 ticks / 1 seconde par ex)
                // Ici on simplifie : petit dégât à chaque tick du loop (attention à l'équilibrage)
                // Idéalement : stocker lastAttackTime sur l'ennemi aussi.
                // Pour l'instant : Dégât brut divisé par la vitesse du loop
                blocker.takeDamage(15.0 / 10.0); // ex: 15 dégâts par seconde
                
                continue; // On passe à l'ennemi suivant, il n'avance pas sur le chemin
            }

            // --- 2. LOGIQUE DE MOUVEMENT (PATHFINDING) ---
            // Si pas bloqué, il avance sur sa ligne
            int lane = enemyLaneIndex.getOrDefault(enemy.getUniqueId(), 0);
            List<Location> path = map.getPath(lane);
            int targetIndex = enemyPathIndex.getOrDefault(enemy.getUniqueId(), 0);

            if (targetIndex >= path.size()) {
                // Arrivé au bout (Nexus)
                plugin.getSessionManager().damageNexus(1);
                enemy.remove();
                cleanEnemyData(enemy);
                it.remove();
                continue;
            }

            Location target = path.get(targetIndex);
            Location current = enemy.getLocation();
            
            // Vitesse définie dans les stats de l'ennemi
            double speed = enemy.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();

            // Si proche du point, on passe au suivant
            if (current.distance(target) < speed + 0.5) {
                enemyPathIndex.put(enemy.getUniqueId(), targetIndex + 1);
            } else {
                // Vecteur de déplacement
                Vector dir = target.toVector().subtract(current.toVector()).normalize().multiply(speed);
                
                // On garde la gravité (Y)
                if (Math.abs(target.getY() - current.getY()) < 0.1) {
                    dir.setY(enemy.getVelocity().getY()); 
                }
                
                enemy.setVelocity(dir);
                
                // Orientation vers où il marche
                Location look = current.clone().setDirection(dir);
                enemy.setRotation(look.getYaw(), look.getPitch());
            }
        }
    }
    
    private void cleanEnemyData(LivingEntity enemy) {
        enemyPathIndex.remove(enemy.getUniqueId());
        enemyLaneIndex.remove(enemy.getUniqueId());
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