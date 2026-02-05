# Blocknights — Arborescence & index des fichiers Java

> Généré le 2025-12-23 — 302 fichiers `.java` dans 61 packages.

Ce document sert de **carte** du plugin : structure globale, packages, puis liste des classes avec une description courte.

> Note : quand un fichier n’a pas de JavaDoc clair, la description est **déduite du nom / du type** (Registry, Manager, Listener, …).

## Arborescence du projet (vue rapide)
```text
export/
  ├─ README.md
  ├─ docs
  │  ├─ api-skills-talents.md
  │  ├─ backlog.md
  │  ├─ ci.md
  │  ├─ config.example.yml
  │  ├─ contracts_missions.md
  │  ├─ contributing.md
  │  ├─ end.yml
  │  ├─ extensions_arknights.md
  │  ├─ guide-creation-operateurs.md
  │  ├─ lore
  │  │  └─ prologue.md
  │  ├─ maintenance_calendar.md
  │  ├─ manual-tests.md
  │  ├─ medailles.md
  │  ├─ menus.md
  │  ├─ messages.md
  │  ├─ mode-histoire.md
  │  ├─ modes-classique-endless-timeattack.md
  │  ├─ modes.md
  │  ├─ operators.md
  │  ├─ playtest.md
  │  ├─ preparation_animations_skills.md
  │  ├─ prochaines_etapes.md
  │  ├─ prochaines_etapes_operateurs.md
  │  ├─ prochaines_etapes_quetes_medailles.md
  │  ├─ prochaines_etapes_talents_skills.md
  │  ├─ quetes.md
  │  ├─ skill-effects.txt
  │  ├─ systeme_synergie_talents_skills.md
  │  ├─ tutoriel_creation_complete.md
  │  ├─ validation.md
  │  ├─ waves.md
  │  └─ waves.yml
  ├─ pom.xml
  └─ src
     ├─ main
     │  ├─ java
     │  │  └─ com
     │  │     └─ blocknights
     │  └─ resources
     │     ├─ animations
     │     │  └─ skills.yml
     │     ├─ combat.yml
     │     ├─ config
     │     │  └─ perf.yml
     │     ├─ config.yml
     │     ├─ contracts
     │     │  └─ example.yml
     │     ├─ deck-menu.yml
     │     ├─ enemies
     │     │  ├─ brute.yml
     │     │  ├─ burrower.yml
     │     │  ├─ flying.yml
     │     │  └─ grunt.yml
     │     ├─ history-menu.yml
     │     ├─ levels.yml
     │     ├─ messages.yml
     │     ├─ messages_en.yml
     │     ├─ messages_fr.yml
     │     ├─ operators
     │     │  ├─ archer.yml
     │     │  ├─ guard.yml
     │     │  └─ mage.yml
     │     ├─ placeholders.txt
     │     ├─ plugin.yml
     │     ├─ rarities.yml
     │     ├─ role-affinity.yml
     │     ├─ side_missions.yml
     │     ├─ skill-gui.yml
     │     ├─ skills
     │     │  ├─ arcane_focus.yml
     │     │  ├─ burning_shot.yml
     │     │  ├─ iron_will.yml
     │     │  ├─ multishot.yml
     │     │  ├─ overload.yml
     │     │  ├─ precision.yml
     │     │  └─ rapid_strike.yml
     │     ├─ stat-tuning.yml
     │     ├─ story
     │     │  └─ prologue.yml
     │     ├─ talents
     │     │  ├─ eagle-eye.yml
     │     │  └─ synergies.yml
     │     ├─ version.properties
     │     └─ waves
     │        └─ example.yml
     └─ test
        ├─ java
        │  └─ com
        │     └─ blocknights
        └─ resources
           └─ mockito-extensions
              └─ org.mockito.plugins.MockMaker
```

## Arborescence des packages Java
```text
src/main/java/
  └─ com
     └─ blocknights
        ├─ api
        │  ├─ events
        │  ├─ skills
        │  └─ spec
        ├─ battle
        │  └─ effects
        ├─ combat
        ├─ commands
        │  ├─ medals
        │  ├─ operators
        │  └─ quests
        │     └─ menu
        ├─ editor
        │  ├─ deploy
        │  ├─ model
        │  ├─ ops
        │  ├─ ui
        │  │  └─ shared
        │  └─ validate
        ├─ enemy
        ├─ game
        │  ├─ contracts
        │  ├─ effects
        │  ├─ enemy
        │  ├─ medals
        │  │  ├─ audit
        │  │  └─ reset
        │  ├─ operator
        │  ├─ progress
        │  ├─ quests
        │  │  ├─ audit
        │  │  ├─ events
        │  │  ├─ reload
        │  │  ├─ reset
        │  │  └─ storage
        │  ├─ skills
        │  │  ├─ animation
        │  │  └─ gui
        │  ├─ story
        │  │  └─ timeline
        │  └─ ui
        │     └─ menu
        ├─ i18n
        ├─ integration
        ├─ maps
        ├─ operators
        │  ├─ model
        │  ├─ runtime
        │  ├─ ui
        │  └─ visual
        ├─ papi
        ├─ pathfinding
        ├─ playtest
        ├─ stats
        ├─ telemetry
        │  ├─ medals
        │  └─ quests
        ├─ tools
        ├─ util
        ├─ validation
        └─ waves
           └─ editor
```

## Index détaillé des fichiers Java (par package)

### `com.blocknights` (3)
- `BlocknightsPlugin.java` — Class: blocknights plugin.  
  _src/main/java/com/blocknights/BlocknightsPlugin.java_
- `EditorCommand.java` — Commande dédiée à l'ouverture des outils d'édition.  
  _src/main/java/com/blocknights/EditorCommand.java_
- `WavesCommand.java` — Commande utilitaire pour lancer ou arrêter des vagues.  
  _src/main/java/com/blocknights/WavesCommand.java_

### `com.blocknights.api` (4)
- `EnemyTypeRegistry.java` — Registry for enemy type.  
  _src/main/java/com/blocknights/api/EnemyTypeRegistry.java_
- `OperatorType.java` — Class: operator type.  
  _src/main/java/com/blocknights/api/OperatorType.java_
- `OperatorTypeRegistry.java` — Registry for operator type.  
  _src/main/java/com/blocknights/api/OperatorTypeRegistry.java_
- `OperatorYaml.java` — YAML model/loader for operator.  
  _src/main/java/com/blocknights/api/OperatorYaml.java_

### `com.blocknights.api.events` (2)
- `OperatorHitEvent.java` — Custom event for operator hit.  
  _src/main/java/com/blocknights/api/events/OperatorHitEvent.java_
- `OperatorShotEvent.java` — Custom event for operator shot.  
  _src/main/java/com/blocknights/api/events/OperatorShotEvent.java_

### `com.blocknights.api.skills` (8)
- `OperatorSkillTypeRegistry.java` — Registry for linking skill type identifiers to their {  
  _src/main/java/com/blocknights/api/skills/OperatorSkillTypeRegistry.java_
- `OperatorSkillTypeRegistryTest.java` — Class: operator skill type registry test.  
  _export/src/test/java/com/blocknights/api/skills/OperatorSkillTypeRegistryTest.java_
- `Skill.java` — Interface: skill.  
  _src/main/java/com/blocknights/api/skills/Skill.java_
- `SkillExecutor.java` — Interface: skill executor.  
  _src/main/java/com/blocknights/api/skills/SkillExecutor.java_
- `SkillRegistry.java` — Registry for skill.  
  _src/main/java/com/blocknights/api/skills/SkillRegistry.java_
- `Talent.java` — Interface: talent.  
  _src/main/java/com/blocknights/api/skills/Talent.java_
- `TalentHook.java` — Interface: talent hook.  
  _src/main/java/com/blocknights/api/skills/TalentHook.java_
- `TalentHookRegistry.java` — Registry for talent hook.  
  _src/main/java/com/blocknights/api/skills/TalentHookRegistry.java_

### `com.blocknights.api.spec` (3)
- `OperatorLevelDef.java` — Class: operator level def.  
  _src/main/java/com/blocknights/api/spec/OperatorLevelDef.java_
- `OperatorSkillSpec.java` — Class: operator skill spec.  
  _src/main/java/com/blocknights/api/spec/OperatorSkillSpec.java_
- `OperatorTalentSpec.java` — Class: operator talent spec.  
  _src/main/java/com/blocknights/api/spec/OperatorTalentSpec.java_

### `com.blocknights.battle.effects` (3)
- `StatModifier.java` — Represents a temporary modification to an operator's statistics.  
  _src/main/java/com/blocknights/battle/effects/StatModifier.java_
- `TickingModifier.java` — Extension de {  
  _src/main/java/com/blocknights/battle/effects/TickingModifier.java_
- `TimedModifier.java` — Wrapper associating a {  
  _src/main/java/com/blocknights/battle/effects/TimedModifier.java_

### `com.blocknights.combat` (6)
- `CombatConfig.java` — Représente la configuration des priorités de ciblage côté opérateurs.  
  _src/main/java/com/blocknights/combat/CombatConfig.java_
- `CombatConfigTest.java` — Class: combat config test.  
  _export/src/test/java/com/blocknights/combat/CombatConfigTest.java_
- `DamageType.java` — Types de dégâts de base utilisés par les opérateurs et les ennemis.  
  _src/main/java/com/blocknights/combat/DamageType.java_
- `OperatorDamageCalculator.java` — Utilitaire centralisant les calculs de dégâts opérateur.  
  _src/main/java/com/blocknights/combat/OperatorDamageCalculator.java_
- `RoleAffinity.java` — Reads role affinities from role-affinity.yml and provides damage multipliers between attacker and defender roles.  
  _src/main/java/com/blocknights/combat/RoleAffinity.java_
- `TargetPriority.java` — Calcule la priorité d'une cible en s'appuyant sur {  
  _src/main/java/com/blocknights/combat/TargetPriority.java_

### `com.blocknights.commands` (2)
- `BnCommands.java` — Bukkit command executor.  
  _src/main/java/com/blocknights/commands/BnCommands.java_
- `ReturnPointListener.java` — Bukkit event listener for return point.  
  _src/main/java/com/blocknights/commands/ReturnPointListener.java_

### `com.blocknights.commands.medals` (1)
- `MedalCommands.java` — Staff commands for medal management.  
  _src/main/java/com/blocknights/commands/medals/MedalCommands.java_

### `com.blocknights.commands.operators` (1)
- `OperatorCommands.java` — Sub-commands for "/bn op" related to operator progression.  
  _src/main/java/com/blocknights/commands/operators/OperatorCommands.java_

### `com.blocknights.commands.quests.menu` (1)
- `QuestMenuGenerator.java` — Copie les fichiers DeluxeMenus de ressources vers le dossier de données.  
  _src/main/java/com/blocknights/commands/quests/menu/QuestMenuGenerator.java_

### `com.blocknights.editor` (13)
- `AutoPathService.java` — Service de calcul des chemins (2D, à hauteur fixe).  
  _src/main/java/com/blocknights/editor/AutoPathService.java_
- `EditorKeys.java` — Clés centralisées pour PDC/scoreboards/etc.  
  _src/main/java/com/blocknights/editor/EditorKeys.java_
- `EditorManager.java` — EditorManager — version consolidée - API attendue par BlocknightsPlugin: saveMap(Player,String,boolean), cancelEditor(Player), saveCurrent(Player), shutdown() - Relais d'inventaire: * Toolbox: détection par identité d'inventaire, annule toutes interactions * Map Editor/Wave Designer (hérité): verrou par titre pour éviter le vol d'items  
  _src/main/java/com/blocknights/editor/EditorManager.java_
- `EditorScratch.java` — Class: editor scratch.  
  _src/main/java/com/blocknights/editor/EditorScratch.java_
- `EditorSession.java` — EditorSession – compat avec l'existant : - constructeur (Player, String, BnMap) - registre statique: register/by/unregister - champ returnLocation utilisé par SelectionVisualizer  
  _src/main/java/com/blocknights/editor/EditorSession.java_
- `EditorSessionSync.java` — Maintient la cohérence entre EditorScratch, EditorSession et BnMap.  
  _src/main/java/com/blocknights/editor/EditorSessionSync.java_
- `EditorTool.java` — Enum: editor tool.  
  _src/main/java/com/blocknights/editor/EditorTool.java_
- `EditorToolboxGui.java` — Toolbox GUI — version classique revisitée.  
  _src/main/java/com/blocknights/editor/EditorToolboxGui.java_
- `MapConfigAccessor.java` — Accès en lecture/écriture au fichier maps/<map>/config.yml pour exposer les paramètres de gameplay directement depuis l'éditeur.  
  _src/main/java/com/blocknights/editor/MapConfigAccessor.java_
- `PathIO.java` — Class: path io.  
  _src/main/java/com/blocknights/editor/PathIO.java_
- `PathSmoother.java` — Class: path smoother.  
  _src/main/java/com/blocknights/editor/PathSmoother.java_
- `TimelineEditor.java` — Class: timeline editor.  
  _src/main/java/com/blocknights/editor/TimelineEditor.java_
- `TimelineHolder.java` — Holder dédié pour reconnaître l’inventaire de la timeline et attacher la session.  
  _src/main/java/com/blocknights/editor/TimelineHolder.java_

### `com.blocknights.editor.deploy` (5)
- `DeployBrush.java` — Outil côté joueur : choix du brush + peinture/efface “là où on regarde”.  
  _src/main/java/com/blocknights/editor/deploy/DeployBrush.java_
- `DeployGrid.java` — Grille de déploiement en tuiles fixes 3x3 (ancrées à la bbox).  
  _src/main/java/com/blocknights/editor/deploy/DeployGrid.java_
- `DeployIO.java` — I/O compact et robuste pour deploy.yml (tuiles 3×3 alignées).  
  _src/main/java/com/blocknights/editor/deploy/DeployIO.java_
- `DeployPaintListener.java` — Bukkit event listener for deploy paint.  
  _src/main/java/com/blocknights/editor/deploy/DeployPaintListener.java_
- `DeployType.java` — Enum: deploy type.  
  _src/main/java/com/blocknights/editor/deploy/DeployType.java_

### `com.blocknights.editor.model` (1)
- `PathModel.java` — Chemin discret en coordonnées bloc (x,y,z) + ySurf (hauteur “marche”).  
  _src/main/java/com/blocknights/editor/model/PathModel.java_

### `com.blocknights.editor.ops` (6)
- `OperatorInstanceManager.java` — Manager for operator instance.  
  _src/main/java/com/blocknights/editor/ops/OperatorInstanceManager.java_
- `SlotDef.java` — Class: slot def.  
  _src/main/java/com/blocknights/editor/ops/SlotDef.java_
- `SlotMarkerService.java` — Service for slot marker.  
  _src/main/java/com/blocknights/editor/ops/SlotMarkerService.java_
- `SlotRuntime.java` — Class: slot runtime.  
  _src/main/java/com/blocknights/editor/ops/SlotRuntime.java_
- `SlotsIO.java` — Class: slots io.  
  _src/main/java/com/blocknights/editor/ops/SlotsIO.java_
- `SlotUiListener.java` — Bukkit event listener for slot ui.  
  _src/main/java/com/blocknights/editor/ops/SlotUiListener.java_

### `com.blocknights.editor.ui` (9)
- `EditorItems.java` — Class: editor items.  
  _src/main/java/com/blocknights/editor/ui/EditorItems.java_
- `EditorToolboxHolder.java` — Class: editor toolbox holder.  
  _src/main/java/com/blocknights/editor/ui/EditorToolboxHolder.java_
- `HoloUtil.java` — Utility helpers for holo.  
  _src/main/java/com/blocknights/editor/ui/HoloUtil.java_
- `Icon.java` — Class: icon.  
  _src/main/java/com/blocknights/editor/ui/Icon.java_
- `MapEditorHolder.java` — Class: map editor holder.  
  _src/main/java/com/blocknights/editor/ui/MapEditorHolder.java_
- `PathPreviewer.java` — PathPreviewer – affiche un chemin en WP#n + une ligne de particules entre les points.  
  _src/main/java/com/blocknights/editor/ui/PathPreviewer.java_
- `SelectionVisualizer.java` — SelectionVisualizer v3 (clair) - Scheduler global (toutes les 3 ticks) → affichage stable.  
  _src/main/java/com/blocknights/editor/ui/SelectionVisualizer.java_
- `UiPreferences.java` — Simple per-player preferences stored in the persistent data container.  
  _src/main/java/com/blocknights/editor/ui/UiPreferences.java_
- `WandListener.java` — WandListener — BBox uniquement (pos1/pos2, spawn/goal, autopath).  
  _src/main/java/com/blocknights/editor/ui/WandListener.java_

### `com.blocknights.editor.ui.shared` (2)
- `ModeConfigStrip.java` — Barre inférieure du Map Editor dédiée aux paramètres de gameplay.  
  _src/main/java/com/blocknights/editor/ui/shared/ModeConfigStrip.java_
- `ModeScoreComparisonGui.java` — Affiche un comparatif visuel entre les scores Endless et Time Attack pour une carte donnée.  
  _src/main/java/com/blocknights/editor/ui/shared/ModeScoreComparisonGui.java_

### `com.blocknights.editor.validate` (1)
- `EditorValidator.java` — Validation en direct pour la commande /bn validate depuis l’éditeur.  
  _src/main/java/com/blocknights/editor/validate/EditorValidator.java_

### `com.blocknights.enemy` (7)
- `EnemyManager.java` — Manager for enemy.  
  _src/main/java/com/blocknights/enemy/EnemyManager.java_
- `EnemyRunner.java` — Class: enemy runner.  
  _src/main/java/com/blocknights/enemy/EnemyRunner.java_
- `EnemySafetyListener.java` — Protège les ennemis Blocknights des interactions inattendues côté joueur.  
  _src/main/java/com/blocknights/enemy/EnemySafetyListener.java_
- `EnemyStats.java` — Stocke et récupère les infos des ennemis via PersistentDataContainer.  
  _src/main/java/com/blocknights/enemy/EnemyStats.java_
- `EnemyStatsTest.java` — Class: enemy stats test.  
  _export/src/test/java/com/blocknights/enemy/EnemyStatsTest.java_
- `WaveDef.java` — Définition d'une vague d'ennemis.  
  _src/main/java/com/blocknights/enemy/WaveDef.java_
- `WaveIO.java` — Chargement et sauvegarde des définitions de vagues.  
  _src/main/java/com/blocknights/enemy/WaveIO.java_

### `com.blocknights.game` (14)
- `DeckGui.java` — GUI screen for deck.  
  _src/main/java/com/blocknights/game/DeckGui.java_
- `DeployerItem.java` — Class: deployer item.  
  _src/main/java/com/blocknights/game/DeployerItem.java_
- `EndlessSession.java` — Simple orchestrator for the endless wave mode.  
  _src/main/java/com/blocknights/game/EndlessSession.java_
- `GameProgressListener.java` — Listener unique relayant les événements de progression aux gestionnaires enregistrés.  
  _src/main/java/com/blocknights/game/GameProgressListener.java_
- `OperatorFieldRegistry.java` — Registre léger des opérateurs placés sur le terrain pour permettre aux autres composants (vagues, IA ennemie…) de consulter les instances actuellement actives.  
  _src/main/java/com/blocknights/game/OperatorFieldRegistry.java_
- `OperatorLevelServiceIntegrationTest.java` — Class: operator level service integration test.  
  _export/src/test/java/com/blocknights/game/OperatorLevelServiceIntegrationTest.java_
- `OperatorSynergyManager.java` — Manages operator synergy bonuses defined in {  
  _src/main/java/com/blocknights/game/OperatorSynergyManager.java_
- `OpIntrospect.java` — Class: op introspect.  
  _src/main/java/com/blocknights/game/OpIntrospect.java_
- `PlacementListener.java` — Bukkit event listener for placement.  
  _src/main/java/com/blocknights/game/PlacementListener.java_
- `ProgressListener.java` — Interface générique pour tout gestionnaire recevant des événements de progression.  
  _src/main/java/com/blocknights/game/ProgressListener.java_
- `SessionManager.java` — Manager for session.  
  _src/main/java/com/blocknights/game/SessionManager.java_
- `StoryEngine.java` — Simple loader for story chapters defined under story/*.yml.  
  _src/main/java/com/blocknights/game/StoryEngine.java_
- `TimeAttackSession.java` — Session dédiée au mode Time Attack.  
  _src/main/java/com/blocknights/game/TimeAttackSession.java_
- `TowerDefenseRunTest.java` — Class: tower defense run test.  
  _export/src/test/java/com/blocknights/game/TowerDefenseRunTest.java_

### `com.blocknights.game.contracts` (6)
- `ContractDefinition.java` — Description statique d'un contrat (modificateur de run).  
  _src/main/java/com/blocknights/game/contracts/ContractDefinition.java_
- `ContractLoader.java` — Charge les contrats depuis {  
  _src/main/java/com/blocknights/game/contracts/ContractLoader.java_
- `ContractSelection.java` — Sélection courante de contrats pour un run donné.  
  _src/main/java/com/blocknights/game/contracts/ContractSelection.java_
- `ContractSelectionApplier.java` — Applique les effets d'une sélection de contrats à {  
  _src/main/java/com/blocknights/game/contracts/ContractSelectionApplier.java_
- `SideMissionDefinition.java` — Mission secondaire optionnelle pour un run.  
  _src/main/java/com/blocknights/game/contracts/SideMissionDefinition.java_
- `SideMissionLoader.java` — Charge les missions secondaires depuis {  
  _src/main/java/com/blocknights/game/contracts/SideMissionLoader.java_

### `com.blocknights.game.effects` (4)
- `BuffEngine.java` — Gestion centralisée des buffs collectifs (auras, bannières, champs tactiques).  
  _src/main/java/com/blocknights/game/effects/BuffEngine.java_
- `EffectEngine.java` — Class: effect engine.  
  _src/main/java/com/blocknights/game/effects/EffectEngine.java_
- `GlobalAreaEffectRegistry.java` — Registre global des effets de zone applicables aux opérateurs et ennemis.  
  _src/main/java/com/blocknights/game/effects/GlobalAreaEffectRegistry.java_
- `TrapEngine.java` — Class: trap engine.  
  _src/main/java/com/blocknights/game/effects/TrapEngine.java_

### `com.blocknights.game.enemy` (2)
- `Enemy.java` — Class: enemy.  
  _src/main/java/com/blocknights/game/enemy/Enemy.java_
- `Status.java` — Class: status.  
  _src/main/java/com/blocknights/game/enemy/Status.java_

### `com.blocknights.game.medals` (10)
- `Medal.java` — Définition d'une médaille.  
  _src/main/java/com/blocknights/game/medals/Medal.java_
- `MedalAttributes.java` — Métadonnées étendues d'une médaille ou d'un palier.  
  _src/main/java/com/blocknights/game/medals/MedalAttributes.java_
- `MedalCondition.java` — Condition individuelle nécessaire à l'obtention d'une médaille.  
  _src/main/java/com/blocknights/game/medals/MedalCondition.java_
- `MedalConditionComparator.java` — Comparateur permettant de vérifier une condition.  
  _src/main/java/com/blocknights/game/medals/MedalConditionComparator.java_
- `MedalListener.java` — Écouteur répercutant les événements de jeu sur les médailles.  
  _src/main/java/com/blocknights/game/medals/MedalListener.java_
- `MedalManager.java` — Charge les médailles et suit la progression des joueurs.  
  _src/main/java/com/blocknights/game/medals/MedalManager.java_
- `MedalPlaceholders.java` — Prépare des placeholders simples pour l'affichage des médailles.  
  _src/main/java/com/blocknights/game/medals/MedalPlaceholders.java_
- `MedalProgress.java` — Suivi global de la progression des médailles pour un joueur.  
  _src/main/java/com/blocknights/game/medals/MedalProgress.java_
- `MedalService.java` — Surcouche qui permet de publier des progrès de médailles hors-ligne et de relier les objectifs partagés.  
  _src/main/java/com/blocknights/game/medals/MedalService.java_
- `MedalTier.java` — Palier progressif d'une médaille (bronze, argent, or...).  
  _src/main/java/com/blocknights/game/medals/MedalTier.java_

### `com.blocknights.game.medals.audit` (1)
- `MedalAuditReport.java` — Export détaillé des médailles.  
  _src/main/java/com/blocknights/game/medals/audit/MedalAuditReport.java_

### `com.blocknights.game.medals.reset` (2)
- `MedalResetOptions.java` — Options de reset pour le service de médailles.  
  _src/main/java/com/blocknights/game/medals/reset/MedalResetOptions.java_
- `MedalResetReport.java` — Résultat d'un reset de médailles.  
  _src/main/java/com/blocknights/game/medals/reset/MedalResetReport.java_

### `com.blocknights.game.operator` (7)
- `Operator.java` — Operator: version propre et compacte.  
  _src/main/java/com/blocknights/game/operator/Operator.java_
- `OperatorDeckStorage.java` — Persistance simple des decks d'opérateurs sélectionnés par les joueurs.  
  _src/main/java/com/blocknights/game/operator/OperatorDeckStorage.java_
- `OperatorManager.java` — Manager for operator.  
  _src/main/java/com/blocknights/game/operator/OperatorManager.java_
- `OperatorMenuListener.java` — Surveille la fermeture des menus DeluxeMenus ouverts par {  
  _src/main/java/com/blocknights/game/operator/OperatorMenuListener.java_
- `OperatorRangeFalloffTest.java` — Class: operator range falloff test.  
  _export/src/test/java/com/blocknights/game/operator/OperatorRangeFalloffTest.java_
- `SkillGui.java` — Utilitaires d'interface pour les compétences d'un opérateur.  
  _src/main/java/com/blocknights/game/operator/SkillGui.java_
- `UpgradeGui.java` — Simple GUI allowing players to upgrade placed operators.  
  _src/main/java/com/blocknights/game/operator/UpgradeGui.java_

### `com.blocknights.game.progress` (1)
- `ProgressType.java` — Types d'événements de progression communs aux systèmes de médailles, succès et quêtes.  
  _src/main/java/com/blocknights/game/progress/ProgressType.java_

### `com.blocknights.game.quests` (10)
- `ProgressUtil.java` — Méthodes utilitaires pour formater les messages de progression.  
  _src/main/java/com/blocknights/game/quests/ProgressUtil.java_
- `Quest.java` — Définition d'une quête.  
  _src/main/java/com/blocknights/game/quests/Quest.java_
- `QuestCondition.java` — Condition individuelle composant une quête.  
  _src/main/java/com/blocknights/game/quests/QuestCondition.java_
- `QuestListener.java` — Écouteur principal mettant à jour les quêtes.  
  _src/main/java/com/blocknights/game/quests/QuestListener.java_
- `QuestManager.java` — Loads quest definitions from quests.yml and exposes them at runtime.  
  _src/main/java/com/blocknights/game/quests/QuestManager.java_
- `QuestPeriodicity.java` — Fréquence de réinitialisation d'une quête.  
  _src/main/java/com/blocknights/game/quests/QuestPeriodicity.java_
- `QuestReward.java` — Représente une commande exécutée lors de la complétion d'une quête.  
  _src/main/java/com/blocknights/game/quests/QuestReward.java_
- `QuestService.java` — Service de haut niveau ajoutant une file d'attente offline et une API de publication.  
  _src/main/java/com/blocknights/game/quests/QuestService.java_
- `QuestState.java` — Progrès d'une quête pour un joueur.  
  _src/main/java/com/blocknights/game/quests/QuestState.java_
- `QuestType.java` — Catégories de quêtes contrôlant leur durée de vie.  
  _src/main/java/com/blocknights/game/quests/QuestType.java_

### `com.blocknights.game.quests.audit` (1)
- `QuestAuditReport.java` — Export d'audit pour les quêtes.  
  _src/main/java/com/blocknights/game/quests/audit/QuestAuditReport.java_

### `com.blocknights.game.quests.events` (11)
- `BaseHpChangeEvent.java` — Variation des points de vie de la base.  
  _src/main/java/com/blocknights/game/quests/events/BaseHpChangeEvent.java_
- `OperatorDeployEvent.java` — Pose d'un opérateur sur le terrain.  
  _src/main/java/com/blocknights/game/quests/events/OperatorDeployEvent.java_
- `OperatorUpgradeEvent.java` — Amélioration d'un opérateur existant.  
  _src/main/java/com/blocknights/game/quests/events/OperatorUpgradeEvent.java_
- `ResourceCollectEvent.java` — Collecte d'une ressource spécifique.  
  _src/main/java/com/blocknights/game/quests/events/ResourceCollectEvent.java_
- `ResourceSpendEvent.java` — Dépense d'une ressource spécifique.  
  _src/main/java/com/blocknights/game/quests/events/ResourceSpendEvent.java_
- `SessionEndEvent.java` — Événement déclenché à la fin d'une session Tower Defense.  
  _src/main/java/com/blocknights/game/quests/events/SessionEndEvent.java_
- `SkillUseContext.java` — Contexte détaillé lors du déclenchement d'une compétence opérateur.  
  _src/main/java/com/blocknights/game/quests/events/SkillUseContext.java_
- `SkillUseEvent.java` — Utilisation d'une compétence spéciale.  
  _src/main/java/com/blocknights/game/quests/events/SkillUseEvent.java_
- `SurviveTimeEvent.java` — Survie pendant un certain temps.  
  _src/main/java/com/blocknights/game/quests/events/SurviveTimeEvent.java_
- `TrapPlacedEvent.java` — Placement d'un piège par un joueur.  
  _src/main/java/com/blocknights/game/quests/events/TrapPlacedEvent.java_
- `WaveCompleteEvent.java` — Événement déclenché lorsqu'une vague est terminée.  
  _src/main/java/com/blocknights/game/quests/events/WaveCompleteEvent.java_

### `com.blocknights.game.quests.reload` (1)
- `QuestReloadReport.java` — Résumé d'un rechargement de quêtes.  
  _src/main/java/com/blocknights/game/quests/reload/QuestReloadReport.java_

### `com.blocknights.game.quests.reset` (2)
- `QuestResetOptions.java` — Options de réinitialisation de quêtes.  
  _src/main/java/com/blocknights/game/quests/reset/QuestResetOptions.java_
- `QuestResetReport.java` — Résultat d'une opération de reset de quêtes.  
  _src/main/java/com/blocknights/game/quests/reset/QuestResetReport.java_

### `com.blocknights.game.quests.storage` (2)
- `QuestProgressRecord.java` — Données persistées pour une quête spécifique (palier + progression brute).  
  _src/main/java/com/blocknights/game/quests/storage/QuestProgressRecord.java_
- `QuestProgressRepository.java` — Gestion centralisée de la persistance des progrès de quêtes.  
  _src/main/java/com/blocknights/game/quests/storage/QuestProgressRepository.java_

### `com.blocknights.game.skills` (24)
- `SkillDef.java` — Definition of a custom skill or talent loaded from configuration.  
  _src/main/java/com/blocknights/game/skills/SkillDef.java_
- `SkillEffect.java` — Specification of a single skill effect parsed from YAML.  
  _src/main/java/com/blocknights/game/skills/SkillEffect.java_
- `SkillEffectApplier.java` — Interface: skill effect applier.  
  _src/main/java/com/blocknights/game/skills/SkillEffectApplier.java_
- `SkillEffectMulTest.java` — Class: skill effect mul test.  
  _export/src/test/java/com/blocknights/game/skills/SkillEffectMulTest.java_
- `SkillEffectType.java` — Enumerates supported effect behaviors.  
  _src/main/java/com/blocknights/game/skills/SkillEffectType.java_
- `SkillLibrary.java` — Loads custom skill and talent definitions from YAML files.  
  _src/main/java/com/blocknights/game/skills/SkillLibrary.java_
- `SkillLibraryTest.java` — Class: skill library test.  
  _export/src/test/java/com/blocknights/game/skills/SkillLibraryTest.java_
- `SkillModifier.java` — Simple value object representing stat modifications contributed by a skill or talent.  
  _src/main/java/com/blocknights/game/skills/SkillModifier.java_
- `SkillRuntime.java` — Minimal runtime responsible for activating skills on {  
  _src/main/java/com/blocknights/game/skills/SkillRuntime.java_
- `SkillsBuiltin.java` — Class: skills builtin.  
  _src/main/java/com/blocknights/game/skills/SkillsBuiltin.java_
- `SkillSpec.java` — Class: skill spec.  
  _src/main/java/com/blocknights/game/skills/SkillSpec.java_
- `SkillStat.java` — Enumerates all supported stat keys for skill effects.  
  _src/main/java/com/blocknights/game/skills/SkillStat.java_
- `SkillYamlNoUnknownStatsTest.java` — Class: skill yaml no unknown stats test.  
  _export/src/test/java/com/blocknights/game/skills/SkillYamlNoUnknownStatsTest.java_
- `SkillYamlParser.java` — Utility for loading skill specifications from YAML files.  
  _src/main/java/com/blocknights/game/skills/SkillYamlParser.java_
- `SkillYamlParserTest.java` — Class: skill yaml parser test.  
  _export/src/test/java/com/blocknights/game/skills/SkillYamlParserTest.java_
- `TalentAuditService.java` — Produit un audit JSON/Markdown synthétisant un talent pour les revues staff.  
  _src/main/java/com/blocknights/game/skills/TalentAuditService.java_
- `TalentDraftBuilder.java` — Génère un brouillon de talent prêt à être édité et linté.  
  _src/main/java/com/blocknights/game/skills/TalentDraftBuilder.java_
- `TalentDraftBuilderTest.java` — Class: talent draft builder test.  
  _export/src/test/java/com/blocknights/game/skills/TalentDraftBuilderTest.java_
- `TalentFormatting.java` — Class: talent formatting.  
  _src/main/java/com/blocknights/game/skills/TalentFormatting.java_
- `TalentLintService.java` — Vérifie rapidement un brouillon de talent afin de signaler les champs manquants ou incohérents.  
  _src/main/java/com/blocknights/game/skills/TalentLintService.java_
- `TalentLintServiceTest.java` — Class: talent lint service test.  
  _export/src/test/java/com/blocknights/game/skills/TalentLintServiceTest.java_
- `TalentPointListener.java` — Awards talent points to players when killing enemies based on the value stored on the mob.  
  _src/main/java/com/blocknights/game/skills/TalentPointListener.java_
- `TalentTree.java` — Simple talent tree system backed by YAML definitions and per-player persistence.  
  _src/main/java/com/blocknights/game/skills/TalentTree.java_
- `TalentTreeEffectsTest.java` — Class: talent tree effects test.  
  _export/src/test/java/com/blocknights/game/skills/TalentTreeEffectsTest.java_

### `com.blocknights.game.skills.animation` (17)
- `SkillAnimationAction.java` — Une action élémentaire de la timeline (particule, son...).  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationAction.java_
- `SkillAnimationActionType.java` — Types d'actions supportées dans une séquence d'animation.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationActionType.java_
- `SkillAnimationBudget.java` — Budget de performances par mode de jeu.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationBudget.java_
- `SkillAnimationCondition.java` — Conditions pour activer une animation.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationCondition.java_
- `SkillAnimationContext.java` — Contexte d'exécution d'une animation.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationContext.java_
- `SkillAnimationDefinition.java` — Définition complète d'une animation.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationDefinition.java_
- `SkillAnimationEngine.java` — Exécuteur par défaut capable de rejouer une timeline d'animation à base de particules, sons et effets visuels variés.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationEngine.java_
- `SkillAnimationEvent.java` — Événement déclenché avant la lecture d'une animation.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationEvent.java_
- `SkillAnimationExecutor.java` — Interface: skill animation executor.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationExecutor.java_
- `SkillAnimationHookListener.java` — Relie les événements d'utilisation de compétences au service d'animations.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationHookListener.java_
- `SkillAnimationLintResult.java` — Représente le résultat d'une vérification de bibliothèque.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationLintResult.java_
- `SkillAnimationPlayEvent.java` — Événement émis après le démarrage d'une animation.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationPlayEvent.java_
- `SkillAnimationSequence.java` — Timeline ordonnée d'actions.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationSequence.java_
- `SkillAnimationService.java` — Service centralisé chargé de charger, valider et jouer les animations de skills.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationService.java_
- `SkillAnimationTelemetry.java` — Flux JSONL pour tracer l'utilisation des animations.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationTelemetry.java_
- `SkillAnimationTimeline.java` — Résultat prêt à l'emploi pour jouer une animation.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationTimeline.java_
- `SkillAnimationTrigger.java` — Types de déclencheurs qu'une animation de skill peut écouter.  
  _src/main/java/com/blocknights/game/skills/animation/SkillAnimationTrigger.java_

### `com.blocknights.game.skills.gui` (5)
- `SkillEffectPreviewService.java` — Génère des aperçus textuels pour les niveaux d'un talent afin d'alimenter les lores des menus (inventaire vanilla ou DeluxeMenus).  
  _src/main/java/com/blocknights/game/skills/gui/SkillEffectPreviewService.java_
- `TalentComparisonMenu.java` — Inventaire comparatif pour visualiser les écarts de talents entre deux joueurs.  
  _src/main/java/com/blocknights/game/skills/gui/TalentComparisonMenu.java_
- `TalentGui.java` — Gestion du menu talents (fallback inventaire + pont DeluxeMenus).  
  _src/main/java/com/blocknights/game/skills/gui/TalentGui.java_
- `TalentMenuGenerator.java` — Synchronise le menu talents entre les ressources et DeluxeMenus.  
  _src/main/java/com/blocknights/game/skills/gui/TalentMenuGenerator.java_
- `TalentResourceWidget.java` — Compose les éléments d'interface relatifs aux ressources talents : points restants et délai de réinitialisation.  
  _src/main/java/com/blocknights/game/skills/gui/TalentResourceWidget.java_

### `com.blocknights.game.story` (8)
- `CutsceneManager.java` — Gère les cutscenes avancées : transitions caméra, effets et commandes.  
  _src/main/java/com/blocknights/game/story/CutsceneManager.java_
- `StoryChapter.java` — Conteneur immutable pour les étapes d'un chapitre.  
  _src/main/java/com/blocknights/game/story/StoryChapter.java_
- `StoryChapterCompleteEvent.java` — Événement déclenché lorsqu'un chapitre est terminé par un joueur.  
  _src/main/java/com/blocknights/game/story/StoryChapterCompleteEvent.java_
- `StoryChapterParser.java` — Conversion entre YAML et représentation objet des chapitres.  
  _src/main/java/com/blocknights/game/story/StoryChapterParser.java_
- `StoryManager.java` — Service principal orchestrant les sessions d'histoire.  
  _src/main/java/com/blocknights/game/story/StoryManager.java_
- `StorySession.java` — Session de dialogue pour un joueur et un chapitre donné.  
  _src/main/java/com/blocknights/game/story/StorySession.java_
- `StoryStep.java` — Représente une étape de narration (dialogue + effets associés).  
  _src/main/java/com/blocknights/game/story/StoryStep.java_
- `StoryTimelineEditor.java` — Éditeur textuel pour configurer la timeline des cutscenes.  
  _src/main/java/com/blocknights/game/story/StoryTimelineEditor.java_

### `com.blocknights.game.story.timeline` (3)
- `StoryCameraCue.java` — Décrit une transition caméra à appliquer pendant une étape.  
  _src/main/java/com/blocknights/game/story/timeline/StoryCameraCue.java_
- `StoryCommandCue.java` — Commande à exécuter pendant la cutscene.  
  _src/main/java/com/blocknights/game/story/timeline/StoryCommandCue.java_
- `StoryParticleCue.java` — Spécifie un effet de particules déclenché durant une étape.  
  _src/main/java/com/blocknights/game/story/timeline/StoryParticleCue.java_

### `com.blocknights.game.ui` (1)
- `DpHud.java` — HUD configurable via config.yml using scoreboard and bossbar.  
  _src/main/java/com/blocknights/game/ui/DpHud.java_

### `com.blocknights.game.ui.menu` (8)
- `ConfigurableMenu.java` — Menu inventaire piloté par YML.  
  _src/main/java/com/blocknights/game/ui/menu/ConfigurableMenu.java_
- `ConfigurableMenuRegistry.java` — Cache/chargeur pour les menus YML (dossier {  
  _src/main/java/com/blocknights/game/ui/menu/ConfigurableMenuRegistry.java_
- `DeckOverviewMenu.java` — Menu deck + contexte carte/contrats/missions.  
  _src/main/java/com/blocknights/game/ui/menu/DeckOverviewMenu.java_
- `MenuItemTemplate.java` — Gabarit d'item pour les menus configurables (nom, lore, tête custom).  
  _src/main/java/com/blocknights/game/ui/menu/MenuItemTemplate.java_
- `MenuManager.java` — Gestionnaire générique pour les menus internes (inventaires custom).  
  _src/main/java/com/blocknights/game/ui/menu/MenuManager.java_
- `OperatorBrowserMenu.java` — Menu de navigation d'opérateurs avec filtres (onglets).  
  _src/main/java/com/blocknights/game/ui/menu/OperatorBrowserMenu.java_
- `SimplePlaceholder.java` — Résolution de placeholders : PAPI si dispo, sinon remplacements internes.  
  _src/main/java/com/blocknights/game/ui/menu/SimplePlaceholder.java_
- `UiMenu.java` — Contrat minimal pour un menu custom.  
  _src/main/java/com/blocknights/game/ui/menu/UiMenu.java_

### `com.blocknights.i18n` (1)
- `Messages.java` — Class: messages.  
  _src/main/java/com/blocknights/i18n/Messages.java_

### `com.blocknights.integration` (3)
- `CitizensAdapter.java` — Class: citizens adapter.  
  _src/main/java/com/blocknights/integration/CitizensAdapter.java_
- `DeluxeMenusHook.java` — Intégration légère avec DeluxeMenus pour ouvrir/recharger les menus.  
  _src/main/java/com/blocknights/integration/DeluxeMenusHook.java_
- `FaweIntegration.java` — Lightweight bridge to FastAsyncWorldEdit.  
  _src/main/java/com/blocknights/integration/FaweIntegration.java_

### `com.blocknights.maps` (8)
- `BnMap.java` — Minimal, coherent BnMap used by the editor and map manager.  
  _src/main/java/com/blocknights/maps/BnMap.java_
- `FolderMapIO.java` — Class: folder map io.  
  _src/main/java/com/blocknights/maps/FolderMapIO.java_
- `MapFS.java` — Class: map fs.  
  _src/main/java/com/blocknights/maps/MapFS.java_
- `MapIO.java` — Class: map io.  
  _src/main/java/com/blocknights/maps/MapIO.java_
- `MapManager.java` — MapManager compatible avec les appels existants (loadOrCreate, listMaps, exportMap, save).  
  _src/main/java/com/blocknights/maps/MapManager.java_
- `VoidWorldAllocator.java` — Simple allocator that spreads map instances across a void world so that they do not overlap.  
  _src/main/java/com/blocknights/maps/VoidWorldAllocator.java_
- `VoidWorldController.java` — Contrôle l'environnement du monde void (cycle jour/nuit et météo).  
  _src/main/java/com/blocknights/maps/VoidWorldController.java_
- `ZoneRegistry.java` — Registry for zone.  
  _src/main/java/com/blocknights/maps/ZoneRegistry.java_

### `com.blocknights.operators` (6)
- `BnOpCommandTest.java` — Class: bn op command test.  
  _export/src/test/java/com/blocknights/operators/BnOpCommandTest.java_
- `OperatorLore.java` — Static lore library for operators.  
  _src/main/java/com/blocknights/operators/OperatorLore.java_
- `OperatorPointPool.java` — Identifie le pool de points opérateur affecté par une mutation.  
  _src/main/java/com/blocknights/operators/OperatorPointPool.java_
- `OperatorPointsChangeEvent.java` — Événement déclenché lorsqu'un joueur voit ses points opérateur ajustés.  
  _src/main/java/com/blocknights/operators/OperatorPointsChangeEvent.java_
- `OperatorPointsChangeListener.java` — Enregistre les événements de changement de points dans la base et la télémétrie.  
  _src/main/java/com/blocknights/operators/OperatorPointsChangeListener.java_
- `OperatorPointsLogDao.java` — DAO responsable de la persistance des modifications de points opérateur.  
  _src/main/java/com/blocknights/operators/OperatorPointsLogDao.java_

### `com.blocknights.operators.model` (1)
- `OperatorRole.java` — Enum: operator role.  
  _src/main/java/com/blocknights/operators/model/OperatorRole.java_

### `com.blocknights.operators.runtime` (10)
- `OperatorDerivedStats.java` — Calcule une vue consolidée des statistiques dérivées d'un opérateur pour affichage.  
  _src/main/java/com/blocknights/operators/runtime/OperatorDerivedStats.java_
- `OperatorInstance.java` — Runtime representation of a placed operator.  
  _src/main/java/com/blocknights/operators/runtime/OperatorInstance.java_
- `OperatorLevelService.java` — Manages per-player operator progression and XP curve.  
  _src/main/java/com/blocknights/operators/runtime/OperatorLevelService.java_
- `OperatorProgress.java` — Tracks level and stat growth for a player's operator.  
  _src/main/java/com/blocknights/operators/runtime/OperatorProgress.java_
- `OperatorProjectile.java` — Extremely small custom projectile used for play‑testing.  
  _src/main/java/com/blocknights/operators/runtime/OperatorProjectile.java_
- `OperatorXpSource.java` — Catégorise les gains d'expérience opérateur afin d'alimenter la progression globale du joueur.  
  _src/main/java/com/blocknights/operators/runtime/OperatorXpSource.java_
- `PlayerProgressionService.java` — Service de progression globale joueur.  
  _src/main/java/com/blocknights/operators/runtime/PlayerProgressionService.java_
- `SkillActivationMode.java` — Mode d'activation des compétences opérateur.  
  _src/main/java/com/blocknights/operators/runtime/SkillActivationMode.java_
- `StatTuning.java` — Applique des ajustements post-calcul configurables sur les statistiques des opérateurs.  
  _src/main/java/com/blocknights/operators/runtime/StatTuning.java_
- `TargetPriorityTest.java` — Class: target priority test.  
  _export/src/test/java/com/blocknights/operators/runtime/TargetPriorityTest.java_

### `com.blocknights.operators.ui` (4)
- `AllocationGui.java` — Simple GUI allowing players to allocate their manual stat points.  
  _src/main/java/com/blocknights/operators/ui/AllocationGui.java_
- `OperatorPointsMenu.java` — Passerelle vers le menu DeluxeMenus centralisé pour la gestion des points opérateur.  
  _src/main/java/com/blocknights/operators/ui/OperatorPointsMenu.java_
- `OperatorStaffMenu.java` — Ouvre les menus staff opérateur et injecte les placeholders attendus.  
  _src/main/java/com/blocknights/operators/ui/OperatorStaffMenu.java_
- `OperatorStaffMenuGenerator.java` — Génère les menus DeluxeMenus staff opérateur à partir des templates intégrés.  
  _src/main/java/com/blocknights/operators/ui/OperatorStaffMenuGenerator.java_

### `com.blocknights.operators.visual` (6)
- `ArmorStandVisualBackend.java` — Simple ArmorStand-based fallback backend.  
  _src/main/java/com/blocknights/operators/visual/ArmorStandVisualBackend.java_
- `CitizensVisualBackend.java` — Citizens backend spawning an NPC surrounded by directional holograms.  
  _src/main/java/com/blocknights/operators/visual/CitizensVisualBackend.java_
- `DisplayVisualBackend.java` — Default backend relying on TextDisplay to show operator names.  
  _src/main/java/com/blocknights/operators/visual/DisplayVisualBackend.java_
- `OperatorVisualBackend.java` — Abstraction for operator visuals so different backends (ArmorStand, Citizens…) can be plugged in without touching gameplay code.  
  _src/main/java/com/blocknights/operators/visual/OperatorVisualBackend.java_
- `OperatorVisualHelper.java` — Méthodes utilitaires partagées par les différents backends visuels.  
  _src/main/java/com/blocknights/operators/visual/OperatorVisualHelper.java_
- `OperatorVisualStyle.java` — Centralise la lecture de la configuration des hologrammes opérateurs afin d'harmoniser les différents backends visuels.  
  _src/main/java/com/blocknights/operators/visual/OperatorVisualStyle.java_

### `com.blocknights.papi` (2)
- `BlocknightsExpansion.java` — Class: blocknights expansion.  
  _src/main/java/com/blocknights/papi/BlocknightsExpansion.java_
- `BlocknightsShortExpansion.java` — Extension PlaceholderAPI courte permettant d'utiliser l'identifiant %bn_*%.  
  _src/main/java/com/blocknights/papi/BlocknightsShortExpansion.java_

### `com.blocknights.pathfinding` (1)
- `AutoPathfinder.java` — A* pathfinder on a 2D grid (x,z) at a fixed Y, tailored for Minecraft.  
  _src/main/java/com/blocknights/pathfinding/AutoPathfinder.java_

### `com.blocknights.playtest` (3)
- `BaseHealthManager.java` — Manager for base health.  
  _src/main/java/com/blocknights/playtest/BaseHealthManager.java_
- `PathRunner.java` — Class: path runner.  
  _src/main/java/com/blocknights/playtest/PathRunner.java_
- `PlayTestManager.java` — Manager for play test.  
  _src/main/java/com/blocknights/playtest/PlayTestManager.java_

### `com.blocknights.stats` (11)
- `FileLeaderboardStorage.java` — Backend de classement basé sur un simple fichier YAML.  
  _src/main/java/com/blocknights/stats/FileLeaderboardStorage.java_
- `HistoryGui.java` — Simple inventory menu displaying recent session history.  
  _src/main/java/com/blocknights/stats/HistoryGui.java_
- `HistoryManager.java` — Persistent storage for past game sessions.  
  _src/main/java/com/blocknights/stats/HistoryManager.java_
- `InMemoryLeaderboardStorage.java` — Backend simple en mémoire, utilisé comme repli en cas d'échec SQL.  
  _src/main/java/com/blocknights/stats/InMemoryLeaderboardStorage.java_
- `LeaderboardStorage.java` — Abstraction de stockage pour les classements, afin de supporter plusieurs backends.  
  _src/main/java/com/blocknights/stats/LeaderboardStorage.java_
- `SqlLeaderboard.java` — Class: sql leaderboard.  
  _src/main/java/com/blocknights/stats/SqlLeaderboard.java_
- `SqlOperatorStats.java` — Stores per-player operator stats in SQL, including upgrades and talents.  
  _src/main/java/com/blocknights/stats/SqlOperatorStats.java_
- `StatsManager.java` — Simple wrapper autour de {  
  _src/main/java/com/blocknights/stats/StatsManager.java_
- `StatsManagerTest.java` — Class: stats manager test.  
  _export/src/test/java/com/blocknights/stats/StatsManagerTest.java_
- `SynergyTelemetry.java` — Append-only flux JSON (format JSON Lines) listant les synergies actives à la fin d'un run.  
  _src/main/java/com/blocknights/stats/SynergyTelemetry.java_
- `TimeAttackLeaderboard.java` — Classe simple conservant un classement en mémoire pour chaque map Time Attack.  
  _src/main/java/com/blocknights/stats/TimeAttackLeaderboard.java_

### `com.blocknights.telemetry` (2)
- `OperatorTelemetry.java` — Télémétrie basique autour des modifications de points opérateur.  
  _src/main/java/com/blocknights/telemetry/OperatorTelemetry.java_
- `SynergyTelemetry.java` — Append-only flux JSON (format JSON Lines) listant les synergies actives à la fin d'un run.  
  _src/main/java/com/blocknights/telemetry/SynergyTelemetry.java_

### `com.blocknights.telemetry.medals` (2)
- `MedalExportFormat.java` — Enum: medal export format.  
  _src/main/java/com/blocknights/telemetry/medals/MedalExportFormat.java_
- `MedalTelemetry.java` — Télémétrie dédiée aux médailles.  
  _src/main/java/com/blocknights/telemetry/medals/MedalTelemetry.java_

### `com.blocknights.telemetry.quests` (1)
- `QuestTelemetry.java` — Outils de télémétrie pour les quêtes : JSON lines et alertes Discord.  
  _src/main/java/com/blocknights/telemetry/quests/QuestTelemetry.java_

### `com.blocknights.tools` (1)
- `AnimationValidator.java` — Validateur offline des animations pour la CI.  
  _src/main/java/com/blocknights/tools/AnimationValidator.java_

### `com.blocknights.util` (5)
- `ItemUtil.java` — Utility helpers for item.  
  _src/main/java/com/blocknights/util/ItemUtil.java_
- `LocationUtil.java` — Utility helpers for location.  
  _src/main/java/com/blocknights/util/LocationUtil.java_
- `Locs.java` — Class: locs.  
  _src/main/java/com/blocknights/util/Locs.java_
- `MineskinService.java` — Small helper around the Mineskin API.  
  _src/main/java/com/blocknights/util/MineskinService.java_
- `VoidWorldGenerator.java` — Class: void world generator.  
  _src/main/java/com/blocknights/util/VoidWorldGenerator.java_

### `com.blocknights.validation` (2)
- `MapValidator.java` — Utilitaires de validation de maps (session en cours ou carte persistée).  
  _src/main/java/com/blocknights/validation/MapValidator.java_
- `MapValidatorConfigTest.java` — Class: map validator config test.  
  _export/src/test/java/com/blocknights/validation/MapValidatorConfigTest.java_

### `com.blocknights.waves` (9)
- `BurrowerRunner.java` — Runner for burrowing enemies.  
  _src/main/java/com/blocknights/waves/BurrowerRunner.java_
- `FlyingRunner.java` — Runner for flying enemies.  
  _src/main/java/com/blocknights/waves/FlyingRunner.java_
- `MapSettings.java` — Paramètres de gameplay propres à une carte.  
  _src/main/java/com/blocknights/waves/MapSettings.java_
- `MobRunner.java` — Base runner that moves a mob along a path and handles rewards.  
  _src/main/java/com/blocknights/waves/MobRunner.java_
- `MobSettings.java` — Paramètres globaux pour le comportement des mobs.  
  _src/main/java/com/blocknights/waves/MobSettings.java_
- `WaveEditorClipboardTest.java` — Vérifie rapidement les interactions UX du presse-papiers dans l'éditeur de vagues.  
  _export/src/test/java/com/blocknights/waves/WaveEditorClipboardTest.java_
- `WaveEditorGui.java` — Éditeur en jeu des vagues d'ennemis.  
  _src/main/java/com/blocknights/waves/WaveEditorGui.java_
- `WaveManager.java` — YAML driven wave orchestrator used for playtesting.  
  _src/main/java/com/blocknights/waves/WaveManager.java_
- `WaveManagerResolveTypeTest.java` — Class: wave manager resolve type test.  
  _export/src/test/java/com/blocknights/waves/WaveManagerResolveTypeTest.java_

### `com.blocknights.waves.editor` (6)
- `GroupDelayMenu.java` — Sous-menu permettant d'ajuster le délai entre les apparitions et la vitesse.  
  _src/main/java/com/blocknights/waves/editor/GroupDelayMenu.java_
- `GroupMenuContext.java` — Contexte partagé entre les sous-menus de configuration des groupes.  
  _src/main/java/com/blocknights/waves/editor/GroupMenuContext.java_
- `GroupMobMenu.java` — Sous-menu permettant de choisir l'entité associée à un groupe.  
  _src/main/java/com/blocknights/waves/editor/GroupMobMenu.java_
- `GroupPathMenu.java` — Sous-menu pour modifier la voie et le chemin d'un groupe.  
  _src/main/java/com/blocknights/waves/editor/GroupPathMenu.java_
- `GroupQuantityMenu.java` — Sous-menu dédié à la quantité d'ennemis d'un groupe.  
  _src/main/java/com/blocknights/waves/editor/GroupQuantityMenu.java_
- `GroupStatMenu.java` — Sous-menus numériques pour les groupes (dégâts, résistances, etc.).  
  _src/main/java/com/blocknights/waves/editor/GroupStatMenu.java_
