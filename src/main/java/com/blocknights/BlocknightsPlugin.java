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
        this.operatorManager = new OperatorManager(this); // Fixe l'erreur rouge précédente

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

        getLogger().info("Blocknights V2 (Architecture Arknights) chargé avec succès !");
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
}