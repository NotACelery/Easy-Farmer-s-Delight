# Development notes

## Compatibility boundary

Do not copy, port or redistribute implementation code/assets from Easy Villagers or Farmer's Delight.

Interop preference:

1. Minecraft/NeoForge public data components and registries.
2. Resource IDs/tags.
3. Public APIs/events where available.
4. Small runtime adapters only when necessary.

The Farmer integration remains isolated in `integration/EasyVillagersFarmerAdapter.java`. Farmer's Delight configuration access remains isolated in `integration/FarmersDelightAdapter.java`. Addon-specific crop rules belong in `CompatFarmerBlockEntity`.

## Stable addon NBT

- `EfdcSchema`
- `EfdcPaddyGrowth`
- `EfdcBaseProgress`
- `EfdcRopeOneProgress`
- `EfdcRopeTwoProgress`
- `EfdcRopeCount`

Unknown fields must survive load/save and upgrade round trips.

## Crop rules

### Paddy Farmer

- Farmer's Delight Rice only.
- Virtual progression `0..7`: lower Rice `0..3`, then panicles `0..3`.
- Mature harvest uses the real panicles loot table.
- After harvest, return to virtual stage `3` so the lower mature Rice stays planted.
- Once fully mature, harvest on the next Farmer cadence without another `farmSpeed` RNG roll.
- Never globally add Rice to `minecraft:villager_plantable_seeds`.

### Rich Farmer

- Generic terrestrial crops are resolved/validated by Easy Villagers.
- Rich Soil receives an independent `farmSpeed`-scaled opportunity and then uses Farmer's Delight's live `richSoilBoostChance`.
- Respect `farmersdelight:unaffected_by_rich_soil`.
- Use compatible crops' real Bone Meal age increments where possible.

#### Tomato

- Persistent `budding_tomatoes` -> `tomatoes` lifecycle.
- Base / Rope 1 / Rope 2 store independent progress and use independent work rolls.
- Up to two Rope sections.
- Render Rope-grown sections using Farmer's Delight Tomato-on-Rope state.

#### Mushroom Colonies

- Red/Brown Mushroom only through Rich Farmer special handling.
- Convert to matching Farmer's Delight colony state.
- Mature harvest resets colony age without consuming another mushroom.
- Rich Soil may accelerate colony growth.

### Rich Paddy Farmer

- Paddy Rice engine plus Rich Soil opportunity.
- Rich Soil applies across the complete virtual `0..7` Rice lifecycle.

## Lossless harvest rule

Before any compat-managed mature crop is harvested, simulate insertion of the **complete generated harvest** into a copy of the four output slots.

- If every generated stack fits, commit the harvest and reset/regrow the crop as appropriate.
- If any remainder would be lost, keep the crop mature and retry later.
- This rule applies to Rice, Tomato sections, Mushroom Colonies and generic Rich Farmer harvests managed by the compat engine.

Never reset a mature crop after silently discarding output remainder.

## Optional integrations

### Argentum

Optional `minecraft:villager_plantable_seeds` entries:

- `argentum:yerba_semilla`
- `argentum:te_semilla`
- `argentum:batata`
- `argentum:membrillo_semilla`

### Ars Nouveau

Optional entry: `ars_nouveau:magebloom_crop`.

### Jade

Jade remains compile-only and optional. The provider exposes selected crop, growth, Rich Soil status and Tomato Base/Rope progress.

## Release state

**1.0.0 is the first stable public release.**

After 1.0.0, prefer compatibility fixes, bug fixes and explicitly scoped enhancements. Preserve registry IDs and addon NBT compatibility whenever possible.
