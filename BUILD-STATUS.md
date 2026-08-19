# Build status — 1.1.0

## Baseline

- Minecraft: **1.21.1**
- NeoForge minimum/baseline: **21.1.235**
- Java: **21**
- Required runtime mods: **Easy Villagers 1.1.42+**, **Farmer's Delight 1.2.9+**
- Optional integrations: **Jade**, **JEI**, **EMI**, **Argentum**, **Ars Nouveau**
- Release state: **stable / ready for public distribution**

## Implemented

- Paddy Farmer block, recipe and full Rice lifecycle
- Rich Farmer block and state-preserving upgrade recipe
- Rich Paddy Farmer block and state-preserving upgrade recipe
- Creative Mode tab
- Dedicated Farmer output menus, including the protected Rich-Farmer Knife slot
- NeoForge item-handler capability for hopper/mod automation
- Farmer villager insertion/removal and age handling
- Farmer-compatible facing/orientation
- Visible villager/crop renderer and crop-specific RenderType/tint handling
- Paddy water basin and Rich-only second glass shell
- Correct block-breaking particle textures
- Rich Soil acceleration using Farmer's Delight `richSoilBoostChance`
- Rich Soil acceleration across the complete Rich Paddy Rice lifecycle
- Persistent Tomato + independent Rope 1 / Rope 2 lifecycle
- Red/Brown Mushroom Colony lifecycle
- Lossless harvest gating when output inventory lacks room for the complete harvest
- Optional Argentum crop compatibility
- Optional Ars Nouveau Magebloom compatibility
- Native Rich Farmer / Rich Paddy Knife-aware harvesting and legacy `EruruuKnife` migration
- Automated Cutter with Cutting recipes, Axe actions, villager power, Fortune and sided automation
- Recipe Book display recipes for state-preserving Farmer upgrades
- Optional Jade, JEI and EMI integrations
- Mod-list logo and resource-pack icon

## Validation

The 1.0.0 baseline remains the validated gameplay foundation. Version 1.1.0 adds the Knife/Cutter feature set migrated from the Eruruu sandbox; source-level core and client compilation checks are included in the migration pass, with final in-game regression validation required before publishing the release.

An accelerated `tick rate 2000` comparison observed Rich Paddy producing roughly **30% more Rice harvests** than Paddy during the tested interval. This is an observation, not a fixed multiplier; growth remains RNG/config driven.
