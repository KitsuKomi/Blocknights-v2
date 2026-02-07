package com.blocknights.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.UUID;

public class GamePlayer {
    
    private final UUID uuid;
    private double money;

    // Constructeur principal
    public GamePlayer(UUID uuid, double money) {
        this.uuid = uuid;
        this.money = money;
    }

    // --- CORRECTION ERREUR 2 : Constructeur de commodité ---
    // Permet de faire new GamePlayer(player) sans se soucier de l'argent (par défaut 0 ou config)
    public GamePlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.money = 1000; // Argent par défaut si non spécifié
    }

    public UUID getUniqueId() { return uuid; }
    
    public double getMoney() { return money; }
    public void addMoney(double amount) { this.money += amount; }
    public void removeMoney(double amount) { this.money = Math.max(0, this.money - amount); }

    // Méthode standard
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    // --- CORRECTION ERREURS 4,5,6,8,9 : Alias pour compatibilité ---
    // Ton code appelle getBukkitPlayer(), donc on redirige vers getPlayer()
    public Player getBukkitPlayer() {
        return getPlayer();
    }
}