# Bluestaffy — Fabric Loader Port (Multi-Loader Refactor)

> **For Claude Code:** This document is the spec for adding Fabric Loader support to the Bluestaffy mod. Read it fully before changing any files. When in doubt, prefer reading the actual source over assuming — sections marked **VERIFY** mean "open the file and confirm before acting."

---

## 1. Goal

Add Fabric Loader support to Bluestaffy **without rewriting the mob's behavior**. The entity, AI goal, model, renderer, and sound logic must remain in one place and be shared across both loaders. NeoForge support must continue to work exactly as it does today.

End state:

- `./gradlew :neoforge:build` produces a NeoForge-compatible JAR (functionally equivalent to today's build).
- `./gradlew :fabric:build` produces a Fabric-compatible JAR.
- Both JARs install into a vanilla 1.21.x client (with their respective loaders) and produce a Blue Staffy that looks, sounds, and behaves identically.

## 2. Non-goals

- **No vanilla / datapack version.** Datapacks cannot register a custom `EntityType` or `Goal`. Out of scope.
- **No Paper/Spigot plugin.** Out of scope.
- **No gameplay changes.** The Fabric build must match NeoForge feature-for-feature. If a feature is hard to port, flag it in **Section 11 (Open Questions)** rather than dropping it.
- **No mappings overhaul.** Stay on Parchment / current mappings. Yarn is acceptable in the Fabric module if Parchment for the target MC version isn't published yet — see Section 6.

## 3. Constraints

The mob's behavior code is the load-bearing IP. Touch it as little as possible.

- The following classes/files must move to `common/` with **only import changes and registration call-site changes** — no logic edits:
  - `BlueStaffyEntity`
  - `entity/goal/ZoomiesGoal`
  - `client/model/BlueStaffyModel` (including `BabyModelTransform`)
  - `client/renderer/BlueStaffyRenderer`
  - All sound event IDs (string identifiers must be byte-identical)
  - All texture, model JSON, lang, and sound asset paths
- `setupAnim()` procedural animation code is sacred. Do not refactor it. Do not "improve" it. Move it as-is.
- Sound event registry IDs (`bluestaffy:bark_zoomie`, etc. — **VERIFY** exact IDs from `src/main/resources/data/bluestaffy/` and `assets/bluestaffy/sounds.json`) must remain identical so existing recordings, videos, and any future server-side audio replacement packs continue to work.

## 4. Target environment

**VERIFY before starting** — read these from `gradle.properties` and pin the Fabric stack to the same MC patch version:

- `minecraft_version` (e.g. `1.21.x`)
- `neo_version` (e.g. `21.11.x`)
- `parchment_minecraft` / `parchment_version`
- Java version (expect Java 21)

Then pin Fabric to match:

- `fabric_loader_version` — latest stable for the target MC version
- `fabric_api_version` — latest matching the target MC version
- `yarn_mappings` — only if Parchment isn't available for this MC version on the Fabric side; otherwise use Parchment via Loom's `parchment` mapping merger.

If the `minecraft_version` in `gradle.properties` doesn't resolve to a real public MC release, **stop and ask** before continuing. (The user previously mentioned `1.21.11` which may be a typo.)

## 5. Architecture

Use **Architectury Loom** for the multi-loader setup. It's the de facto standard for NeoForge↔Fabric multi-loader projects in 1.20+ and integrates cleanly with Parchment.

Three Gradle subprojects:

- **`common/`** — All shared code. Compiles against vanilla Minecraft + Architectury API stubs. No NeoForge or Fabric imports allowed here, ever. This is enforced by Loom's classpath.
- **`neoforge/`** — NeoForge-specific entrypoint, registration, and any NeoForge-only event handlers. Depends on `common`.
- **`fabric/`** — Fabric-specific entrypoint, registration, and Fabric-only event handlers. Depends on `common`.

Where common code needs to do something loader-specific (registration, creative tab events, attribute defaults), it should call **Architectury API** abstractions:

- `RegistrySupplier<T>` and `DeferredRegister` (Architectury) — replaces NeoForge's `DeferredRegister`. Works on both loaders.
- `CreativeTabRegistry` — replaces NeoForge's `BuildCreativeModeTabContentsEvent`.
- `EntityAttributeRegistry` — replaces NeoForge's `EntityAttributeCreationEvent`.

Renderer and model layer registration remain client-only and live in each loader module (Fabric uses `EntityRendererRegistry` / `EntityModelLayerRegistry`; NeoForge uses `EntityRenderersEvent.RegisterRenderers` / `RegisterLayerDefinitions`). Architectury Client Events can also abstract these if convenient, but per-loader is fine since it's ~10 lines each.

## 6. Repository layout (target)

```
bluestaffy/
├── build.gradle                    # root, applies architectury-plugin
├── settings.gradle                 # includes :common, :neoforge, :fabric
├── gradle.properties               # all version pins
├── common/
│   ├── build.gradle
│   └── src/main/
│       ├── java/ai/gamu/bluestaffy/
│       │   ├── BlueStaffy.java                    # MOD_ID, shared init
│       │   ├── registry/
│       │   │   ├── ModEntities.java               # RegistrySupplier-based
│       │   │   ├── ModItems.java
│       │   │   └── ModSounds.java
│       │   ├── entity/
│       │   │   ├── BlueStaffyEntity.java          # MOVED, minimal edits
│       │   │   └── goal/ZoomiesGoal.java          # MOVED, no edits
│       │   └── client/
│       │       ├── model/BlueStaffyModel.java     # MOVED, no edits
│       │       └── renderer/BlueStaffyRenderer.java
│       └── resources/
│           ├── assets/bluestaffy/                 # MOVED whole tree
│           └── data/bluestaffy/                   # MOVED whole tree
├── neoforge/
│   ├── build.gradle
│   └── src/main/
│       ├── java/ai/gamu/bluestaffy/neoforge/
│       │   ├── BlueStaffyNeoForge.java            # @Mod entrypoint
│       │   └── client/BlueStaffyNeoForgeClient.java
│       └── resources/
│           ├── META-INF/neoforge.mods.toml        # MOVED from main
│           └── pack.mcmeta
└── fabric/
    ├── build.gradle
    └── src/main/
        ├── java/ai/gamu/bluestaffy/fabric/
        │   ├── BlueStaffyFabric.java              # ModInitializer (NEW)
        │   └── client/BlueStaffyFabricClient.java # ClientModInitializer (NEW)
        └── resources/
            ├── fabric.mod.json                    # NEW
            └── pack.mcmeta
```

## 7. File-by-file plan

### 7a. Files that MOVE to `common/` with minimal changes

For each, the only allowed edits are:

1. Replace `net.neoforged.*` imports with vanilla equivalents or Architectury equivalents.
2. Replace `DeferredRegister.create(BuiltInRegistries.X, MOD_ID)` (NeoForge) with `dev.architectury.registry.registries.DeferredRegister.create(MOD_ID, Registries.X)` (Architectury).
3. Replace `RegistryObject<T>` / `Supplier<T>` with `RegistrySupplier<T>`.
4. Replace `event.accept(...)` creative-tab calls with `CreativeTabRegistry.modify(...)`.
5. Replace `event.put(EntityType, attributes)` with Architectury's `EntityAttributeRegistry.register(...)`.

| File | Logic edits allowed? | Notes |
|---|---|---|
| `BlueStaffyEntity.java` | No | Just imports + `super(...)` constructor signature stays the same. Verify `EntityType<? extends Wolf>` parameter is unchanged. |
| `entity/goal/ZoomiesGoal.java` | **No.** | Pure logic file. Imports only. |
| `client/model/BlueStaffyModel.java` | **No.** | `setupAnim()` procedural code is sacred. Imports only. |
| `client/model/BabyModelTransform.java` | **No.** | Imports only. |
| `client/renderer/BlueStaffyRenderer.java` | No | Imports only. May need `ResourceLocation` construction style updated for 1.21.x (`ResourceLocation.fromNamespaceAndPath` instead of `new ResourceLocation(...)`). **VERIFY** which form the current code uses — leave it unchanged if it already works. |
| `assets/bluestaffy/**` | No | Move whole tree to `common/src/main/resources/assets/bluestaffy/`. |
| `data/bluestaffy/**` | No | Move whole tree to `common/src/main/resources/data/bluestaffy/`. |

### 7b. Files that get rewritten (registration glue)

#### `common/.../BlueStaffy.java` (shared init)

```java
public final class BlueStaffy {
    public static final String MOD_ID = "bluestaffy";

    public static void init() {
        ModSounds.SOUNDS.register();
        ModEntities.ENTITIES.register();
        ModItems.ITEMS.register();
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
```

#### `common/.../registry/ModEntities.java`

```java
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
        DeferredRegister.create(BlueStaffy.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<BlueStaffyEntity>> BLUE_STAFFY =
        ENTITIES.register("blue_staffy", () ->
            EntityType.Builder.of(BlueStaffyEntity::new, MobCategory.CREATURE)
                .sized(0.6f, 0.85f)   // VERIFY current values from existing NeoForge registration
                .build("blue_staffy")
        );
}
```

**VERIFY** the current `EntityType.Builder` parameters (size, MobCategory, clientTrackingRange, updateInterval) from the existing NeoForge registration and copy them exactly.

#### `common/.../registry/ModItems.java` and `ModSounds.java`

Same `DeferredRegister` pattern. Mirror current NeoForge registrations 1:1. **VERIFY** every sound event name and item registry name — no renames.

### 7c. NeoForge module (mostly the existing entrypoint, slightly trimmed)

#### `neoforge/.../BlueStaffyNeoForge.java`

```java
@Mod(BlueStaffy.MOD_ID)
public final class BlueStaffyNeoForge {
    public BlueStaffyNeoForge(IEventBus modBus) {
        BlueStaffy.init();   // delegates to common
        modBus.addListener(this::onAttributes);
        modBus.addListener(this::onCreativeTab);
    }

    private void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BLUE_STAFFY.get(), BlueStaffyEntity.createAttributes().build());
    }

    private void onCreativeTab(BuildCreativeModeTabContentsEvent event) {
        // VERIFY which tab the existing mod adds the spawn egg to.
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.BLUE_STAFFY_SPAWN_EGG.get());
        }
    }
}
```

Client renderer registration moves to `BlueStaffyNeoForgeClient` and uses `EntityRenderersEvent.RegisterRenderers` and `RegisterLayerDefinitions` exactly as the current code does. **Lift this code unchanged from the existing source.**

### 7d. Fabric module (NEW)

#### `fabric/.../BlueStaffyFabric.java`

```java
public final class BlueStaffyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BlueStaffy.init();

        FabricDefaultAttributeRegistry.register(
            ModEntities.BLUE_STAFFY.get(),
            BlueStaffyEntity.createAttributes().build()
        );

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries ->
            entries.accept(ModItems.BLUE_STAFFY_SPAWN_EGG.get())
        );
    }
}
```

#### `fabric/.../client/BlueStaffyFabricClient.java`

```java
public final class BlueStaffyFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(
            BlueStaffyModelLayers.BLUE_STAFFY,        // VERIFY: lift the ModelLayerLocation constant from current code
            BlueStaffyModel::createBodyLayer
        );
        EntityRendererRegistry.register(
            ModEntities.BLUE_STAFFY.get(),
            BlueStaffyRenderer::new
        );
    }
}
```

#### `fabric/src/main/resources/fabric.mod.json`

```json
{
  "schemaVersion": 1,
  "id": "bluestaffy",
  "version": "${version}",
  "name": "Blue Staffy",
  "description": "A Blue Staffy companion for Minecraft.",
  "authors": ["iamgaru"],
  "contact": { "sources": "https://github.com/iamgaru/bluestaffy" },
  "license": "ARR",
  "icon": "assets/bluestaffy/icon.png",
  "environment": "*",
  "entrypoints": {
    "main": ["ai.gamu.bluestaffy.fabric.BlueStaffyFabric"],
    "client": ["ai.gamu.bluestaffy.fabric.client.BlueStaffyFabricClient"]
  },
  "depends": {
    "fabricloader": ">=0.16",
    "fabric-api": "*",
    "minecraft": "~1.21",
    "java": ">=21"
  }
}
```

**VERIFY** license, author, icon path, and adjust the `minecraft` predicate to the exact target MC patch.

## 8. Build configuration

Use the **Architectury Loom example template** (`architectury/architectury-templates`) as the reference. Key version pins to set in root `gradle.properties`:

```
minecraft_version=          # from current gradle.properties
neoforge_version=           # from current gradle.properties
fabric_loader_version=      # latest matching
fabric_api_version=         # latest matching
architectury_version=       # latest matching MC version
parchment_minecraft=        # from current gradle.properties
parchment_version=          # from current gradle.properties
mod_version=                # from current gradle.properties
maven_group=ai.gamu
archives_base_name=bluestaffy
```

Each module's `build.gradle` follows the standard Loom pattern. **Use the Architectury template verbatim** rather than hand-rolling — it handles the resource-merging quirks correctly.

The root `build.gradle` should set up `architectury { }` and configure both loaders' transformations of `common`'s output.

## 9. Acceptance criteria

The port is done when **all** of the following are true:

1. `./gradlew build` (from repo root) succeeds with no warnings about missing classes, broken refmaps, or duplicate IDs.
2. `./gradlew :neoforge:build` produces a JAR. Installing it in a fresh NeoForge 1.21.x profile loads the mod with no errors.
3. `./gradlew :fabric:build` produces a JAR. Installing it in a fresh Fabric 1.21.x profile (with Fabric API + Architectury API installed) loads the mod with no errors.
4. **Behavioral parity test** (run on both loaders, side-by-side if possible):
   - `/summon bluestaffy:blue_staffy ~ ~ ~` produces a visually correct Blue Staffy.
   - Spawn egg appears in the same creative tab on both.
   - Tame with raw beef, feed cooked steak at full health → triple bark, no zoomies.
   - Feed cooked steak at non-full health → triple bark + zoomies for 14–18s.
   - Feed other wolf food at non-full health → zoomies for 6–9s.
   - Unsit a sitting Staffy → zoomies for 3–5s.
   - Walk into water and back onto land → zoomies for 3–5s.
   - During zoomies: speed ≈2.5x, erratic direction changes, periodic bark sounds, mobs/players hit during zoomies take 1.5 damage with a 1s cooldown per target.
   - Idle: head-tilt animation cycles through three variants.
   - Resting/sleeping: doze 15–45s, breathing rise/fall, occasional flop with sleep sigh at random pitch.
   - Baby/puppy: spawned via `/summon ... {Age:-24000}` shows the puppy proportions (BabyModelTransform applied).
5. Sound event IDs are unchanged. Verify with `/playsound bluestaffy:<each sound id> ...` — every ID that worked on the NeoForge build still works on Fabric, byte-for-byte.
6. Existing NeoForge users who update see no regressions — same JAR filename pattern, same mod ID, same registry IDs.

## 10. Verification steps Claude Code should run

In order:

1. `git status` — confirm clean working tree before starting.
2. Read `gradle.properties` and capture all version pins. Stop if `minecraft_version` doesn't look like a real release.
3. Read `BlueStaffyEntity.java`, `ZoomiesGoal.java`, `BlueStaffyModel.java`, and `BlueStaffyRenderer.java` end-to-end. Confirm assumptions in this doc match the code (especially: entity size, attribute setup, sound event IDs, model layer location constant, creative tab destination).
4. Create a feature branch: `git checkout -b fabric-port`.
5. Refactor to multi-loader layout (Sections 5–7). Commit after each module compiles.
6. Run `./gradlew :neoforge:build` — must succeed and produce a JAR functionally equivalent to the pre-refactor build.
7. Run `./gradlew :fabric:build` — must succeed.
8. Run the acceptance tests in Section 9 manually (or write a brief test plan note in `TESTING.md` if manual is required).
9. Open a PR with a checklist tied to Section 9.

## 11. Open questions for Claude Code to resolve from the repo

Each of these should be answered by reading the actual source, not guessed:

- [ ] Exact `minecraft_version` and `neo_version` from `gradle.properties`.
- [ ] Whether `1.21.11` is the literal value in `gradle.properties` (and if so, what release it actually resolves to).
- [ ] Exact `EntityType.Builder` parameters currently used (size, tracking range, update interval).
- [ ] Exact creative tab(s) the spawn egg is added to.
- [ ] Whether there are any additional registered objects (custom items, particles, data components) not enumerated in this doc.
- [ ] Whether the renderer uses `ResourceLocation.fromNamespaceAndPath` or `new ResourceLocation(...)` — keep whichever already works.
- [ ] Whether there are any NeoForge-specific capabilities, mixin/coremod hooks, or `IExtensibleEnum` extensions in use. If yes, those need a per-loader implementation and **must be flagged before refactoring** — they may force a deeper port than this spec assumes.
- [ ] Existing license headers — preserve them in moved files.

## 12. Rollback plan

The refactor is non-destructive — all logic stays the same, only the project structure changes. If something breaks:

1. The `main` branch is untouched. The work is on `fabric-port`.
2. To roll back partially, `git revert` per-commit. Commits should be small (one per module: "extract common", "wire neoforge", "wire fabric") so a partial rollback is sane.
3. If Architectury proves too painful, the fallback is a **standalone parallel Fabric project** in a sibling repo (`bluestaffy-fabric`). This duplicates source but avoids multi-loader Gradle complexity. Document the decision in the PR description.

## 13. Out of scope, but worth noting in the PR

- Publishing both JARs to Modrinth/CurseForge under the same project listing.
- A GitHub Actions workflow that builds both JARs on tag.
- A combined "Blue Staffy + companion mods" Modrinth modpack.

These are follow-up tasks. Do not bundle them with this port.

---

**End of spec.** Claude Code: when you start, read Section 11's checklist, then come back here. Do not skip the **VERIFY** callouts.
