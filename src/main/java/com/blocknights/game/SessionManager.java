package com.blocknights.game;

import com.blocknights.BlocknightsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class SessionManager {

    private final BlocknightsPlugin plugin;
    private boolean isRunning = false;
    private int nexusLife = 20;

    public SessionManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isRunning) {
                    tick();
                }
            }
        }.runTaskTimer(plugin, 20L, 1L);
    }

    private void tick() {
        plugin.getWaveManager().tick();
        plugin.getOperatorManager().tick();
    }

    public void startGame() {
        if (plugin.getMapManager().getPath().size() < 2) {
            Bukkit.broadcast(Component.text("Impossible de lancer : Chemin non défini !", NamedTextColor.RED));
            return;
        }
        
        this.nexusLife = 20;
        this.isRunning = true;
        
        plugin.getWaveManager().startWave();
        
        Bukkit.broadcast(Component.text("=== BLOCKNIGHTS V2 : START ===", NamedTextColor.GREEN));
    }

    public void stopGame() {
        this.isRunning = false;
        plugin.getWaveManager().clearAll();
        Bukkit.broadcast(Component.text("Partie terminée.", NamedTextColor.YELLOW));
    }

    public void damageNexus(int amount) {
        this.nexusLife -= amount;
        
        // Alerte rouge quand on prend des dégâts
        Bukkit.broadcast(Component.text("Nexus touché ! Vies restantes : " + nexusLife, NamedTextColor.RED));
        
        if (this.nexusLife <= 0) {
            Bukkit.broadcast(Component.text("DEFAITE !", NamedTextColor.DARK_RED));
            stopGame();
        }
    }
    
    public boolean isRunning() { return isRunning; }
}