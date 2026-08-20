# Changelog

## 1.2.0 — 2026-08-19

### Runtime hotfix — Cutter standby and stem Rich Soil cadence

- Cutter processing now starts only when at least one queued input can actually be processed by the equipped Knife/Axe. Missing or wrong tools remain at 0% instead of looping 0→100→0.
- Cutter processability is cached and invalidated by tool/input/output/villager changes, avoiding repeated failed recipe scans while a machine is blocked.
- A failed completion caused by capacity/state changes parks the Cutter until its inventories change instead of immediately starting another failed cycle.
- Melon/Pumpkin Rich Soil acceleration is no longer coupled to Easy Villagers `farmSpeed`. Their virtual Rich Soil now follows Minecraft random-tick selection plus Farmer's Delight `richSoilBoostChance`; fruit generation remains on normal Farmer speed.
- Melon/Pumpkin Fortune behavior remains delegated to the real vanilla block loot table. Timed throughput comparisons must separate fruit-generation RNG from per-fruit loot yield.

### Farmer tools and crops

- Generalized the Rich Farmer/Rich Paddy Knife slot into a Harvest Tool slot for Knives, Hoes and Axes while preserving legacy Knife NBT.
- Added optional Fortune Hoe routing for compatible normal crops/Tomatoes without artificial durability loss.
- Added virtual Melon/Pumpkin stem + fruit automation; Rich Soil affects the stem only and ready fruit requires an Axe.
- Added Paddy/Rich Paddy Sugar Cane mode using installed Sand, with lossless dismantling and no Rich Soil speed bonus.

### Villager Noise Switch

- Added the Villager Noise Switch and its shaped recipe.
- Stores one Easy Villagers Villager and toggles a persistent client-local global Villager mute preference.
- Visual Lever/Redstone state is client-only and produces no server BlockState, redstone signal, neighbor update or Observer event.

### Jade / JEI / EMI

- Jade now reports hard tool blockers, Sugar Cane mode, Melon/Pumpkin phases and the local Noise Switch state.
- Added shared viewer-neutral Block Guide data rendered by both JEI and EMI.
- Added ten guide pages covering Paddy Rice/Sugar Cane, Harvest Tools, normal crops, Tomato/Rope, Mushroom Colonies, Melon, Pumpkin, Cutter and Noise Switch.
- Expanded Farmer Harvesting with optional/required tool semantics, durability information and real-loot/illustrative output markers.
- Added explicit EMI category localization and wrapped EMI text.
- Preserved Cutter integration as a Farmer's Delight Cutting catalyst/workstation.

### Reliability / compatibility

- Fixed Paddy/Rich Paddy visual geometry: the villager support and Sugar Cane Sand now sit submerged with their top faces exactly flush to the internal waterline; Paddy villagers render at 90% scale to keep profession hats inside the enclosure.
- Fixed Melon/Pumpkin layout so the stem and fruit occupy the 1/3 and 2/3 horizontal positions, and ready fruit renders the vanilla attached-stem state facing the fruit.
- Fixed virtual growth pacing: Easy Villagers `farm_speed` now has a safe default-10 fallback instead of defaulting to 1 on reflection failure, and Sugar Cane now advances its vanilla-style internal AGE 0..15 before creating each new section.
- Jade now explicitly marks hard-tool harvests as ready, reports Sugar Cane internal progress, and distinguishes a missing Cutter tool from an equipped wrong tool.
- Hardened non-RNG Cutter tool diagnostics with representative Knife/Axe fallbacks during tag lookup edge cases.
- Added non-RNG Cutter operation probing for diagnostics.
- Preserved the historical Jade provider UID `farmer_knife` while renaming the implementation to Harvest Tool.
- Kept all nine shipped locale files at identical key parity.
- `Output full` Jade diagnostics remain deliberately deferred until they can be determined without rolling chance outputs.

## 1.1.0 — 2026-08-18

### Farmer tools

- Added a protected Knife slot to Rich Farmer and Rich Paddy Farmer.
- Knife-aware normal-crop and Rice loot now use the real equipped Knife in the loot context.
- Mature Mushroom Colonies wait for a Knife before harvesting.
- Added one-way migration from the experimental `EruruuKnife` NBT key to native `EfdcKnife` storage.
- Added dedicated Farmer menus/screens for the protected Knife slot.

### Cutter

- Added the automated Cutter with nine work-surface variants.
- Added 4 input slots, one protected Knife/Axe slot and 4 output slots.
- Added adult-villager processing, Farmer's Delight Cutting recipe support, Fortune forwarding and Axe strip/scrape/unwax fallback.
- Added atomic output simulation and sided automation.
- Added Cutter rendering, item variant rendering, crafting recipe and Recipe Book support.

### Integrations / UX

- Added optional JEI and EMI documentation for Knife harvesting and Cutter Axe actions.
- Extended Jade with Farmer Knife and Cutter status/output information.
- Added Recipe Book display recipes for Paddy/Rich/Rich Paddy upgrades.
- Creative Pick Block now returns clean Farmer items instead of cloning complete machine state.
- `build-dev.bat` no longer contains a stale hardcoded development version.

## 1.0.0 — 2026-08-16

Initial stable release for Minecraft 1.21.1 / NeoForge.

### Farmers

- Added Paddy Farmer for Farmer's Delight Rice.
- Added Rich Farmer with Farmer's Delight Rich Soil acceleration.
- Added Rich Paddy Farmer combining the Paddy lifecycle with Rich Soil acceleration.
- Farmer/Paddy upgrades preserve villager, crop, progress and output data.

### Crops

- Full Rice lower-plant + panicle lifecycle.
- Persistent Tomato harvesting.
- Up to two independent Tomato Rope sections.
- Red/Brown Mushroom Colony support in Rich Farmer.
- Optional Ars Nouveau Magebloom support.
- Optional Argentum crop support.

### Integration / UX

- Optional Jade crop/growth/Rich Soil tooltip integration.
- Visible villager and crop rendering inside compat Farmers.
- Hopper/NeoForge item-handler output automation.
- Dedicated Creative Mode tab.
- Correct Farmer-facing orientation, Paddy water visuals and Rich Farmer rendering.

### Reliability

- Fixed mature Rice harvest stalls.
- Fixed Tomato Rope sections progressing in permanent lockstep.
- Fixed missing breaking textures and client synchronization/rendering issues.
- Mature crops now wait when the output inventory cannot hold the complete generated harvest, preventing silent resource loss.

### Mature harvest cadence hotfix
- Mature normal crops, Tomato sections and Mushroom Colonies no longer require an additional successful `farmSpeed` RNG roll before harvesting.
- Once a final harvestable stage is reached, the Farmer attempts harvest on the next one-second work cadence.
- Tool/output/villager blockers keep the crop mature and in standby; they do not reset growth or consume resources.
- Tomato rope sections use the same deterministic harvest phase and do not immediately regrow on the same cadence they are harvested.
