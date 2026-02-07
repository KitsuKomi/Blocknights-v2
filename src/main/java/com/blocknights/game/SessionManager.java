package com.blocknights.game;

import com.blocknights.BlocknightsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private final BlocknightsPlugin plugin;
    private final ScoreboardManager scoreboardManager;
    private boolean isRunning = false;
    
    private final Map<UUID, GamePlayer> players = new HashMap<>();

    public SessionManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        this.scoreboardManager = new ScoreboardManager(plugin);
    }

    public void startGame() {
        if (isRunning) return;
        if (plugin.getMapManager().getActiveMap() == null) {
            Bukkit.broadcast(Component.text("Aucune map chargée !", NamedTextColor.RED));
            return;
        }

        isRunning = true;
        players.clear();

        // Setup des joueurs
        for (Player p : Bukkit.getOnlinePlayers()) {
            GamePlayer gp = new GamePlayer(p);
            players.put(p.getUniqueId(), gp);
            
            // Initialisation Scoreboard
            scoreboardManager.setupScoreboard(p);
            
            p.sendMessage(Component.text("=== MISSION START ===", NamedTextColor.GOLD));
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
        }
        plugin.getWaveManager().startNextWave();
    }
    
    public void victory() {
        if (!isRunning) return;
        
        // 1. Message Chat (Via LangManager)
        plugin.getLang().broadcast("game-victory-chat");
        
        // 2. Titre & Son pour tous les joueurs
        for (org.bukkit.entity.Player p : plugin.getServer().getOnlinePlayers()) {
            p.playSound(p.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
            
            // On récupère le texte brut pour le Title
            String title = plugin.getLang().getRaw("game-victory-title").replace("&", "§");
            String sub = plugin.getLang().getRaw("game-victory-subtitle").replace("&", "§");
            p.sendTitle(title, sub, 10, 70, 20);
        }

        stopGame(); // On arrête tout proprement
    }
    public void stopGame() {
        if (!isRunning) return;
        isRunning = false;
        
        plugin.getWaveManager().clearAll();
        
        // Reset Scoreboard
        for (GamePlayer gp : players.values()) {
            gp.getBukkitPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
        players.clear();
        
        Bukkit.broadcast(Component.text("Mission Terminée.", NamedTextColor.YELLOW));
    }

    public void damageNexus(int damage) {
        if (!isRunning) return;
        
        // Mode Coop : Dégâts partagés
        for (GamePlayer gp : players.values()) {
            gp.removeLife(damage);
            gp.getBukkitPlayer().playSound(gp.getBukkitPlayer().getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 1f);
            
            if (gp.getLives() <= 0) {
                failGame();
                return;
            }
        }
    }
    
    private void failGame() {
        Bukkit.broadcast(Component.text("=== MISSION FAILED ===", NamedTextColor.DARK_RED));
        Bukkit.broadcast(Component.text("Le Nexus a été détruit.", NamedTextColor.RED));
        for (GamePlayer gp : players.values()) {
            gp.getBukkitPlayer().playSound(gp.getBukkitPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
        }
        stopGame();
    }

    public void rewardPlayer(Player p, double money) {
        // Si null (ex: kill par une tour sans propriétaire direct), on partage ?
        // Pour l'instant on donne à tout le monde (Coop simple)
        if (p == null) {
            for (GamePlayer gp : players.values()) gp.addMoney(money);
        } else {
            GamePlayer gp = players.get(p.getUniqueId());
            if (gp != null) gp.addMoney(money);
        }
    }

    public GamePlayer getGamePlayer(Player p) {
        return players.get(p.getUniqueId());
    }
    
    public Collection<GamePlayer> getPlayers() {
        return players.values();
    }

    public boolean isRunning() { return isRunning; }
}