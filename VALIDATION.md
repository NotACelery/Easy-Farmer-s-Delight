# Validation status — 1.0.0 release

Validation date: **2026-08-16**

## Architecture / packaging

- Java 21 / Minecraft 1.21.1 / NeoForge 21.1.235 baseline.
- Easy Villagers Farmer integration remains isolated behind `EasyVillagersFarmerAdapter`.
- Farmer's Delight configuration access remains isolated behind `FarmersDelightAdapter`.
- No Easy Villagers or Farmer's Delight implementation classes/assets are redistributed.
- Upgrade recipes preserve source ItemStack components / block-entity data.
- Compat BlockEntity owns persistence and synchronization.
- Jade is compile-only/optional and is not bundled.
- Argentum and Ars Nouveau tag entries are optional.

## Gameplay validation — passed

Validated during development and final release testing:

- Paddy Farmer, Rich Farmer and Rich Paddy Farmer placement/orientation/rendering;
- villager and crop visualization;
- Rice lower crop + panicle lifecycle and repeated harvest;
- mature Rice harvest timing;
- Rich Soil acceleration on terrestrial crops and Rice;
- Tomato persistent harvesting and independent Base / Rope 1 / Rope 2 progression;
- Red/Brown Mushroom Colony repeated harvesting;
- Farmer's Delight non-vanilla crops used during the test pass;
- Ars Nouveau Magebloom acceptance, harvest and Rich Soil acceleration;
- Jade crop/growth/Rich Soil/Tomato progress integration;
- output inventory automation and persistence during the multiplayer/server pass;
- full-output behavior: mature crops wait for enough capacity to hold the complete harvest instead of deleting remainder items.

## Multiplayer / server release pass

The final multiplayer/dedicated-server test pass was completed before promoting the project to **1.0.0**. The detailed `MULTIPLAYER-TEST-CHECKLIST.md` remains in the repository for future regression testing.

## Release note

The approximately 30% Rich Paddy advantage observed in one accelerated Rice comparison is not a guaranteed multiplier. Rich Soil uses Farmer's Delight configuration and RNG-driven work opportunities.
