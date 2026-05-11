# Blue Staffy

<p align="center">
  <img src="docs/banner.png" alt="Blue Staffy" />
</p>

A Minecraft mod that adds the Blue Staffordshire Bull Terrier as a tameable companion — with its own personality, behaviours, and that unmistakable Staffy energy.

Available for **Fabric** and **NeoForge** on Minecraft **1.21.11**.

---

## What's in the mod

### Appearance
- [x] Custom entity model — Staffy proportions (wide skull, rose ears, barrel chest, short thick legs, stub tail)
- [x] Blue-gray texture with white chest blaze and small dark eyes
- [x] Baby/puppy variant via `BabyModelTransform`
- [ ] Proper Blockbench-quality texture
- [ ] Collar variants

### Behaviours

#### Zoomies
The signature behaviour. Triggers in three situations:

| Trigger | Duration |
|---|---|
| Fed cooked steak while under full health | **14–18 s** + excited barking |
| Fed any other wolf food while under full health | 6–9 s |
| Owner right-clicks to unsit the dog | 3–5 s |
| Dog exits water onto solid ground (post-bath sprint) | 3–5 s |

While zooming the dog sprints at **2.5× normal speed** in tight, erratic arcs (direction changes every 5–7 ticks). Zoomies override all other goals — sit, follow, wander, and attack — at priority 0. A wolf bark/growl plays the moment they start.

**Cooked steak bonus:** triggers a burst of three barks at the moment of feeding, then the dog barks every 1.5–3 s throughout the run.

**Collision damage:** anything the Staffy runs into while zooming (mobs, players, armour stands) takes **1.5 damage** (¾ heart). A 1-second cooldown prevents hitting the same target every tick.

#### Other behaviours
- **Idle head tilt** — while standing still the head tilts side-to-side with smooth ease-in/ease-out. Three variations cycle randomly (~8 s period, stops the moment the dog moves):
  - *Single tilt* (~50%) — tilts to one side, holds, returns to centre
  - *Sweep* (~25%) — tilts to one side then flows continuously through to the other without stopping
  - *Quick-then-slow* (~25%) — snaps to one side, then drifts at a slightly slower speed to the other
- **Napping** — when tamed and sitting quietly the dog will doze off (15–45 s sessions). While asleep:
  - Head droops forward with a gentle breathing rise-and-fall
  - Occasional sighs play at random pitch (high, low, mid — unpredictable, like a sleeping dog)
  - Periodically (every 35–55 s) the dog flops to its side for 12–18 s with a deeper settling sigh as it goes down, then rights itself again
- **Resting whimper** — occasional soft whimper while sitting (~25 s average gap).
- **Idle oink/snort sounds** — Staffies are notorious snorters.
- Taming, sitting, following, and loyalty (inherited from Wolf)

### Roadmap
- [ ] Excited greeting — mini-zoomie when owner comes close (goal exists, not yet active)
- [ ] Unique idle animations (wiggle bum)
- [ ] Sounds (growls, excited barking)
- [ ] Puppy variant (model layer exists; texture and behaviour pending)
- [ ] Toy interactions
- [ ] Named dog support

---

## Installing the mod

### Fabric

1. Install **Fabric Loader 0.19.2** via the [Fabric Installer](https://fabricmc.net/use/installer/)
2. Install **Fabric API 0.141.3+1.21.11** — drop the Fabric API JAR into your mods folder
3. Drop `bluestaffy-<version>.jar` into your mods folder

```bash
make build-fabric   # produces fabric/build/libs/bluestaffy-1.0.0.jar
```

### NeoForge

1. Install **NeoForge 21.11.42** — download the installer from [neoforged.net](https://neoforged.net/) and run it:
   ```
   java -jar neoforge-21.11.42-installer.jar
   ```
2. Drop `bluestaffy-<version>.jar` into your mods folder

```bash
make build-neoforge   # produces neoforge/build/libs/bluestaffy-21.11.42.jar
```

### Mods folder location

| OS | Path |
|---|---|
| macOS | `~/Library/Application Support/minecraft/mods/` |
| Windows | `%APPDATA%\.minecraft\mods\` |
| Linux | `~/.minecraft/mods/` |

Create the `mods/` folder if it doesn't exist, then launch Minecraft with the correct loader profile and click Play.

To spawn one in Creative mode:
```
/summon bluestaffy:blue_staffy
```
Or find the spawn egg in the **Blue Staffy** creative tab (NeoForge) or the Spawn Eggs tab (Fabric).

---

## Development setup

### Requirements
- Java 21
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (recommended) or Eclipse

### Commands

```bash
make build            # compile and package both Fabric and NeoForge JARs
make build-fabric     # Fabric JAR only
make build-neoforge   # NeoForge JAR only
make run-fabric       # launch Fabric client for live testing
make run-neoforge     # launch NeoForge client for live testing
make clean            # wipe build artefacts
make jar-fabric       # build and print the Fabric JAR path
make jar-neoforge     # build and print the NeoForge JAR path
make help             # list all targets
```

---

## Project structure

```
common/src/main/java/ai/gamu/bluestaffy/
  BlueStaffy.java              # Mod initialiser — registrations, MOD_ID
  entity/
    BlueStaffyEntity.java      # Dog entity — zoomies triggers, sounds, collision damage
    goal/
      ZoomiesGoal.java         # AI goal: 2.5× speed erratic sprint, priority 0
      NapGoal.java             # Napping detection helper
      GreetOwnerGoal.java      # Reserved: greeting mini-zoomie (not yet active)
  client/
    model/
      BlueStaffyModel.java     # Custom entity model + idle head tilt animation
    renderer/
      BlueStaffyRenderer.java  # Entity renderer
      BlueStaffyRenderState.java
  registry/
    ModEntities.java           # Entity type registration
    ModItems.java              # Spawn egg + item registration
    ModBlocks.java             # Block registration

fabric/src/main/java/ai/gamu/bluestaffy/fabric/
  BlueStaffyFabric.java        # Fabric mod initialiser (ModInitializer)
  client/
    BlueStaffyFabricClient.java # Fabric client initialiser (ClientModInitializer)

neoforge/src/main/java/ai/gamu/bluestaffy/neoforge/
  BlueStaffyNeoForge.java      # NeoForge @Mod entry point + creative tab
  client/
    BlueStaffyNeoForgeClient.java # NeoForge client event subscriber

common/src/main/resources/assets/bluestaffy/
  textures/entity/             # Entity skin (64×64, blue-gray + white chest blaze)
  textures/item/               # Spawn egg texture
  models/                      # Item + block models
  items/                       # Item definitions (1.21.11 format)
  blockstates/                 # Block state definitions
  lang/en_us.json              # Translations
```

---

## Tech stack

| | |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | 0.19.2 |
| Fabric API | 0.141.3+1.21.11 |
| NeoForge | 21.11.42 |
| Architectury API | 19.0.1 |
| Language | Java 21 |
| Build tool | Gradle 9 + Architectury Loom 1.14 |
| Mappings | Parchment 2025.12.20 |

---

## Notes for contributors

- Entity model work is done in [Blockbench](https://www.blockbench.net/) — export as a Java entity model
- `BlueStaffyEntity` extends `Wolf`, giving taming, pathfinding, and sitting for free. Custom behaviours are added as AI goals
- All entity logic lives in `common/` — neither the `fabric/` nor `neoforge/` modules touch game behaviour
- Item models use the 1.21.11 `assets/<mod>/items/` definition format alongside traditional `models/item/` files
- Run `make run-fabric` or `make run-neoforge` to test in-game — no need to install the mod separately

---

## License

All Rights Reserved — [iamgaru](https://github.com/iamgaru)
