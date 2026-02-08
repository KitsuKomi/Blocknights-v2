package com.blocknights.game;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.game.operator.GameOperator;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatListener implements Listener {

    private final BlocknightsPlugin plugin;

    public CombatListener(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity victim)) return;

        double damage = 0;
        double defense = 0;
        boolean isCustomHit = false;

        // --- 1. DÉTERMINER L'ATTAQUE (ATK) ---
        if (e.getDamager() instanceof Projectile proj && proj.hasMetadata("bn_damage")) {
            // Cas A : Projectile (Flèche/Boule de feu marquée)
            damage = proj.getMetadata("bn_damage").get(0).asDouble();
            isCustomHit = true;
        } else if (e.getDamager() instanceof LivingEntity attacker) {
            // Cas B : Corps à corps
            if (net.citizensnpcs.api.CitizensAPI.getNPCRegistry().isNPC(attacker)) {
                // Attaquant est un Opérateur
                GameOperator op = plugin.getOperatorManager().getOperatorByEntity(attacker);
                if (op != null) {
                    damage = op.getDefinition().getAtk();
                    isCustomHit = true;
                }
            } else if (attacker.hasMetadata("bn_atk")) {
                // Attaquant est un Ennemi
                damage = attacker.getMetadata("bn_atk").get(0).asDouble();
                isCustomHit = true;
            }
        }

        if (!isCustomHit) return; // Pas touche aux dégâts vanilla

        // --- 2. DÉTERMINER LA DÉFENSE (DEF) & CALCUL FINAL ---
        
        // CAS 1 : La victime est un Opérateur (Nos Tanks)
        if (net.citizensnpcs.api.CitizensAPI.getNPCRegistry().isNPC(victim)) {
            GameOperator op = plugin.getOperatorManager().getOperatorByEntity(victim);
            if (op != null) {
                defense = op.getDefinition().getDef();
                double finalDmg = Math.max(1.0, damage - defense);
                
                e.setCancelled(true); // On annule les dégâts Minecraft
                op.takeDamage(finalDmg); // On gère nos propres PV
                return; // Fini pour l'opérateur
            }
        }
        
        // CAS 2 : La victime est un Ennemi (Zombie)
        if (victim.hasMetadata("bn_def")) {
            defense = victim.getMetadata("bn_def").get(0).asDouble();
        }

        // Calcul des dégâts finaux
        double finalDamage = Math.max(1.0, damage - defense);

        // Application sur l'ennemi
        e.setDamage(finalDamage);

        // --- 3. UI (Barre de vie Ennemie) ---
        if (victim.hasMetadata("bn_def")) {
            double currentHP = victim.getHealth() - finalDamage;
            double maxHP = victim.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
            plugin.getWaveManager().updateEnemyHealthBar(victim, Math.max(0, currentHP), maxHP);
        }
        
        // Petit son critique
        if (finalDamage > 50) {
            victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f);
        }

        // --- 4. GESTION DES DÉGÂTS DE ZONE (AOE) ---
        // On le fait APRÈS avoir calculé le finalDamage
        if (e.getDamager().hasMetadata("bn_aoe_radius")) {
            double radius = e.getDamager().getMetadata("bn_aoe_radius").get(0).asDouble();
            
            // Effet visuel
            e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), 0F, false);

            for (org.bukkit.entity.Entity nearby : e.getEntity().getNearbyEntities(radius, radius, radius)) {
                if (nearby == e.getEntity()) continue; // Déjà touché
                if (!(nearby instanceof LivingEntity target)) continue;
                
                // On ne touche que les ENNEMIS
                if (target.hasMetadata("bn_def")) {
                    // On applique les dégâts du centre de l'explosion
                    ((LivingEntity) nearby).damage(finalDamage, e.getDamager());
                }
            }
        }
    }

    private double calculateFinalDamage(double atk, double def) {
        // Formule : ATK - DEF. Minimum 1 dégât.
        return Math.max(1.0, atk - def);
    }
}