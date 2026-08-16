# Build status — 1.0.0

## Baseline

- Minecraft: **1.21.1**
- NeoForge minimum/baseline: **21.1.235**
- Java: **21**
- Required runtime mods: **Easy Villagers 1.1.42+**, **Farmer's Delight 1.2.9+**
- Optional integrations: **Jade**, **Argentum**, **Ars Nouveau**
- Release state: **stable / ready for public distribution**

## Implemented

- Paddy Farmer block, recipe and full Rice lifecycle
- Rich Farmer block and state-preserving upgrade recipe
- Rich Paddy Farmer block and state-preserving upgrade recipe
- Creative Mode tab
- Easy Villagers four-slot output menu reuse
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
- Optional Jade tooltip integration
- Mod-list logo and resource-pack icon

## Validation

The 1.0.0 feature set has completed its gameplay and multiplayer release pass, including the final output-capacity regression fix. `MULTIPLAYER-TEST-CHECKLIST.md` is retained as a regression checklist for future releases.

An accelerated `tick rate 2000` comparison observed Rich Paddy producing roughly **30% more Rice harvests** than Paddy during the tested interval. This is an observation, not a fixed multiplier; growth remains RNG/config driven.
