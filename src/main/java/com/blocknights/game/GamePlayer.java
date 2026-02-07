package com.blocknights.game;

import org.bukkit.entity.Player;
import java.util.UUID;

public class GamePlayer {
    
    private final Player bukkitPlayer;
    private double money; // "LMD" (Monnaie Arknights)
    private int lives;    // "Defense Seals" (Vies)
    private int kills;

    public GamePlayer(Player player) {
        this.bukkitPlayer = player;
        // Valeurs par défaut (à configurer plus tard via map settings)
        this.money = 1000.0; // De quoi acheter 2-3 opérateurs
        this.lives = 30;
        this.kills = 0;
    }

    public void addMoney(double amount) {
        this.money += amount;
    }

    public boolean removeMoney(double amount) {
        if (money >= amount) {
            money -= amount;
            return true;
        }
        return false;
    }

    public void removeLife(int amount) {
        this.lives -= amount;
        if (this.lives < 0) this.lives = 0;
    }

    public void addKill() {
        this.kills++;
    }

    public Player getBukkitPlayer() { return bukkitPlayer; }
    public double getMoney() { return money; }
    public int getLives() { return lives; }
    public int getKills() { return kills; }
}