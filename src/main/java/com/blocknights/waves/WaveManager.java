package com.blocknights.waves;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.data.WaveDefinition;
import com.blocknights.data.WaveGroup;
import com.blocknights.maps.BnMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.*;

public class WaveManager {

    private final BlocknightsPlugin plugin;
    
    // État du jeu
    private int currentWaveIndex = 0; // 1 = Vague 1
    private boolean waveActive = false;
    
    // Gestion des monstres
    private final List<LivingEntity> activeEnemies = new ArrayList<>();
    // Map pour savoir quel ennemi va vers quel point (Index)
    private final Map<UUID, Integer> enemyPathIndex = new HashMap<>();
    // Map pour savoir sur quelle ligne est l'ennemi (Lane)
    private final Map<UUID, Integer> enemyLaneIndex = new HashMap<>(); 
    
    // Gestion du Spawning précis
    private Queue<WaveGroup> groupsQueue = new LinkedList<>();
    private WaveGroup currentGroup;
    private int mobsSpawnedInGroup = 0;
    private long lastSpawnTime = 0;

    public WaveManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    public void startWave() {
        BnMap map = plugin.getMapManager().getActiveMap();
        if (map == null || map.getWaves().isEmpty()) {
            Bukkit.broadcast(Component.text("Pas de vagues configurées sur cette map !", NamedTextColor.RED));
            return;
        }

        currentWaveIndex++;
        
        // Vérifier si on a fini toutes les vagues
        if (currentWaveIndex > map.getWaves().size()) {
            Bukkit.broadcast(Component.text("--- VICTOIRE ! Mission Accomplie ---", NamedTextColor.GOLD));
            plugin.getSessionManager().stopGame();
            return;
        }
        
        // Récupérer la définition de la vague depuis la Map
        // (Les index de liste commencent à 0, donc vague 1 = index 0)
        WaveDefinition wave = map.getWaves().get(currentWaveIndex - 1);
        
        plugin.getLang().send(Bukkit.getConsoleSender(), "wave-start", "{wave}", String.valueOf(currentWaveIndex));
        Bukkit.broadcast(Component.text("--- VAGUE " + currentWaveIndex + " ---", NamedTextColor.RED));
        
        this.groupsQueue = new LinkedList<>(wave.getGroups());
        this.currentGroup = groupsQueue.poll();
        this.mobsSpawnedInGroup = 0;
        this.waveActive = true;
    }

    public void tick() {
        if (!plugin.getSessionManager().isRunning()) return;

        // 1. Logique de Spawning
        if (waveActive && currentGroup != null) {
            long now = System.currentTimeMillis();
            long intervalMs = currentGroup.getInterval() * 50L; 

            if (now - lastSpawnTime >= intervalMs) {
                spawnEnemy(currentGroup);
                lastSpawnTime = now;
                mobsSpawnedInGroup++;
                
                if (mobsSpawnedInGroup >= currentGroup.getCount()) {
                    if (groupsQueue.isEmpty()) {
                        currentGroup = null; // Vague finie de spawner
                    } else {
                        currentGroup = groupsQueue.poll();
                        mobsSpawnedInGroup = 0;
                    }
                }
            }
        } else if (waveActive && activeEnemies.isEmpty() && currentGroup == null) {
            // Vague terminée (Plus rien à spawn ET plus rien en vie)
            waveActive = false;
            
            // Récupérer le délai avant la prochaine
            BnMap map = plugin.getMapManager().getActiveMap();
            int delay = 5;
            if (map != null && currentWaveIndex <= map.getWaves().size()) {
                delay = map.getWaves().get(currentWaveIndex - 1).getDelayBeforeNext();
            }
            
            Bukkit.broadcast(Component.text("Vague terminée ! Prochaine dans " + delay + "s...", NamedTextColor.GREEN));
            Bukkit.getScheduler().runTaskLater(plugin, this::startWave, delay * 20L);
        }

        // 2. Logique de Mouvement
        moveEnemies();
    }

    private void spawnEnemy(WaveGroup group) {
        int lane = group.getLaneIndex();
        
        var map = plugin.getMapManager().getActiveMap();
        if (map == null) return;
        List<Location> path = map.getPath(lane);
        if (path.isEmpty()) return;

        Location spawnLoc = path.get(0);
        LivingEntity enemy = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, group.getMobType());
        
        // Configuration Stats
        enemy.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(group.getHealth());
        enemy.setHealth(group.getHealth());
        enemy.setAI(false); 
        enemy.setGravity(false);
        enemy.customName(Component.text("PV: " + (int)group.getHealth(), NamedTextColor.RED));
        enemy.setCustomNameVisible(true);

        // Stockage Vitesse dans le modifier de vitesse vanilla (détourné)
        // 0.25 est la vitesse standard d'un zombie
        enemy.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(group.getSpeed());

        activeEnemies.add(enemy);
        enemyPathIndex.put(enemy.getUniqueId(), 1); // Vise le point 1
        enemyLaneIndex.put(enemy.getUniqueId(), lane);
    }

    private void moveEnemies() {
        Iterator<LivingEntity> it = activeEnemies.iterator();
        var map = plugin.getMapManager().getActiveMap();
        
        while (it.hasNext()) {
            LivingEntity enemy = it.next();
            
            if (enemy.isDead() || !enemy.isValid()) {
                // Nettoyage silencieux (mort naturelle ou kill command)
                // Si tué par joueur/tour, c'est onEnemyKilled qui gère
                enemyPathIndex.remove(enemy.getUniqueId());
                enemyLaneIndex.remove(enemy.getUniqueId());
                it.remove();
                continue;
            }
            
            enemy.customName(Component.text("PV: " + (int)enemy.getHealth(), NamedTextColor.RED));

            int lane = enemyLaneIndex.get(enemy.getUniqueId());
            List<Location> path = map.getPath(lane);
            int targetIndex = enemyPathIndex.get(enemy.getUniqueId());

            if (targetIndex >= path.size()) {
                // Arrivé au Nexus
                plugin.getSessionManager().damageNexus(1);
                removeEnemy(enemy, it);
                continue;
            }

            Location target = path.get(targetIndex);
            Location current = enemy.getLocation();
            double speed = enemy.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();

            if (current.distance(target) < speed) {
                enemyPathIndex.put(enemy.getUniqueId(), targetIndex + 1);
            } else {
                Vector dir = target.toVector().subtract(current.toVector()).normalize().multiply(speed);
                Location look = current.clone().setDirection(dir);
                enemy.setRotation(look.getYaw(), look.getPitch());
                enemy.setVelocity(dir);
            }
        }
    }
    
    private void removeEnemy(LivingEntity enemy, Iterator<LivingEntity> it) {
        enemy.remove();
        enemyPathIndex.remove(enemy.getUniqueId());
        enemyLaneIndex.remove(enemy.getUniqueId());
        it.remove();
    }
    
    // Appelé quand un monstre meurt (récompense)
    public void onEnemyKilled(LivingEntity enemy) {
        plugin.getSessionManager().rewardPlayer(null, 10.0);
        enemy.getWorld().spawnParticle(org.bukkit.Particle.WAX_ON, enemy.getLocation().add(0, 1, 0), 5);
    }

    public void clearAll() {
        for (LivingEntity e : activeEnemies) e.remove();
        activeEnemies.clear();
        enemyPathIndex.clear();
        enemyLaneIndex.clear();
        currentWaveIndex = 0;
        waveActive = false;
        currentGroup = null;
        groupsQueue.clear();
    }
    
    // --- Getters pour le Scoreboard (C'est ça qui te manquait) ---

    public List<LivingEntity> getEnemies() { return activeEnemies; }
    
    public int getEnemiesCount() {
        return activeEnemies.size();
    }

    public int getCurrentWave() {
        return currentWaveIndex;
    }

    public int getTotalWaves() {
        BnMap map = plugin.getMapManager().getActiveMap();
        return map != null ? map.getWaves().size() : 0;
    }
}