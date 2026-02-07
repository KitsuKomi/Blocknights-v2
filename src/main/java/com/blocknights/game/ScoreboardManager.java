package com.blocknights.game;

import com.blocknights.BlocknightsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

public class ScoreboardManager {

    private final BlocknightsPlugin plugin;

    public ScoreboardManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        startUpdater();
    }

    public void setupScoreboard(Player p) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("bn_stats", Criteria.DUMMY, Component.text("§b§lBLOCKNIGHTS"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Lignes statiques (pour l'instant, on mettra à jour via le runnable)
        Score line1 = obj.getScore("§7----------------");
        line1.setScore(15);

        p.setScoreboard(board);
    }

    private void startUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getSessionManager().isRunning()) return;

                for (GamePlayer gp : plugin.getSessionManager().getPlayers()) {
                    updateBoard(gp);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Mise à jour toutes les secondes
    }

    private void updateBoard(GamePlayer gp) {
        Scoreboard board = gp.getBukkitPlayer().getScoreboard();
        Objective obj = board.getObjective("bn_stats");
        if (obj == null) return;

        // Astuce pour éviter le scintillement sans lib externe : 
        // On utilise les Teams pour les valeurs dynamiques (c'est un peu verbeux mais natif)
        // OU Méthode simple "Brutale" (reset scores) pour commencer :
        
        // Nettoyage (Méthode simple pour prototype)
        for (String entry : board.getEntries()) {
            if (entry.contains("§")) board.resetScores(entry);
        }
        
        // Réaffichage
        int wave = plugin.getWaveManager().getCurrentWave();
        int maxWave = plugin.getWaveManager().getTotalWaves();
        
        setScore(obj, "§7----------------", 15);
        setScore(obj, "§fVague: §e" + wave + "§7/" + maxWave, 14);
        setScore(obj, "§b ", 13);
        setScore(obj, "§fLMD (Or): §6" + (int)gp.getMoney(), 12);
        setScore(obj, "§fVies: §c" + gp.getLives() + " ❤", 11);
        setScore(obj, "§a ", 10);
        setScore(obj, "§fEnnemis: §c" + plugin.getWaveManager().getEnemiesCount(), 9);
        setScore(obj, "§7---------------- ", 1);
    }
    
    private void setScore(Objective obj, String text, int score) {
        Score s = obj.getScore(text);
        s.setScore(score);
    }
}