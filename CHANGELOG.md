# Changelog

## 1.2.0 — 2026-08-19

### Viewer cleanup — Farmer guides split by machine

- Split the previous mixed `Farmer Harvesting` viewer category into three player-facing sections: `Farmer Harvest Tools`, `Paddy Farmer Harvesting`, and `Rich Farmer Harvesting`.
- `Paddy Farmer Harvesting` is now exclusive to Paddy Farmer / Rich Paddy Farmer and documents Rice plus Sugar Cane; Rich Paddy Rice explicitly explains the optional Knife behaviour.
- `Rich Farmer Harvesting` now contains only Rich Farmer crops: normal Easy Villagers crops, Tomato/Rope, Mushroom Colonies, Melon and Pumpkin.
- `Farmer Harvest Tools` is a short shared reference for Knife, Hoe and Axe and is only attached to Rich variants that actually have the Harvest Tool slot.
- Removed the duplicate Harvest Tools page from Block Guide; specialized Block Guide pages now explain their own required/optional tools in context.
- Rewrote Farmer viewer text for casual players, removing implementation-facing wording such as virtual ages and authoritative loot tables while preserving useful behaviour such as Fortune/Silk Touch, tool requirements and durability.

### Viewer cleanup — compact Cutter Axe Actions

- Replaced the exhaustive per-item Cutter Axe Actions listing with one compact two-row documentation page.
- The copper row now uses a representative waxed Copper Block → Copper Block action with the tooltip `Any waxed/oxidized copper item`, covering scrape and wax-removal semantics without enumerating every copper state.
- The stripping row now groups the eight vanilla logs plus Bamboo Block under `Any compatible log` and displays their corresponding stripped outputs.
- Compatible modded stripping entries are discovered from the live axe transformation rules and added to the same rotating ingredient group, so supported woods such as Ars Nouveau logs can appear without hardcoded per-mod pages.
- JEI and EMI consume the same compact shared dataset, reducing the category from dozens of redundant pages to a single explanatory page.

### Integration correction — contextual EMI Block Guide

- Corrected EMI Block Guide discovery so each guide page is sourced by the machine it documents; opening that block's **Recipes** view now groups `Block Guide` beside normal crafting categories instead of routing the guide through **Uses**.
- Removed Block Guide workstation/input/catalyst lookup semantics from EMI while keeping those ingredients visible inside the guide page itself.
- Removed standalone-addon references to experimental patch-era branding and kept only the native `EfdcKnife` → `EfdcHarvestTool` legacy migration path.

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
- Added nine contextual Block Guide pages covering Paddy Rice/Sugar Cane, normal crops, Tomato/Rope, Mushroom Colonies, Melon, Pumpkin, Cutter and Noise Switch.
- Added scoped Farmer viewer categories for general Harvest Tools, Paddy harvesting and Rich Farmer harvesting.
- Added explicit EMI category localization and wrapped EMI text.
- Preserved Cutter integration as a Farmer's Delight Cutting catalyst/workstation.


### Viewer source-completion pass

- Implemented the viewer-neutral `ToolUse`, expanded `FarmerHarvestInfo`, `GuideIngredient` and `BlockGuideInfo` data model.
- Farmer viewer documentation is split into 3 Harvest Tool pages, 2 Paddy pages and 5 Rich Farmer pages.
- Implemented the nine-page contextual Block Guide in both JEI and EMI from one shared `RecipeViewerData` source.
- Added JEI Block Guide categories/catalysts and EMI contextual Block Guide categories while preserving Cutter integration with Farmer's Delight Cutting.
- Added wrapped EMI guide text and concise tooltips for required/optional tools and durability.
- Updated Sugar Cane guide wording to match the final sneak-use dismantle interaction.
- The viewer layer is source-complete but remains an integration candidate until JEI-only, EMI-only, combined and no-viewer launch tests pass.

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
- Added one-way migration from the legacy `EfdcKnife` NBT key into the generalized Harvest Tool storage.
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
