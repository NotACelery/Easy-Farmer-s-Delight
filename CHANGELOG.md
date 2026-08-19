# Changelog

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
