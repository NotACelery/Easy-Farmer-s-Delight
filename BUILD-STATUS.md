# Build status — 1.2.0 integration candidate

## Baseline

- Minecraft: **1.21.1**
- NeoForge baseline: **21.1.235**
- Java: **21**
- Required runtime mods: **Easy Villagers 1.1.42+**, **Farmer's Delight 1.2.9+**
- Optional integrations: **Jade**, **JEI**, **EMI**, **Argentum**, **Ars Nouveau**
- Git recovery point before viewer-completion pass: `dcc44eef3e7327c0d35bb2dbe9d347f7ad2a517e`
- Release state: **integration candidate; viewer/build regression still required**

## Gameplay/core confirmed in-game

- Paddy/Rich Paddy final waterline/support geometry
- Paddy/Rich Paddy dismantle interactions
- realistic Sugar Cane virtual growth cadence
- Melon/Pumpkin 1/3 + 2/3 renderer layout and attached stems
- Harvest Tool / Cutting Tool rotating icon tooltips
- Cutter standby with missing/wrong required tool
- no failed Cutter 0→100→0 loop
- Jade `Waiting for...` and `Wrong tool...` diagnostics
- Villager Noise Switch basic behavior
- Melon Silk Touch behavior

## Implemented in the current 1.2.0 source

- generalized Harvest Tool slot: Knife + Hoe + Axe
- crop-specific tool routing
- mature deterministic harvest cadence for normal crops, Tomato sections and Mushroom Colonies
- Sugar Cane mode for Paddy/Rich Paddy
- Melon/Pumpkin virtual stem + fruit lifecycle
- client-local persistent Villager Noise Switch
- expanded Jade machine diagnostics
- shared viewer-neutral Farmer Harvest / Block Guide model
- 7 Farmer Harvest documentation entries
- 10 Block Guide pages
- JEI Block Guide category + catalysts
- EMI Block Guide category + per-block Recipes-tab discovery
- wrapped EMI guide text
- Cutter Farmer's Delight Cutting integration retained
- nine locale variants kept in key parity

## Required before publication

1. run `build-dev.bat` from the committed baseline + this patch;
2. launch with JEI only;
3. launch with EMI only;
4. launch with JEI + EMI;
5. launch with neither viewer;
6. validate all ten Block Guide pages and seven Farmer Harvest entries;
7. run remaining gameplay/Jade persistence and edge-case regression from `VALIDATION.md`;
8. only after those checks promote 1.2.0 to stable/public.

Deprecated API warnings from NeoForge 1.21.1 are not release failures by themselves.
