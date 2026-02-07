package com.blocknights.editor;

import com.blocknights.BlocknightsPlugin;
import io.papermc.paper.event.player.AsyncChatEvent; // Ou AsyncPlayerChatEvent si vieux Spigot
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EditorChatListener implements Listener {

    private final BlocknightsPlugin plugin;

    public EditorChatListener(BlocknightsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        if (!plugin.getEditorManager().isRenaming(e.getPlayer())) return;

        e.setCancelled(true); // On ne veut pas que le message soit public

        // Conversion Component -> String (Paper API)
        String message = PlainTextComponentSerializer.plainText().serialize(e.message());
        
        // On repasse sur le Thread Principal pour modifier la Map (Bukkit n'aime pas l'Async)
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getEditorManager().finishRenaming(e.getPlayer(), message);
        });
    }
}