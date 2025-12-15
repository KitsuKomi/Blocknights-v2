## Copilot Instructions for Blocknights

Short goal: help contributors extend the Minecraft plugin safely, following project conventions.

- **Big picture:** Blocknights is a Paper (Minecraft) plugin (Java 21, Maven). Main bootstrap: `BlocknightsPlugin.java`. UI/editor code lives in `src/main/java/com/blocknights/editor/` and game logic in `src/main/java/com/blocknights/core/`. Content (maps/waves/enemies/operators) is organized under `src/main/java/com/blocknights/content/` with `model`, `loader`, and `storage` subpackages.

- **Build & test:** Use Maven. Typical commands:
  - `mvn package` (produces a shaded JAR in `target/`)
  - Copy the resulting JAR to a Paper server's `plugins/` folder and start the server (ensure server plugins `Citizens`, `FAWE` are installed — they are declared `provided` in `pom.xml`).

- **Important pom behaviors:** Java 21 and `maven-shade-plugin` are used. Shaded relocations:
  - `com.google.gson` -> `com.blocknights.libs.gson`
  - `net.kyori.adventure` -> `com.blocknights.libs.adventure`
Keep those relocations when adding/adjusting dependencies.

- **Conventions & patterns to follow:**
  - Content types follow the `model` / `loader` / `storage` pattern (e.g., maps, waves, enemies). Add new content by creating these three areas and a registration point during plugin startup.
  - Persistence is JSON/schematic-based (see `content/storage/json` and `schematics`). Use Gson for JSON serialization; respect shading/relocation when adding Gson usage.
  - Editor code (wands, toolbox, UI) is in `editor/*` and communicates with core managers via services/registries — avoid breaking public manager APIs; extend instead.

- **Integration notes:**
  - Server plugins (Paper, Citizens, FAWE) are runtime-provided. During development run a Paper server with those plugin JARs in `plugins/`.
  - `plugin.yml` is at `src/main/resources/plugin.yml` — update it when you add commands or new plugin features.

- **Where to look for examples:**
  - Bootstrap & registration: `src/main/java/com/blocknights/BlocknightsPlugin.java`
  - README for domain model and editor behavior: `README.md`
  - Packaging & relocations: `pom.xml`

- **Do's and don'ts:**
  - Do extend existing systems (skills, talents, synergies) by adding new `model` + `effect` classes and registering them in the appropriate registry.
  - Don't convert `provided` dependencies to compile-time jars; they must remain `provided` to match server environment.
  - Don't remove or change shade relocations lightly — they prevent runtime class conflicts on servers.

If something is ambiguous (where to register a new loader or how a manager is expected to behave), ask for a code owner or point me to a relevant file to inspect. Feedback welcome — tell me what parts need more detail.
