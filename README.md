# Easy Farmer's Delight Compat

Independent, unofficial compatibility addon for **Easy Villagers** and **Farmer's Delight** on Minecraft 1.21.1 / NeoForge.

> This project is not affiliated with, endorsed by, sponsored by, or maintained by the authors of Easy Villagers or Farmer's Delight. It does not redistribute either dependency.

## Current development milestone (0.1.0-dev)

The first milestone establishes the compatibility-safe foundation:

- Registers **Paddy Farmer**, **Rich Farmer** and **Rich Paddy Farmer** as original blocks.
- Adds the agreed recipes:
  - Paddy: `GGG / GFG / IWI`
  - Rich: `GGG / GFG / BRB`
- Preserves the source Farmer's item data/components during upgrades.
- Rewrites only the placed block-entity type to this mod's own block entity.
- Preserves unknown block-entity NBT so Easy Villagers data is not discarded.
- Preserves our own farmer data when broken/picked.
- Adds optional Argentum support for Yerba Mate, Té, Batata and Membrillo through `minecraft:villager_plantable_seeds`.
- Declares Easy Villagers and Farmer's Delight as required dependencies; Jade and Argentum are optional.

## Why the block models currently look simple

The current models are intentionally developer placeholders made from vanilla/Farmer's Delight runtime textures. No Easy Villagers models or textures were copied. Final models/renderers will be original and will visually represent the villager, soil/water and crop state.

## Next implementation layers

1. Farmer inventory + villager interaction/UI.
2. Rice engine for Paddy Farmer.
3. Rich Soil growth behavior following Farmer's Delight configuration.
4. Tomato lifecycle with Base / Rope 1 / Rope 2 independent progress and non-destructive harvest.
5. Red/Brown Mushroom Colony lifecycle.
6. Rich Paddy behavior.
7. Optional Jade HUD provider.
8. Dedicated original block/entity renderers and final assets.
9. Multiplayer/dedicated-server tests and balance pass.

## External projects

- Easy Villagers — required dependency, separate project.
- Farmer's Delight — required dependency, separate project.
- Jade — optional integration.
- Argentum — optional integration.

## Building

The project uses Java 21 and NeoForge ModDevGradle. A normal developer checkout should build with Gradle once dependencies are available. The repository intentionally does not vendor Easy Villagers or Farmer's Delight JARs.
