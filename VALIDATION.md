# Validation status — 0.1.0-dev Rich Farmer milestone

Validated on 2026-08-16:

- Paddy + Rich Farmer core sources compile with Java 21 against the Minecraft 1.21.1 / NeoForge 21.1.235 merged API used for the isolated check.
- `CropBlock#getBonemealAgeIncrease(Level)` exists with the expected protected signature in the mapped NeoForge/Minecraft API.
- Capability registration compiles against NeoForge 21.1.235's real `RegisterCapabilitiesEvent`/`Capabilities` signatures; only unavailable loader/event-bus support types are supplied as compile-only helper stubs in this isolated environment.
- Easy Villagers 1.21.1 upstream source was checked for `getSeedCrop`, `isValidSeed`, the private boolean `ageCrop(...)`, four-slot output inventory/item handler and `farmSpeed` behavior.
- Rich Farmer seed selection resolves the crop through Easy Villagers without invoking the unplaced delegate's `setCrop()` sync path.
- Rich Farmer normal crop work invokes only Easy Villagers' internal crop-aging operation, not its complete tile-entity tick/sync cycle.
- Farmer's Delight upstream source was checked for live `richSoilBoostChance`, default/range semantics and the `unaffected_by_rich_soil` tag behavior.
- Rich Soil virtual boost respects `randomTickSpeed` draws and uses normal `CropBlock` bone-meal age increments instead of a fixed multiplier.
- Farmer's Delight Rice lower/panicle block IDs and two-stage `age 0..3` lifecycle remain represented by the Paddy engine.
- All JSON resources parse successfully.
- No Java source directly imports Easy Villagers or Farmer's Delight implementation packages.
- No Easy Villagers/Farmer's Delight classes or assets are copied into this project.
- Rice is not globally injected into `minecraft:villager_plantable_seeds`.

Implemented but still requiring a live modded instance test:

- Insert/remove Easy Villagers villager item in Paddy and Rich Farmer.
- Insert/remove Farmer's Delight Rice in Paddy.
- Rice growth from lower crop through mature panicles and repeated panicle regrowth.
- Normal Rich Farmer seed selection for vanilla/Cabbage/Onion and any compatible tagged crop.
- Easy Villagers normal growth/harvest behavior inside Rich Farmer.
- Rich Soil acceleration frequency with default and modified Farmer's Delight config.
- Rich Soil behavior with modified `randomTickSpeed` and `farmersdelight:unaffected_by_rich_soil` tags.
- Mature crop loot entering the four-slot output inventory.
- Easy Villagers output menu opening on both compat blocks.
- Hopper/item-capability extraction and persistence after chunk/world reload.
- NBT round trip from Easy Villagers Farmer -> Rich Farmer and Paddy -> Rich Paddy.

Not yet validated/implemented:

- Full NeoForge client launch with the required dependency JARs in this environment.
- Dedicated server launch.
- Multiplayer synchronization.
- Visual villager/crop block-entity renderer.
- Tomato + Rope engine.
- Mushroom Colony engine.
- Rich Paddy Rich Soil acceleration.

The included JAR is an isolated compile artifact for in-game testing, not a release candidate. A normal local build can be produced with `build-dev.bat` once Gradle can resolve NeoForge normally.
