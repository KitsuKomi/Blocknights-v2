package com.blocknights.game;

import com.blocknights.BlocknightsPlugin;
import com.blocknights.maps.BnMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private final BlocknightsPlugin plugin;
    private boolean isRunning = false;
    private final List<GamePlayer> players = new ArrayList<>();
    
    private int nexusLives = 20; 

    public SessionManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isRunning() { return isRunning; }
    public List<GamePlayer> getPlayers() { return players; }
    public int getNexusLives() { return nexusLives; }

    public GamePlayer getGamePlayer(Player p) {
        for (GamePlayer gp : players) {
            if (gp.getUniqueId().equals(p.getUniqueId())) return gp;
        }
        return null; // Devrait rarement arriver si le jeu est lancé
    }

    public void startGame() {
        if (isRunning) return;
        
        BnMap map = plugin.getMapManager().getActiveMap();
        if (map == null) return; 

        isRunning = true;
        players.clear();
        this.nexusLives = map.getInitialLives();

        for (Player p : Bukkit.getOnlinePlayers()) {
            GamePlayer gp = new GamePlayer(p); 
            // Ajustement argent selon la map (Base 1000 dans GamePlayer, on ajoute la diff)
            gp.addMoney(map.getInitialMoney() - 1000); 
            players.add(gp);

            p.setGameMode(GameMode.ADVENTURE);
            p.getInventory().clear();
            
            if (plugin.getMapManager().getSpawnPoint() != null) {
                p.teleport(plugin.getMapManager().getSpawnPoint());
            }

            // Scoreboard (Maintenant ça compile car le getter existe dans Plugin)
            plugin.getScoreboardManager().setupScoreboard(p);
            
            // Message I18n
            plugin.getLang().send(p, "game-start");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
        }
        
        plugin.getWaveManager().startNextWave();
    }

    public void stopGame() {
        isRunning = false;
        
        // Nettoyage entités
        if (plugin.getWaveManager() != null) plugin.getWaveManager().clearAll();
        if (plugin.getOperatorManager() != null) plugin.getOperatorManager().clearAll();

        // Reset Joueurs
        for (GamePlayer gp : players) {
            Player p = gp.getBukkitPlayer();
            if (p != null) {
                p.getInventory().clear();
                p.setGameMode(GameMode.CREATIVE);
                if (plugin.getScoreboardManager() != null) {
                    plugin.getScoreboardManager().removePlayer(p);
                }
            }
        }
        players.clear();
        plugin.getLang().broadcast("game-stop");
    }

    public void damageNexus(int damage) {
        if (!isRunning) return;

        this.nexusLives -= damage;
        
        // Message Chat I18n
        plugin.getLang().broadcast("nexus-damaged-chat", 
            "{damage}", String.valueOf(damage),
            "{lives}", String.valueOf(nexusLives)
        );
        
        // Titres & Sons I18n
        String title = plugin.getLang().getRaw("nexus-damaged-title").replace("&", "§");
        String sub = plugin.getLang().getRaw("nexus-damaged-subtitle").replace("&", "§");

        for (GamePlayer gp : players) {
            Player p = gp.getBukkitPlayer();
            if (p != null) {
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 2f);
                p.sendTitle(title, sub, 0, 20, 10);
            }
        }

        if (this.nexusLives <= 0) {
            gameOver();
        }
    }

    private void gameOver() {
        plugin.getLang().broadcast("game-over-chat");
        
        String title = plugin.getLang().getRaw("game-over-title").replace("&", "§");
        String sub = plugin.getLang().getRaw("game-over-subtitle").replace("&", "§");

        for (GamePlayer gp : players) {
            Player p = gp.getBukkitPlayer();
            if (p != null) {
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 0.5f);
                p.sendTitle(title, sub, 10, 100, 20);
            }
        }
        stopGame();
    }
    
    public void victory() {
        if (!isRunning) return;
        
        plugin.getLang().broadcast("game-victory-chat");
        
        String title = plugin.getLang().getRaw("game-victory-title").replace("&", "§");
        String sub = plugin.getLang().getRaw("game-victory-subtitle").replace("&", "§");

        for (GamePlayer gp : players) {
            Player p = gp.getBukkitPlayer();
            if (p != null) {
                p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
                p.sendTitle(title, sub, 10, 70, 20);
            }
        }
        stopGame();
    }
}