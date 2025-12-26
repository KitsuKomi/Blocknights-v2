package com.blocknights;

import org.bukkit.plugin.java.JavaPlugin;

import com.blocknights.editor.MapEditorManager;
import com.blocknights.editor.command.MapEditorCommand;
import com.blocknights.editor.listener.MapEditorListener;

public class BlocknightsPlugin extends JavaPlugin {

    private MapEditorManager mapEditorManager;

    @Override
    public void onEnable() {
        this.mapEditorManager = new MapEditorManager(this);
        registerCommands();
        registerListeners();
        getLogger().info("Blocknights enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Blocknights disabled.");
    }

    private void registerCommands() {
        if (getCommand("mapeditor") != null) {
            getCommand("mapeditor").setExecutor(new MapEditorCommand(mapEditorManager));
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new MapEditorListener(mapEditorManager), this);
    }
}