package com.blocknights;

import com.blocknights.commands.BnCommands;
import com.blocknights.editor.EditorManager;
import com.blocknights.editor.WandListener;
import com.blocknights.game.SessionManager;
import com.blocknights.game.operator.DeploymentListener;
import com.blocknights.game.operator.OperatorManager;
import com.blocknights.gui.GuiManager;
import com.blocknights.maps.MapManager;
import com.blocknights.utils.LangManager;
import com.blocknights.waves.WaveManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.blocknights.game.ScoreboardManager;
import com.blocknights.game.CombatListener;

public class BlocknightsPlugin extends JavaPlugin {

    // --- Utilitaires ---
    private LangManager langManager;
    private GuiManager guiManager;

    // --- Core Data ---
    private MapManager mapManager;
    
    // --- Gameplay ---
    private WaveManager waveManager;
    private SessionManager sessionManager;
    private OperatorManager operatorManager;
    private ScoreboardManager scoreboardManager;

    // --- Editor ---
    private EditorManager editorManager;

    @Override
    public void onEnable() {
        // 1. Charger la Langue en premier (pour que les autres puissent l'utiliser)
        this.langManager = new LangManager(this);

        // 2. Initialiser les Managers de Données & Jeu
        this.mapManager = new MapManager(this);
        this.waveManager = new WaveManager(this);
        this.sessionManager = new SessionManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.operatorManager = new OperatorManager(this);

        // 3. Initialiser l'Interface & Éditeur
        this.guiManager = new GuiManager(this);
        this.editorManager = new EditorManager(this);

        // 4. Enregistrer les Commandes
        if (getCommand("bn") != null) {
            getCommand("bn").setExecutor(new BnCommands(this));
        }

        // 5. Enregistrer les Listeners (Événements)
        // Gestion de la baguette d'édition
        getServer().getPluginManager().registerEvents(new WandListener(this), this);
        // Gestion du déploiement des opérateurs (Clic droit avec item)
        getServer().getPluginManager().registerEvents(new DeploymentListener(this), this);
        getServer().getPluginManager().registerEvents(new com.blocknights.game.operator.InteractionListener(this), this);
        getServer().getPluginManager().registerEvents(new com.blocknights.game.operator.PlacementListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getLogger().info("Blocknights V2 (Architecture Arknights) chargé avec succès !");
        
        new com.blocknights.game.operator.OperatorLoader(this).loadAll();
    }

    @Override
    public void onDisable() {
        // Nettoyage propre
        if (sessionManager != null) {
            sessionManager.stopGame();
        }
        if (mapManager != null) {
            mapManager.saveActiveMap();
        }
        if (editorManager != null) {
            editorManager.shutdown();
        }
        getLogger().info("Blocknights désactivé.");
    }

    // --- GETTERS (Accessibles partout) ---

    public LangManager getLang() { return langManager; }
    public GuiManager getGuiManager() { return guiManager; }
    
    public MapManager getMapManager() { return mapManager; }
    
    public WaveManager getWaveManager() { return waveManager; }
    public SessionManager getSessionManager() { return sessionManager; }
    public OperatorManager getOperatorManager() { return operatorManager; }
    
    public EditorManager getEditorManager() { return editorManager; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
}