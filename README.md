# Easy Farmer's Delight Compat

Independent, unofficial compatibility addon for **Easy Villagers** and **Farmer's Delight** on Minecraft 1.21.1 / NeoForge.

> This project is not affiliated with, endorsed by, sponsored by, or maintained by the authors of Easy Villagers or Farmer's Delight. It does not redistribute either dependency.

## Current development milestone (0.1.0-dev)

The compatibility-safe foundation is in place. The **Paddy Farmer has a functional Rice engine**, the **Rich Farmer runs normal Easy Villagers crops with virtual Rich Soil acceleration**, and the first **Tomato + Rope** gameplay layer is implemented. This milestone also fixes creative-menu discoverability and the original custom-recipe slot-order bug.

### Foundation

- Registers **Paddy Farmer**, **Rich Farmer** and **Rich Paddy Farmer** as original blocks.
- Registers a dedicated **Easy Farmer's Delight Compat** Creative Mode tab containing all three blocks.
- Adds the agreed recipes:
  - Paddy: `GGG / GFG / IWI`
  - Rich: `GGG / GFG / BRB`
- Preserves the source Farmer's item data/components during upgrades.
- Rewrites only the placed block-entity type to this mod's own block entity.
- Preserves unknown block-entity NBT so Easy Villagers data is not discarded.
- Preserves our own farmer data when broken/picked.
- Adds optional Argentum support for Yerba Mate, Té, Batata and Membrillo through `minecraft:villager_plantable_seeds`.
- Declares Easy Villagers and Farmer's Delight as required dependencies; Jade and Argentum are optional.

### Functional Paddy Farmer

- Accepts the Easy Villagers villager item and keeps the villager payload intact.
- Uses a Farmer villager for work, including stored baby-villager aging.
- Accepts **Farmer's Delight Rice** directly as its crop.
- Models the full two-part Rice lifecycle:
  - submerged Rice crop: ages `0..3`;
  - Rice panicles: ages `0..3`.
- Harvests the mature panicles through Farmer's Delight's actual block loot table instead of hardcoding Rice drops.
- Keeps the mature submerged Rice plant after harvest so only the panicles need to regrow.
- Uses Easy Villagers' configured Farmer speed.
- Reuses Easy Villagers' four-slot output menu through a narrow runtime adapter.
- Exposes the four output slots through NeoForge's block item-handler capability for hopper/mod automation.
- Tracks GUI/capability mutations so output inventory changes are persisted by this addon's block entity.

Rice is intentionally **not** added globally to `minecraft:villager_plantable_seeds`: doing that would also make the normal Easy Villagers Farmer try to process Rice with its generic single-block age logic.

### Functional Rich Farmer base

- Upgrades an Easy Villagers Farmer with `GGG / GFG / BRB` while preserving its block-entity payload.
- Accepts the same normal seed set Easy Villagers validates, including its `villager_plantable_seeds` tag and crop blacklist.
- Reuses Easy Villagers' normal crop aging, mature harvest, loot generation, Farmer requirement and four-slot output logic.
- Uses Easy Villagers' configured Farmer speed.
- Keeps the Easy Villagers delegate unplaced and avoids invoking its own block-entity sync packets.
- References `farmersdelight:block/rich_soil` directly in the current model rather than copying Farmer's Delight's texture.
- Reads Farmer's Delight's live `richSoilBoostChance` value.
- Respects `randomTickSpeed` and Farmer's Delight's `farmersdelight:unaffected_by_rich_soil` block tag.
- For normal `CropBlock`-style crops, applies the crop's own bone-meal age increment instead of an arbitrary speed multiplier.

### Tomato + Rope test layer

- Rich Farmer accepts `farmersdelight:tomato_seeds` as a dedicated persistent crop.
- Models the real Farmer's Delight budding stage before switching to the reusable tomato vine.
- Mature tomato harvest keeps the plant, resets its fruit age and produces 1-2 Tomatoes with the same 5% Rotten Tomato chance.
- Right-clicking a Tomato Rich Farmer with `farmersdelight:rope` installs up to **2 Rope** sections.
- Base, Rope 1 and Rope 2 store independent `0..3` progress values and harvest independently.
- Sneak-right-click removes the uppermost Rope first and returns it. Once no Ropes remain, the next sneak interaction removes the Tomato Seeds selection.

The **Mushroom Colony** engine is the next Rich Farmer crop layer.

## Rich Paddy status

The Rich Paddy block and upgrade recipe already exist and preserve Paddy data, but its Rich Soil acceleration is reserved for the dedicated Rich Paddy phase. At this milestone it behaves like the current Paddy Farmer engine.

## Why the block models currently look simple

The current models are intentionally developer placeholders made from vanilla/Farmer's Delight runtime textures. No Easy Villagers models or textures were copied. Final models/renderers will be original and will visually represent the villager, soil/water and crop state.

## Next implementation layers

1. Live in-game validation of the Creative tab, corrected recipes, Paddy/Rice and Rich Farmer/Tomato/Rope flows.
2. Red/Brown Mushroom Colony lifecycle.
3. Rich Paddy Rich Soil acceleration.
4. Optional Jade HUD provider.
5. Dedicated original block/entity renderers and final assets, including visual Rope/crop state.
6. Multiplayer/dedicated-server tests and balance pass.

## External projects

- Easy Villagers — required dependency, separate project.
- Farmer's Delight — required dependency, separate project.
- Jade — optional integration.
- Argentum — optional integration.

## Building

The project uses Java 21 and NeoForge ModDevGradle. A normal developer checkout should build with Gradle once dependencies are available. The repository intentionally does not vendor Easy Villagers or Farmer's Delight JARs.

## Local build

On Windows, run `build-dev.bat`. It searches for Java 21 (including common Prism Launcher locations), downloads Gradle 9.2.1 locally if needed, and runs `clean build`. The resulting mod JAR is written to `build/libs/`.

On Linux/WSL, the equivalent helper is `build-dev.sh`.
