package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class GameOperator {

    private final OperatorDefinition definition;
    private final LivingEntity entity;
    private long lastAttackTime = 0;

    public GameOperator(OperatorDefinition definition, LivingEntity entity) {
        this.definition = definition;
        this.entity = entity;
        
        // Configuration "Statue"
        entity.setAI(false);
        entity.setGravity(false);
        entity.setInvulnerable(true);
        entity.setSilent(true);
        entity.setCustomNameVisible(true);
        entity.customName(net.kyori.adventure.text.Component.text("§b" + definition.getName()));
    }

    public void tick(BlocknightsPlugin plugin) {
        if (!entity.isValid()) return;

        long now = System.currentTimeMillis();
        long cooldownMs = definition.getAttackSpeed() * 50L;

        if (now - lastAttackTime < cooldownMs) return;

        LivingEntity target = findTarget(plugin);
        if (target != null) {
            attack(target);
            lastAttackTime = now;
        }
    }

    private LivingEntity findTarget(BlocknightsPlugin plugin) {
        LivingEntity bestTarget = null;
        double minDistSq = definition.getRange() * definition.getRange();

        for (LivingEntity enemy : plugin.getWaveManager().getEnemies()) {
            if (enemy.isDead() || !enemy.isValid()) continue;

            double distSq = enemy.getLocation().distanceSquared(entity.getLocation());
            if (distSq <= minDistSq) {
                minDistSq = distSq;
                bestTarget = enemy;
            }
        }
        return bestTarget;
    }

    private void attack(LivingEntity target) {
        // Rotation visuelle
        lookAt(target.getLocation());

        // Effet Visuel (Raycast simple)
        Location start = entity.getEyeLocation();
        Location end = target.getEyeLocation();
        Vector dir = end.toVector().subtract(start.toVector()).normalize();
        
        double dist = start.distance(end);
        for (double d = 0; d < dist; d += 0.5) {
            Location p = start.clone().add(dir.clone().multiply(d));
            p.getWorld().spawnParticle(Particle.CRIT, p, 1, 0, 0, 0, 0);
        }
        
        // Audio & Dégâts
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ARROW_SHOOT, 0.5f, 2.0f);
        target.damage(definition.getDamage());
    }

    private void lookAt(Location targetLoc) {
        Location loc = entity.getLocation();
        Vector dir = targetLoc.toVector().subtract(loc.toVector()).normalize();
        Location look = loc.clone().setDirection(dir);
        entity.teleport(look);
    }
    
    public void remove() {
        if (entity != null) entity.remove();
    }

    // C'EST CETTE MÉTHODE QUI TE MANQUAIT :
    public Location getLocation() {
        return entity.getLocation();
    }
}