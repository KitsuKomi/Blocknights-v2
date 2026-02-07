Blocknights
Tower Defense RPG entièrement éditable en jeu (UI), avec stats Persona, leveling, skills custom, talents, synergies et pathfinding automatique.
Blocknights est un moteur de Tower Defense/RPG avancé pour Minecraft, conçu pour être entièrement éditable en jeu via une interface UI complète.
Aucune configuration manuelle, aucun fichier YAML à modifier : tout se fait via des menus, des wands, des hologrammes et des outils intégrés.
Le projet combine :
- un éditeur de maps complet (avec pathfinding automatique),
- un éditeur de waves,
- un éditeur d’opérateurs,
- un éditeur d’ennemis,
- un éditeur de skills,
- un éditeur de talents,
- un éditeur de synergies,
- un système de leveling,
- un moteur de combat,
- un système de story/triggers,
- un système de visualisation via Citizens.
Blocknights est pensé comme un game engine dans Minecraft.

1. 🎮 Vision générale
Blocknights est un Tower Defense où le joueur :
- Choisit une map
- Sélectionne ses opérateurs
- Place ses unités sur des points dédiés
- Défend contre des vagues d’ennemis
- Fait progresser ses opérateurs (leveling, talents, synergies)
- Débloque des skills et des améliorations
Tout le contenu (maps, waves, opérateurs, skills…) est créé et modifié en jeu via un éditeur UI.

2. 🧩 Architecture générale
Le projet est organisé en modules :
- MapEditor : création et édition des maps
- WaveEditor : création des vagues
- OperatorEditor : création et édition des opérateurs
- EnemyEditor : gestion des ennemis
- SkillSystem : architecture modulaire des compétences
- TalentSystem : arbre de talents
- SynergySystem : bonus combinés
- CombatEngine : moteur de combat
- GameSession : runtime d’une partie
- Persistence : sauvegarde des données
- CitizensIntegration : skins et modèles visuels

3. 🗺️ Éditeur de maps (Map Editor UI)
L’éditeur de maps est accessible via un menu principal appelé Toolbox.
3.1 Toolbox (menu principal)
Chaque item du menu représente un outil :
- Wand Spawn/End
- Wand Path
- Wand Operator Placement (Melee / Ranged / Both)
- Wand FAWE Box
- Bouton “Visualiser”
- Bouton “Nettoyer”
- Bouton “Sauvegarder la map”
- Bouton “Charger une map existante”
- Bouton “Tester la map”

3.2 Wands d’édition
Wand Path
- Clic gauche → ajouter un point de path
- Clic droit → retirer le dernier point
Wand Operator Placement
Trois types :
- Melee
- Ranged
- Both
Chaque point est visualisé via hologramme.
Wand FAWE Box
- pos1 / pos2
- sauvegarde en schematic
- chargement d’une schematic existante

3.3 Pathfinding automatique
Lorsque Spawn et End sont définis :
- un chemin optimal est calculé automatiquement
- le pathfinding se relance si l’un des points est déplacé
- le chemin est visualisé via :
- hologrammes
- glow markers
- (optionnel) particules
Le chemin est sauvegardé comme pathPoints.

3.4 Visualisation
L’éditeur utilise :
- Hologrammes pour les points importants
- Glow markers pour les chemins
- Fake blocks pour les zones FAWE
- ActionBars pour le feedback

3.5 Sauvegarde / Chargement
Chaque map contient :
- spawn
- end
- pathPoints
- operatorPlacements
- schematic FAWE
- metadata

3.6 Validation
Une map valide doit contenir :
- un spawn
- un end
- un path généré
- au moins un placement opérateur
- une schematic

4. 🌊 Éditeur de waves
Permet de :
- créer des waves
- ajouter des ennemis
- définir quantité, délai, lane
- tester la wave
- sauvegarder / charger

5. 👹 Éditeur d’ennemis
Chaque ennemi possède :
- HP
- vitesse
- résistances
- modèle visuel
- comportement
- loot (optionnel)

6. 🧍‍♂️ Éditeur d’opérateurs
Chaque opérateur possède :
- stats Persona (STR, MAG, END, AGI, LUK)
- HP / portée / intervalle
- rôle (melee, ranged, caster…)
- skills
- talents
- synergies
- skin Citizens
- animations
- modèle visuel

7. 🎭 Système de stats Persona
Les opérateurs utilisent les 5 stats classiques :
- STR : dégâts physiques
- MAG : dégâts magiques / puissance des skills
- END : défense / réduction de dégâts
- AGI : vitesse d’attaque / cooldowns
- LUK : critiques / chances d’effets
Ces stats influencent :
- dégâts
- vitesse
- défense
- skills
- talents
- synergies

8. 📈 Système de leveling
Chaque opérateur possède :
- XP
- niveau
- courbe d’XP
- scaling des stats
- recalcul des dérivées (HP, DPS, intervalle…)

9. ✨ Système de skills (modulaire)
Un skill est défini par :
- type (damage, heal, buff, debuff, aoe…)
- scaling (STR, MAG, %HP…)
- cooldown
- charges
- effets multiples
- conditions
- visuels (particles, sounds, animations)
- interactions talents/synergies

10. 🔗 Système de synergies
Les synergies permettent :
- bonus combinés
- passifs débloqués
- interactions entre opérateurs

11. 🌳 Système de talents
Chaque opérateur possède un arbre de talents :
- nœuds
- paliers
- effets
- interactions avec skills et stats

12. 📖 Système de story / triggers
Permet :
- dialogues
- événements
- conditions
- récompenses

13. ⚔️ Moteur de combat
13.1 Boucle opérateurs
- ciblage
- intervalle d’attaque
- dégâts (scaling STR/MAG)
- skills
- buffs/debuffs
- critiques (LUK)
13.2 Boucle ennemis
- pathing
- HP
- mort
- arrivée au End

14. 🧠 GameSession
Une session contient :
- la map
- les waves
- les opérateurs placés
- les ennemis actifs
- le moteur de combat
- la progression

15. 💾 Persistance
Sauvegarde :
- maps
- waves
- opérateurs (level, stats)
- talents
- synergies
- story

16. 🤖 Instructions pour Copilot
Copilot doit :
- suivre l’architecture décrite dans ce README
- utiliser les stats Persona correctement
- ne pas réécrire les systèmes existants
- étendre le code proprement
- respecter les modules
- garder la compatibilité avec l’éditeur UI

17. ➕ Ajouter du contenu
Ajouter une map
- Ouvrir la Toolbox
- Définir Spawn/End
- Laisser le pathfinding générer le chemin
- Ajouter les placements opérateurs
- Définir la zone FAWE
- Sauvegarder
Ajouter une wave
- Ouvrir l’éditeur de waves
- Ajouter des ennemis
- Définir délais et quantités
- Tester
Ajouter un opérateur
- Définir stats Persona
- Définir skills
- Définir talents
- Définir skin Citizens

🎯 Fin du README

Chris, ce README est complet, propre, structuré, et surtout parfait pour Copilot.
On peut maintenant :
- ajouter des sections,
- ajouter des schémas,
- ajouter des exemples de code,
- ou commencer à implémenter les modules un par un.
Tu veux qu’on ajoute quoi ensuite ?
