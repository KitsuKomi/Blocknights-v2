package com.blocknights.waves;

import com.blocknights.BlocknightsPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.*;

public class WaveManager {

    private final BlocknightsPlugin plugin;
    private final List<LivingEntity> enemies = new ArrayList<>();
    // Map pour savoir quel ennemi va vers quel point du chemin (Index)
    private final Map<UUID, Integer> enemyPathProgress = new HashMap<>();
    
    private int mobsToSpawn = 0;
    private long lastSpawnTime = 0;

    public WaveManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    public void startWave() {
        this.mobsToSpawn = 10; // Simple test
    }

    public void tick() {
        // 1. Spawning
        if (mobsToSpawn > 0 && System.currentTimeMillis() - lastSpawnTime > 1500) {
            spawnEnemy();
            mobsToSpawn--;
            lastSpawnTime = System.currentTimeMillis();
        }

        // 2. Mouvement (Rail System)
        Iterator<LivingEntity> it = enemies.iterator();
        List<Location> path = plugin.getMapManager().getPath();
        
        while (it.hasNext()) {
            LivingEntity enemy = it.next();
            
            // Nettoyage si mort
            if (enemy.isDead() || !enemy.isValid()) {
                enemyPathProgress.remove(enemy.getUniqueId());
                it.remove();
                continue;
            }

            int targetIndex = enemyPathProgress.getOrDefault(enemy.getUniqueId(), 1);
            
            // Arrivé au bout ?
            if (targetIndex >= path.size()) {
                plugin.getSessionManager().damageNexus(1);
                removeEnemy(enemy, it);
                continue;
            }

            Location target = path.get(targetIndex);
            Location current = enemy.getLocation();

            // Déplacement Mathématique (Pas d'IA)
            if (current.distance(target) < 0.5) {
                // Point atteint, on passe au suivant
                enemyPathProgress.put(enemy.getUniqueId(), targetIndex + 1);
            } else {
                // On pousse vers la cible
                Vector dir = target.toVector().subtract(current.toVector()).normalize().multiply(0.2); // Vitesse
                enemy.setVelocity(dir);
                
                // Rotation visuelle
                Location look = current.clone().setDirection(dir);
                enemy.setRotation(look.getYaw(), look.getPitch());
            }
        }
    }

    private void spawnEnemy() {
        Location spawn = plugin.getMapManager().getSpawnPoint();
        if (spawn == null) return;

        LivingEntity enemy = (LivingEntity) spawn.getWorld().spawnEntity(spawn, EntityType.ZOMBIE);
        enemy.setAI(false); // Zéro Lag
        enemy.setGravity(false); // Flotte légèrement
        enemy.customName(Component.text("Ennemi V2", NamedTextColor.RED));
        enemy.setCustomNameVisible(true);
        
        enemies.add(enemy);
        enemyPathProgress.put(enemy.getUniqueId(), 1);
    }
    
    private void removeEnemy(LivingEntity enemy, Iterator<LivingEntity> it) {
        enemy.remove();
        enemyPathProgress.remove(enemy.getUniqueId());
        it.remove();
    }

    public void clearAll() {
        for (LivingEntity e : enemies) e.remove();
        enemies.clear();
        enemyPathProgress.clear();
    }
    
    public List<LivingEntity> getEnemies() { return enemies; }
}