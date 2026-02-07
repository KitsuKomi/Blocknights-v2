package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GameOperator {

    private final OperatorDefinition definition;
    private final NPC npc; // <--- C'est lui le patron maintenant
    private long lastAttackTime = 0;
    
    private final List<LivingEntity> blockedEnemies = new ArrayList<>();

    public GameOperator(OperatorDefinition definition, NPC npc) {
        this.definition = definition;
        this.npc = npc;
    }

    // --- Logique Blocage (Inchangée) ---
    public boolean canBlock() {
        blockedEnemies.removeIf(e -> e.isDead() || !e.isValid());
        return blockedEnemies.size() < definition.getBlockCount();
    }

    public void addBlockedEnemy(LivingEntity enemy) {
        if (!blockedEnemies.contains(enemy)) blockedEnemies.add(enemy);
    }
    
    public List<LivingEntity> getBlockedEnemies() { return blockedEnemies; }

    // --- Boucle ---
    public void tick(BlocknightsPlugin plugin) {
        if (!npc.isSpawned()) return;

        long now = System.currentTimeMillis();
        long cooldownMs = definition.getAttackSpeed() * 50L; // 50ms par tick

        if (now - lastAttackTime < cooldownMs) return;

        LivingEntity target = findTarget(plugin);
        if (target != null) {
            attack(target);
            lastAttackTime = now;
        }
    }

    private LivingEntity findTarget(BlocknightsPlugin plugin) {
        // ... (Logique inchangée : target blocked first, then closest) ...
        // Juste remplacer entity.getLocation() par npc.getStoredLocation()
        
        if (!blockedEnemies.isEmpty()) return blockedEnemies.get(0);

        LivingEntity bestTarget = null;
        double minDistSq = definition.getRange() * definition.getRange();
        Location myLoc = npc.getStoredLocation();

        for (LivingEntity enemy : plugin.getWaveManager().getEnemies()) {
            if (enemy.isDead() || !enemy.isValid()) continue;
            if (enemy.getLocation().distanceSquared(myLoc) <= minDistSq) {
                minDistSq = enemy.getLocation().distanceSquared(myLoc);
                bestTarget = enemy;
            }
        }
        return bestTarget;
    }

    private void attack(LivingEntity target) {
        // 1. Animation Citizens : Regarder la cible
        npc.faceLocation(target.getLocation());
        
        // 2. Animation Citizens : Coup de main (Arm Swing)
        if (npc.getEntity() instanceof Player) {
            ((Player) npc.getEntity()).swingMainHand();
        }

        // 3. Effets
        Location loc = npc.getStoredLocation().add(0, 1, 0);
        loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
        
        // 4. Dégâts
        target.damage(definition.getAtk());
    }
    
    public void remove() {
        // IMPORTANT : Détruire le NPC proprement
        npc.destroy();
        blockedEnemies.clear();
    }
    
    public Location getLocation() { return npc.getStoredLocation(); }
    public OperatorDefinition getDefinition() { return definition; }
}