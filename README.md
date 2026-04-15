# Blue Staffy

A Minecraft mod that adds the Blue Staffordshire Bull Terrier as a unique tameable companion — with its own personality, behaviours, and that unmistakable Staffy energy.

Built with [NeoForge](https://neoforged.net/) for Minecraft **1.21.11**.

---

## What's in the mod

- **Blue Staffy entity** — a tameable dog with wolf-level AI as its foundation
- **Spawn egg** — find it in the Blue Staffy creative tab
- Unique appearance, behaviours, and personality planned (see roadmap below)

---

## Roadmap

### Appearance
- [ ] Custom Blockbench model (stockier build than a wolf — wider chest, shorter legs)
- [ ] Proper blue-gray texture with white chest marking
- [ ] Collar variants

### Behaviours
- [ ] **Zoomies** — random bursts of high-speed sprinting, especially after a bath or when excited
- [ ] Taming with specific food items
- [ ] Sitting, following, and staying loyal to owner
- [ ] Unique idle animations (head tilt, wiggle bum)
- [ ] Sounds (huffs, snorts, excited barking)

### Future
- [ ] Puppy variant
- [ ] Toy interactions
- [ ] Named dog support

---

## Development setup

### Requirements
- Java 21
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) or Eclipse (IntelliJ recommended)

### Getting started

```bash
git clone <this repo>
cd bluestaffy
./gradlew build
```

### Run the client (test world)

```bash
./gradlew runClient
```

Once in game, switch to **Creative mode**, open the **Blue Staffy** tab, and use the spawn egg to summon one.

You can also use the command:
```
/summon bluestaffy:blue_staffy
```

### Refresh dependencies if something breaks

```bash
./gradlew --refresh-dependencies
./gradlew clean build
```

---

## Project structure

```
src/main/java/ai/gamu/bluestaffy/
  BlueStaffy.java              # Mod entry point, registrations
  BlueStaffyClient.java        # Client-only setup (renderers)
  Config.java                  # Mod config
  entity/
    BlueStaffyEntity.java      # The dog entity (extends Wolf)
  client/
    renderer/
      BlueStaffyRenderer.java  # Entity renderer

src/main/resources/assets/bluestaffy/
  textures/entity/             # Entity skin
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
| Mod loader | NeoForge 21.11.42 |
| Language | Java 21 |
| Build tool | Gradle |
| Mappings | Parchment 2025.12.20 |

---

## Notes for contributors

- Entity model work is done in [Blockbench](https://www.blockbench.net/) — export as a Java entity model
- The `BlueStaffyEntity` extends `Wolf` giving us taming, pathfinding, and sitting for free. Custom behaviours are added as AI goals
- Item models use the 1.21.11 `assets/<mod>/items/` definition format alongside traditional `models/item/` files
- Run `./gradlew runClient` to test — no need to install the mod separately during development

---

## License

All Rights Reserved — [iamgaru](https://github.com/iamgaru)
