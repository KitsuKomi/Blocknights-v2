package com.blocknights;

import org.bukkit.plugin.java.JavaPlugin;

public class BlocknightsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Blocknights enabled.");
        // TODO: init managers, editors, storage, etc.
    }

    @Override
    public void onDisable() {
        getLogger().info("Blocknights disabled.");
    }
}