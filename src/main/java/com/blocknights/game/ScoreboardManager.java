package com.blocknights.game;

import com.blocknights.BlocknightsPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

public class ScoreboardManager {

    private final BlocknightsPlugin plugin;
    private final Scoreboard scoreboard;
    private final Objective objective;

    public ScoreboardManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        
        // Titre via I18n
        String title = plugin.getLang().getRaw("sb-title").replace("&", "§");
        this.objective = scoreboard.registerNewObjective("blocknights", Criteria.DUMMY, Component.text(title));
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        startUpdateTask();
    }

    // CORRECTION : Renommé pour correspondre à l'appel dans SessionManager
    public void setupScoreboard(Player p) {
        p.setScoreboard(scoreboard);
    }

    public void removePlayer(Player p) {
        p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    private void startUpdateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getSessionManager().isRunning()) return;
                updateBoard();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void updateBoard() {
        // Nettoyage brut (méthode simple)
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        // Données
        int wave = plugin.getWaveManager().getCurrentWave();
        int maxWaves = plugin.getWaveManager().getTotalWaves();
        int lives = plugin.getSessionManager().getNexusLives(); // Corrigé via étape 3
        int enemies = plugin.getWaveManager().getEnemiesCount();
        
        // CORRECTION ERREUR COLLECTION :
        // getPlayers() retourne une Collection, on ne peut pas faire .get(0).
        // On prend le premier joueur trouvé (système solo/coop simple)
        double money = 0;
        var players = plugin.getSessionManager().getPlayers();
        if (!players.isEmpty()) {
            money = players.iterator().next().getMoney();
        }

        // Affichage I18N
        score(txt("sb-separator"), 10);
        score(txt("sb-wave", "{current}", String.valueOf(wave), "{max}", String.valueOf(maxWaves)), 9);
        score(txt("sb-enemies", "{count}", String.valueOf(enemies)), 8);
        score("§f ", 7); // Espace vide
        score(txt("sb-nexus", "{lives}", String.valueOf(lives)), 6);
        score(txt("sb-money", "{money}", String.valueOf((int)money)), 5);
        score(txt("sb-separator"), 4);
        score(txt("sb-footer"), 3);
    }

    private void score(String text, int score) {
        objective.getScore(text).setScore(score);
    }
    
    // Helper pour récupérer et formater le texte
    private String txt(String key, String... placeholders) {
        String msg = plugin.getLang().getRaw(key);
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            msg = msg.replace(placeholders[i], placeholders[i + 1]);
        }
        return msg.replace("&", "§");
    }
}