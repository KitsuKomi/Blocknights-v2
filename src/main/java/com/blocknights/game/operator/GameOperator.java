package com.blocknights.game.operator;

import com.blocknights.BlocknightsPlugin;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameOperator {

    private final OperatorDefinition definition;
    private final NPC npc;
    
    // Liste des ennemis actuellement retenus par cet opérateur
    private final List<LivingEntity> blockedEnemies = new ArrayList<>();
    
    private double currentHealth;
    private long lastAttackTime = 0;

    public GameOperator(OperatorDefinition definition, NPC npc) {
        this.definition = definition;
        this.npc = npc;
        
        // On initialise avec les stats de la définition (Grosses stats acceptées)
        this.currentHealth = definition.getMaxHealth();
        
        // On applique les PV visuels au NPC (Barre de vie au dessus de la tête)
        if (npc.isSpawned() && npc.getEntity() instanceof LivingEntity living) {
            // Permet d'avoir 2000 PV si on veut
            living.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(definition.getMaxHealth());
            living.setHealth(definition.getMaxHealth());
        }
    }

    public OperatorDefinition getDefinition() { return definition; }
    public NPC getNPC() { return npc; }
    public Location getLocation() { return npc.getStoredLocation(); }
    public List<LivingEntity> getBlockedEnemies() { return blockedEnemies; }

    // --- LOGIQUE DE BLOCAGE ---

    public boolean canBlock() {
        // On vérifie si on n'a pas atteint la limite (ex: Block Count = 3)
        return blockedEnemies.size() < definition.getBlockCount();
    }

    public void addBlockedEnemy(LivingEntity enemy) {
        if (!blockedEnemies.contains(enemy)) {
            blockedEnemies.add(enemy);
        }
    }

    // --- LOGIQUE DE COMBAT (TICK) ---
    // Cette méthode est appelée par OperatorManager toutes les X secondes (ex: 2 ticks)
    public void tick(BlocknightsPlugin plugin) {
        if (!npc.isSpawned()) return;

        // 1. Nettoyage (ennemis morts)
        blockedEnemies.removeIf(e -> e.isDead() || !e.isValid());

        long now = System.currentTimeMillis();
        // Vérification du Cooldown d'attaque
        if (now - lastAttackTime < (definition.getAttackSpeed() * 1000)) {
            return; // On attend
        }

        // 2. PRIORITÉ : Attaquer ceux qu'on bloque (Corps à corps)
        if (!blockedEnemies.isEmpty()) {
            attackBlockedEnemies();
            lastAttackTime = now;
            return; // On ne tire pas si on est occupé au corps à corps
        }

        // 3. SECONDAIRE : Tirer à distance (si on est une unité à distance)
        if (definition.isRanged()) {
            LivingEntity target = findTarget(plugin);
            if (target != null) {
                shootAt(target);
                lastAttackTime = now;
            }
        }
    }

    private LivingEntity findTarget(BlocknightsPlugin plugin) {
        // On récupère la liste de tous les ennemis vivants via le WaveManager
        List<LivingEntity> potentialTargets = plugin.getWaveManager().getEnemies();
        
        LivingEntity bestTarget = null;
        double bestDistSq = Double.MAX_VALUE;
        double rangeSq = definition.getRange() * definition.getRange();

        Location myLoc = getLocation();

        for (LivingEntity e : potentialTargets) {
            if (e.isDead() || !e.isValid()) continue;

            double distSq = e.getLocation().distanceSquared(myLoc);
            
            // Si hors de portée, on ignore
            if (distSq > rangeSq) continue;

            // STRATÉGIE DE CIBLAGE : "FIRST" (Le plus avancé sur le chemin)
            // Pour l'instant, on fait "CLOSEST" (Le plus proche de moi) pour simplifier le code,
            // mais idéalement on utiliserait l'index du pathfinding stocké dans WaveManager.
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                bestTarget = e;
            }
        }
        return bestTarget;
    }

    // --- NOUVELLE MÉTHODE : TIRER ---
    private void shootAt(LivingEntity target) {
        Location myLoc = getLocation().add(0, 1.5, 0); // Tirer depuis les yeux (environ)
        Location targetLoc = target.getLocation().add(0, 1.0, 0); // Viser le torse
        
        // Orientation du NPC vers la cible
        npc.faceLocation(targetLoc);
        
        // Logique selon le type de projectile
        String type = definition.getProjectileType();
        
        if (type.equalsIgnoreCase("ARROW")) {
            org.bukkit.entity.Arrow arrow = myLoc.getWorld().spawn(myLoc.add(myLoc.getDirection()), org.bukkit.entity.Arrow.class);
            arrow.setVelocity(targetLoc.toVector().subtract(myLoc.toVector()).normalize().multiply(2.0));
            
            // --- NOUVEAU : On marque la flèche avec les dégâts de l'opérateur ---
            // On ajoute metadata "bn_damage"
            arrow.setMetadata("bn_damage", new org.bukkit.metadata.FixedMetadataValue(
                net.citizensnpcs.api.CitizensAPI.getPlugin(), // Ou ton plugin instance si accessible
                definition.getAtk()
            ));
            
            // On marque aussi qui a tiré (pour les stats de kill plus tard)
            arrow.setMetadata("bn_shooter", new org.bukkit.metadata.FixedMetadataValue(
                net.citizensnpcs.api.CitizensAPI.getPlugin(), 
                npc.getUniqueId().toString()
            ));
            
        } else if (type.equalsIgnoreCase("MAGIC")) {
            // Tir Instantané (Hitscan) + Particules
            target.damage(definition.getAtk());
            
            // Effet visuel : Ligne de particules
            org.bukkit.util.Vector dir = targetLoc.toVector().subtract(myLoc.toVector()).normalize();
            for (double d = 0; d < myLoc.distance(targetLoc); d += 0.5) {
                myLoc.add(dir.multiply(0.5));
                myLoc.getWorld().spawnParticle(org.bukkit.Particle.HAPPY_VILLAGER, myLoc, 1);
                myLoc.subtract(dir.multiply(0.5)); // Reset pour la boucle
                dir.normalize(); // Reset
            }
            myLoc.getWorld().playSound(myLoc, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1f, 1.5f);
        }
        
        // Animation
        if (npc.getEntity() instanceof Player p) p.swingMainHand();
    }

    private void attackBlockedEnemies() {
        // Un tank tape généralement tous les ennemis qu'il bloque (ou 1 seul selon design)
        // Ici, on va dire qu'il tape tout ce qu'il bloque (AoE sur sa case)
        Iterator<LivingEntity> it = blockedEnemies.iterator();
        while (it.hasNext()) {
            LivingEntity target = it.next();
            target.damage(definition.getAtk());
            
            // Effet visuel de coup
            npc.getEntity().getWorld().playSound(getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
            // Animation de bras (Swing)
            if (npc.getEntity() instanceof Player p) p.swingMainHand();
        }
    }

    // --- PRENDRE DES DÉGÂTS ---

    public void takeDamage(double amount) {
        // Calcul réduction dégâts via Défense
        double realDamage = Math.max(1, amount - definition.getDef());
        
        this.currentHealth -= realDamage;
        
        // Mise à jour visuelle
        if (npc.isSpawned() && npc.getEntity() instanceof LivingEntity living) {
            // On met à jour la barre de vie visuelle
            living.setHealth(Math.max(0, Math.min(currentHealth, definition.getMaxHealth())));
            
            // CORRECTION ICI : On joue l'effet "HURT" pour le faire rougir
            living.playEffect(org.bukkit.EntityEffect.HURT);
        }

        // Mort de l'opérateur
        if (this.currentHealth <= 0) {
            die();
        }
    }

    // ...

    public void remove() {
        if (npc.isSpawned()) npc.destroy();
        blockedEnemies.clear();
    }
    
    private void die() {
        // Libère tous les ennemis
        blockedEnemies.clear();
        
        if (npc.isSpawned()) {
            npc.despawn(); // Ou destroy direct
        }
        // TODO: Prévenir le Manager pour le retirer de la liste active
    }
    
    // Ajout getter manquant pour les corrections précédentes
    public LivingEntity getEntity() {
        return (LivingEntity) npc.getEntity();
    }
}