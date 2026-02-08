package com.blocknights.utils;

import com.blocknights.BlocknightsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LangManager {

    private final BlocknightsPlugin plugin;
    private final Map<String, String> messages = new HashMap<>();
    private FileConfiguration config;

    public LangManager(BlocknightsPlugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void broadcast(String key, String... placeholders) {
        String msg = getRaw(key); // Récupère le message brut
        
        // Remplace les {variables}
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            msg = msg.replace(placeholders[i], placeholders[i + 1]);
        }
        
        // Applique les couleurs
        msg = msg.replace("&", "§");
        
        // Ajoute le préfixe global si tu en as un (optionnel)
        String prefix = messages.getOrDefault("prefix", "§8[§bBlocknights§8] §r");
        
        // Envoie à tout le serveur
        plugin.getServer().broadcast(net.kyori.adventure.text.Component.text(prefix + msg));
    }
    
    public void loadMessages() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        messages.clear();

        for (String key : config.getKeys(true)) {
            if (config.isString(key)) {
                messages.put(key, config.getString(key));
            }
        }
        plugin.getLogger().info("Messages chargés : " + messages.size());
    }

    // Envoie un message simple
    public void send(CommandSender sender, String key) {
        sender.sendMessage(get(key));
    }

    // Envoie un message avec des variables (ex: {wave}, {amount})
    // Usage: lang.send(player, "op-no-money", "{amount}", "500");
    public void send(CommandSender sender, String key, String... placeholders) {
        String msg = getRaw(key);
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            msg = msg.replace(placeholders[i], placeholders[i + 1]);
        }
        sender.sendMessage(format(msg));
    }

    // Récupère le composant formaté (avec Prefix)
    public Component get(String key) {
        String prefix = messages.getOrDefault("prefix", "&6[BN] ");
        String msg = messages.getOrDefault(key, "&cMessage introuvable: " + key);
        return format(prefix + msg);
    }

    /**
     * Récupère un message du fichier de langue, applique les couleurs, 
     * mais NE L'ENVOIE PAS (utile pour les GUIs, Items, Titles).
     */
    // --- MÉTHODE 1 : Récupérer avec une valeur par défaut (Celle qui manque) ---
    public String get(org.bukkit.entity.Player p, String key, String defaultValue) {
        // 1. Vérifie que la config est chargée
        // Si ta variable s'appelle 'messages' ou 'yaml', change le mot 'config' ici !
        if (config == null) return defaultValue; 

        // 2. Récupère le message
        String msg = config.getString(key);

        // 3. Si la clé n'existe pas dans le fichier, on renvoie la valeur par défaut
        if (msg == null) {
            return org.bukkit.ChatColor.translateAlternateColorCodes('&', defaultValue);
        }

        // 4. Traduction des couleurs
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', msg);
    }

    // --- MÉTHODE 2 : Récupérer sans valeur par défaut (Surcharge) ---
    public String get(org.bukkit.entity.Player p, String key) {
        // On appelle la méthode du dessus avec un message d'erreur par défaut
        return get(p, key, "&cMissing Key: " + key);
    }
    
    // Change "private" en "public" ici
    public String getRaw(String key) {
        return messages.getOrDefault(key, "&cMessage introuvable: " + key);
    }

    // Convertit les "&a" en couleurs
    private Component format(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }
}