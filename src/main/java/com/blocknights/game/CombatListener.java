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
        // On ne gère que les entités vivantes
        if (!(e.getEntity() instanceof LivingEntity victim)) return;

        double damage = 0;
        double defense = 0;
        boolean isCustomHit = false;

        // --- 1. DÉTERMINER L'ATTAQUE (ATK) ---
        
        // CAS A : Projectile (Flèche d'un Sniper)
        if (e.getDamager() instanceof Projectile proj) {
            if (proj.hasMetadata("bn_damage")) {
                damage = proj.getMetadata("bn_damage").get(0).asDouble();
                isCustomHit = true;
            }
        }
        // CAS B : Melee (Corps à Corps)
        else if (e.getDamager() instanceof LivingEntity attacker) {
            // Est-ce un Opérateur (NPC) ?
            if (CitizensAPI.getNPCRegistry().isNPC(attacker)) {
                GameOperator op = plugin.getOperatorManager().getOperatorByEntity(attacker);
                if (op != null) {
                    damage = op.getDefinition().getAtk();
                    isCustomHit = true;
                }
            }
            // Est-ce un Ennemi (Zombie) ?
            else if (attacker.hasMetadata("bn_atk")) {
                damage = attacker.getMetadata("bn_atk").get(0).asDouble();
                isCustomHit = true;
            }
        }

        if (!isCustomHit) return; // Si c'est un dégât vanilla (feu, chute, joueur créatif), on touche pas

        // --- 2. DÉTERMINER LA DÉFENSE (DEF) ---

        // Est-ce que la victime est un Opérateur ?
        if (CitizensAPI.getNPCRegistry().isNPC(victim)) {
            GameOperator op = plugin.getOperatorManager().getOperatorByEntity(victim);
            if (op != null) {
                defense = op.getDefinition().getDef();
                
                // On délègue la gestion de la vie à notre classe GameOperator
                // car Citizens gère mal les PV > 20 par défaut
                e.setCancelled(true); // On annule les dégâts Minecraft
                op.takeDamage(calculateFinalDamage(damage, defense));
                return; 
            }
        }// Dégât vanilla (souvent 0 si tu gères tout toi-même, mais gardons le pour l'instant)
        
        // --- MISE À JOUR BARRE DE VIE ENNEMIE ---
        if (victim.hasMetadata("bn_def")) { // C'est un ennemi à nous
            // On récupère ses PV actuels (Attention : Bukkit gère les PV vanilla, 
            // si tu utilises des PV > 2048, il faut stocker les PV actuels dans une Metadata aussi !)
            
            // Pour simplifier ici, supposons que tu utilises les PV Vanilla scalés (Attribute Modifier)
            double current = victim.getHealth() - finalDamage;
            double max = victim.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
            
            // On appelle la méthode de mise à jour (faut la rendre accessible ou la copier ici)
            plugin.getWaveManager().updateEnemyHealthBar(victim, Math.max(0, current), max);
        }
        if (e.getDamager().hasMetadata("bn_aoe_radius")) {
        double radius = e.getDamager().getMetadata("bn_aoe_radius").get(0).asDouble();
        double centerDamage = e.getFinalDamage(); // Dégâts initiaux calculés
        
        // On récupère les entités autour
        for (org.bukkit.entity.Entity nearby : e.getEntity().getNearbyEntities(radius, radius, radius)) {
            if (nearby == e.getEntity()) continue; // Déjà touché
            if (!(nearby instanceof LivingEntity target)) continue;
            
            // On vérifie que c'est un ENNEMI (via metadata bn_def ou liste WaveManager)
            if (target.hasMetadata("bn_def")) {
                // On applique les dégâts (réduits avec la distance si tu veux, ici brut)
                // Attention : ça va rappeler onDamage récursivement, donc le calcul DEF se fera !
                ((LivingEntity) nearby).damage(centerDamage, e.getDamager());
            }
        }
        
        // Effet visuel
        e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), 0F, false); // 0F = pas de dégâts blocs
    }
        // Est-ce que la victime est un Ennemi ?
        else if (victim.hasMetadata("bn_def")) {
            defense = victim.getMetadata("bn_def").get(0).asDouble();
        }

        // --- 3. CALCUL FINAL ---
        double finalDamage = calculateFinalDamage(damage, defense);

        // Application
        e.setDamage(finalDamage);
        
        // Petit son critique si gros dégâts
        if (finalDamage > 50) {
            victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f);
        }
    }

    private double calculateFinalDamage(double atk, double def) {
        // Formule : ATK - DEF. Minimum 1 dégât.
        return Math.max(1.0, atk - def);
    }
}