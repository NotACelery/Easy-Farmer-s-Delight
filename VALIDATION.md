# Validation status — 0.1.0-dev final singleplayer pass

Validation date: **2026-08-16**

## Source / architecture checks

- Java 21 / Minecraft 1.21.1 / NeoForge 21.1.235 baseline established.
- Easy Villagers Farmer integration remains isolated behind `EasyVillagersFarmerAdapter`.
- Farmer's Delight configuration access remains isolated behind `FarmersDelightAdapter`.
- No Easy Villagers or Farmer's Delight implementation classes/assets are redistributed by the addon.
- Upgrade recipes preserve source ItemStack components / block-entity data.
- Compat BlockEntity owns synchronization rather than invoking the unplaced Easy Villagers delegate sync cycle.
- Creative tab registration is implemented.
- Paddy/Rich recipe matchers use flat `CraftingInput` slots `0..8`.
- Farmer's Delight Rice IDs are `farmersdelight:rice` and `farmersdelight:rice_panicles`; stale `rice_crop` IDs are not part of the implementation.
- Rice is intentionally not added to the global villager seed tag.
- Argentum and Ars Nouveau tag entries are optional (`required: false`).
- Jade is compile-only/optional and is not bundled in the addon JAR.

## Live singleplayer validation — passed

The following has been exercised in a real modded client instance:

### Blocks / rendering

- Paddy Farmer, Rich Farmer and Rich Paddy Farmer place and render.
- Horizontal orientation matches Farmer-facing behavior.
- Villagers render inside the Farmer instead of overlapping the crop.
- Crops render inside the Farmer using their proper model/render pipeline.
- Paddy water renders with the intended tint.
- Only Rich variants use the inset second glass shell.
- Rich Farmer's internal Rich Soil floor is aligned correctly.
- Block-breaking particles no longer use missing-texture black/purple assets.
- Tomato-on-Rope visual sections stay within the Farmer enclosure.

### Farmer behavior

- Easy Villagers villager insertion works in compat Farmers.
- Normal compatible terrestrial crops grow and harvest in Rich Farmer.
- Farmer's Delight non-vanilla crops tested in the current pack are functional.
- Output generation is functional.

### Rice / Paddy

- Rice can be selected in Paddy and Rich Paddy.
- Lower Rice and upper panicle progression render correctly.
- Repeated harvest preserves the lower mature plant.
- A fully mature Rice plant is harvested on the next Farmer cadence without waiting for another `farmSpeed` RNG success.
- Rich Soil acceleration applies to the complete Rice virtual lifecycle rather than only the lower crop.
- An accelerated `tick rate 2000` comparison observed roughly 30% more Rice harvests from Rich Paddy than Paddy during the tested interval. This remains RNG/config dependent rather than a fixed promised multiplier.

### Rich Soil

- Rich Soil acceleration is observable on Rich Farmer terrestrial crops.
- Rich Soil acceleration is observable on Rich Paddy Rice.
- Tomato / Rope sections no longer remain permanently synchronized simply because the Rope items were installed together.

### Tomato

- Tomato Seeds are accepted only by Rich Farmer's dedicated Tomato flow.
- Budding -> persistent Tomato transition works.
- Mature Tomato harvesting preserves the plant.
- Base, Rope 1 and Rope 2 have independent progress/work behavior.
- Rope install/remove flow works.

### Mushroom Colonies

- Red Mushroom is accepted by Rich Farmer and runs the colony lifecycle.
- Brown Mushroom is accepted by Rich Farmer and runs the colony lifecycle.
- Colonies harvest repeatedly without consuming another mushroom.
- Rich Soil acceleration remains compatible with colony progression.

### Ars Nouveau

- Magebloom is accepted through the optional `villager_plantable_seeds` tag entry.
- Magebloom grows/harvests successfully.
- Rich Farmer applies Rich Soil acceleration to Magebloom.

### Jade

- Jade integration loads successfully when Jade is installed.
- Crop name is displayed.
- Growth percentage is displayed.
- Rich variants display Rich Soil active state.
- Tomato Base / Rope 1 / Rope 2 progress is displayed on one line.
- Existing Farmer output/inventory information can coexist with the addon tooltip.

## Pending release validation

These are the remaining release gates, not known failures:

- Dedicated NeoForge server startup with required dependencies.
- Multiplayer client/server synchronization.
- Two-player simultaneous interaction with the same Farmer.
- Chunk unload/reload persistence.
- Full dedicated-server restart persistence.
- Hopper/item-handler extraction under server conditions.
- Full Farmer -> Rich Farmer and Paddy -> Rich Paddy upgrade data round trip under server conditions.
- Baby villager aging/state persistence under server conditions.
- Optional-mod matrix:
  - no Jade / Argentum / Ars Nouveau;
  - Jade client-only where supported by the fallback path;
  - Jade on client + server;
  - Argentum present;
  - Ars Nouveau present.
- JEI display of Paddy and Rich upgrade recipes.
- EMI display of Paddy and Rich upgrade recipes.
- Recipe lookup/transfer behavior if either viewer supports the custom recipe serializers.
- Final balance and smoke test after multiplayer fixes, if any.

Use `MULTIPLAYER-TEST-CHECKLIST.md` for the executable test plan.
