package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OperatorManager {

    private final BlocknightsPlugin plugin;
    private final List<Location> towers = new ArrayList<>();

    public OperatorManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    public void placeOperator(Player p) {
        Location loc = p.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        loc.getBlock().setType(Material.EMERALD_BLOCK); // Visuel temporaire
        towers.add(loc);
        p.sendMessage("§aOpérateur placé !");
    }

    public void tick() {
        for (Location tower : towers) {
            // Tirer toutes les secondes environ (1 chance sur 20 ticks)
            if (Math.random() > 0.05) continue;

            LivingEntity target = findTarget(tower);
            if (target != null) {
                // Effet de tir
                tower.getWorld().spawnParticle(Particle.COMPOSTER, target.getEyeLocation(), 10);
                tower.getWorld().playSound(tower, Sound.ENTITY_BLAZE_HURT, 0.5f, 2f);
                target.damage(5);
            }
        }
    }

    private LivingEntity findTarget(Location tower) {
        double minDst = 8.0;
        LivingEntity best = null;
        
        for (LivingEntity enemy : plugin.getWaveManager().getEnemies()) {
            double dst = enemy.getLocation().distance(tower);
            if (dst < minDst) {
                minDst = dst;
                best = enemy;
            }
        }
        return best;
    }
}