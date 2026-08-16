# Build status — 0.1.0-dev

## Baseline

- Minecraft: **1.21.1**
- NeoForge minimum/baseline: **21.1.235**
- Java: **21**
- Required runtime mods: **Easy Villagers 1.1.42+**, **Farmer's Delight 1.2.9+**
- Optional integrations: **Jade**, **Argentum**, **Ars Nouveau**

## Implemented

- Paddy Farmer block, recipe and Rice engine
- Rich Farmer block and upgrade recipe
- Rich Paddy Farmer block and upgrade recipe
- Upgrade preservation of source Farmer/Paddy block-entity data
- Creative Mode tab
- Correct 3x3 custom recipe slot matching
- Easy Villagers four-slot output menu reuse
- NeoForge item-handler capability for output automation
- Farmer villager insertion/removal and age handling
- Horizontal `FACING` state and Farmer-compatible orientation
- Client villager/crop renderer
- Crop-specific RenderType/tint rendering
- Paddy shallow-water visual basin
- Rich-only inset second glass shell
- Correct breaking/particle textures
- Farmer's Delight Rice IDs (`rice`, `rice_panicles`)
- Full Rice virtual lifecycle `0..7`
- Mature Rice harvest on the next Farmer cadence without an extra `farmSpeed` RNG roll
- Rich Soil virtual work-cycle behavior using Farmer's Delight `richSoilBoostChance`
- Rich Soil acceleration for Rich Farmer compatible crops
- Rich Soil acceleration across the full Rich Paddy Rice lifecycle
- Persistent Tomato lifecycle
- Tomato Rope 1 / Rope 2 installation, removal, rendering and independent progress/work rolls
- Red/Brown Mushroom Colony lifecycle
- Optional Argentum seed-tag compatibility
- Optional Ars Nouveau Magebloom compatibility
- Optional Jade Farmer-status integration
- Mod-list logo and resource-pack icon assets

## Live singleplayer validation completed

Validated in a real modded 1.21.1 instance:

- block placement and visual orientation;
- visible villager and crop rendering;
- Rice growth and repeated harvest;
- corrected mature-Rice harvest timing;
- Rich Soil acceleration on terrestrial crops;
- Rich Soil acceleration on Rice;
- Tomato persistent harvesting;
- independent Tomato Base / Rope 1 / Rope 2 progress;
- Red/Brown Mushroom Colony growth and repeated harvesting;
- non-vanilla Farmer's Delight crops tested successfully;
- Ars Nouveau Magebloom accepted and accelerated by Rich Soil;
- Jade tooltip displaying crop, growth, Rope progress, Rich Soil status and output inventory context.

An accelerated comparison at `tick rate 2000` observed Rich Paddy producing roughly **30% more Rice harvests** than Paddy over the tested interval. This is a validation observation, not a guaranteed fixed multiplier; the system remains RNG/config driven.

## Remaining release validation

- Dedicated-server startup
- Multiplayer synchronization with multiple players observing/interacting
- Chunk unload/reload persistence
- Full server restart persistence
- Hopper/item capability behavior under multiplayer/server conditions
- Upgrade round-trip preservation under server conditions
- Optional-mod test matrix (Jade / Argentum / Ars Nouveau absent and present)
- JEI recipe visibility
- EMI recipe visibility
- Final balance/smoke test after the multiplayer pass

See `MULTIPLAYER-TEST-CHECKLIST.md`.
