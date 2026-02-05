package com.blocknights;

import org.bukkit.plugin.java.JavaPlugin;

import com.blocknights.game.SessionManager;
import com.blocknights.game.operator.OperatorManager;
import com.blocknights.maps.MapManager;
import com.blocknights.waves.WaveManager;
import com.blocknights.commands.BnCommands;
// Import corrigé pour le Listener
import com.blocknights.editor.WandListener;

public class BlocknightsPlugin extends JavaPlugin {

    private static BlocknightsPlugin instance;

    private SessionManager sessionManager;
    private MapManager mapManager;
    private WaveManager waveManager;
    private OperatorManager operatorManager;
    private com.blocknights.editor.EditorManager editorManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("--- Initialisation de Blocknights V2 (Core) ---");

        this.mapManager = new MapManager(this);
        this.operatorManager = new OperatorManager(this);
        this.waveManager = new WaveManager(this);
        this.editorManager = new com.blocknights.editor.EditorManager(this);
        this.sessionManager = new SessionManager(this);
        
        // Enregistrement avec le bon import
        getServer().getPluginManager().registerEvents(new WandListener(this), this);
        
        getCommand("bn").setExecutor(new BnCommands(this));

        getLogger().info("Blocknights V2 est prêt !");
    }

    @Override
    public void onDisable() {
        if (sessionManager != null) {
            sessionManager.stopGame();
        }
    }

    public static BlocknightsPlugin get() { return instance; }

    public SessionManager getSessionManager() { return sessionManager; }
    public MapManager getMapManager() { return mapManager; }
    public WaveManager getWaveManager() { return waveManager; }
    public OperatorManager getOperatorManager() { return operatorManager; }
    public com.blocknights.editor.EditorManager getEditorManager() { return editorManager; }
}