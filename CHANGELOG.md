# Changelog

## 1.3.1 — 2026-08-23

- Replaced the mod icon and resource-pack icon with the new in-game screenshot-based artwork.
- Cleaned up overly verbose and redundant source comments without changing runtime behavior.
- Fixed the Iron Farm Noise Switch display Golem moving its head/body while stored; the miniature Golem now renders in a fully frozen pose.

## 1.3.0 — 2026-08-22

- Villager Noise Switch is now always non-stackable, matching its Iron Farm sibling.
- Iron Farm Noise Switch inventory/JEI/EMI previews always render the pedestal and Lever, and completed item state previews render the stored Iron Golem.
- Iron Farm Noise Switch Block Guide is condensed into a visual `empty switch + 4× Iron Block + Carved Pumpkin -> completed switch` transformation, leaving room to explain the actual client-side sound-muting behavior.

- Added the Iron Farm Noise Switch for Easy Villagers Iron Farms.
- The switch uses the former Iron-based Noise Switch recipe, then requires four additional Iron Blocks inserted one at a time plus a final Carved Pumpkin.
- Its renderer mirrors vanilla Iron Golem construction order: base, body, left arm, right arm, then permanent miniature Iron Golem after the pumpkin is consumed.
- Completed Iron Farm Noise Switches are permanently assembled, always non-stackable, and preserve assembly/Golem state when mined.
- Added a second persistent client-only preference so each player can independently toggle Iron Farm noise without changing server/world state.
- Sound filtering is restricted to Zombie Ambient and Iron Golem Hurt/Death events whose exact source position is an `easy_villagers:iron_farm`; real mobs are unaffected.
- Added Jade assembly/status information and a shared JEI/EMI Block Guide page.
- Villager Noise Switch recipe, model accents and internal pedestal now use an Emerald Block; the Iron Block identity is reserved for the Iron Farm Noise Switch.
- Fixed empty Farmer upgrades being treated as stateful when their source only carried structurally empty block-entity data, so clean Paddy/Rich Farmer items are stackable immediately from crafting and recipe transfer.
- NeoForge Paddy/Rich Paddy sneak-removal is now server-authoritative: Shift + Right Click is captured even if the client-side Easy Villagers delegate is one sync behind, preventing intermittent failures to extract planted Rice.

## 1.2.1 - 2026-08-21

### Crafting / recipe transfer hotfix

- Replaced the parallel Farmer display recipes with the real shaped Paddy, Rich Farmer and Rich Paddy recipes, so JEI/EMI recipe transfer uses the exact gameplay 3x3 pattern.
- Split Rich Farmer and Rich Paddy into their own authoritative shaped recipes while preserving the source Farmer block-entity data during upgrades.
- Verified the remaining addon crafting entries keep one authoritative transfer-friendly recipe: Cutter remains a real shaped recipe and Villager Noise Switch remains vanilla shaped crafting.
- Empty Paddy, Rich Farmer and Rich Paddy items now remain clean items and stack normally, including after being placed and mined again.
- Farmers carrying a Villager, crop, output inventory, Harvest Tool or other saved state serialize that state and are forced to stack size 1 so stored contents cannot be duplicated.
- Added one-time normalization for 1.2.0 Farmer items: empty legacy block-entity payloads are stripped, while legacy stateful items are locked to one.
- Easy Villagers' transient Farmer block-entity cache component is no longer copied into upgraded Easy FD Farmer items, preventing apparently empty machines from becoming non-stackable because of client-side representation data.
- Farmer inventory icons are now state-aware: a mined Farmer that contains a Villager/crop renders those stored contents instead of always looking empty.
- EMI 1.1.24 now uses a dedicated transfer path for Paddy/Rich Farmer/Rich Paddy upgrades instead of its generic component-strict crafting filler. The handler selects ingredients with Minecraft `Ingredient.test()` and moves the exact inventory stack through normal slot clicks, so Easy Villagers Farmers carrying synchronized block-entity components no longer make recipe transfer stop at the center slot.
- The EMI-only upgrade views use synthetic 3x3 crafting entries while the real gameplay recipes remain authoritative; JEI's normal crafting transfer is left untouched.

### Paddy rendering hotfix

- Paddy Farmer and Rich Paddy Farmer no longer force their entire frame/glass model onto the translucent render layer just to display water.
- The structural shell now uses the same cutout-style rendering approach as Easy Villagers/terrestrial Farmers, while the water surface is rendered separately with biome tint.
- Dynamic Paddy contents sample the brightest light available from the enclosure and its six neighbours, preventing a single adjacent wall or roof from turning the water/interior visually black.
- Paddy item icons use the same custom Farmer renderer, keeping the water/platform visible in inventory without forcing the placed block shell onto the translucent layer.

### Stateful machine-item consistency

- Empty Villager Noise Switch items now use their normal stack size; only switches carrying a stored Villager are forced to stack size 1 when mined.
- Removing the stored Villager before breaking a Noise Switch returns a clean, stackable item again.
- Villager Noise Switch inventory rendering now reconstructs its saved BlockEntity state so the stored Villager is visible without duplicating the Lever/Redstone presentation.
- Cutter inventory rendering now reconstructs saved machine state while preserving the established empty Cutter silhouette and stored log/Bamboo variant.

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
