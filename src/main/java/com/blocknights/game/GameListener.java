package com.blocknights.game;

import com.blocknights.BlocknightsPlugin;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class GameListener implements Listener {

    private final BlocknightsPlugin plugin;

    public GameListener(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        // 1. Vérifier si la partie est en cours
        if (!plugin.getSessionManager().isRunning()) return;

        LivingEntity entity = e.getEntity();

        // 2. Vérifier si c'est un ennemi du jeu (géré par WaveManager)
        if (plugin.getWaveManager().getEnemies().contains(entity)) {
            
            e.getDrops().clear(); // Pas de viande pourrie au sol
            e.setDroppedExp(0);   // Pas d'XP vanilla

            // 3. Gain d'argent (Configurable plus tard, disons 15 par kill pour l'instant)
            int reward = 15; 
            
            // On donne l'argent à tous les joueurs de la session (Coop)
            for (GamePlayer gp : plugin.getSessionManager().getPlayers()) {
                gp.addMoney(reward);
                
                Player p = gp.getPlayer();
                if (p != null) {
                    // Petit son "Ding" discret (Pitch haut)
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 2.0f);
                    // Action bar pour feedback immédiat
                    p.sendActionBar(net.kyori.adventure.text.Component.text("§6+" + reward + " ⛃"));
                }
            }
            
            // Retirer de la liste des ennemis actifs
            // Note : WaveManager le fait peut-être déjà via un Runnable, mais c'est plus sûr ici
            // (Si ton WaveManager nettoie les morts, c'est bon, sinon ajoute une méthode removeEnemy)
        }
    }
}