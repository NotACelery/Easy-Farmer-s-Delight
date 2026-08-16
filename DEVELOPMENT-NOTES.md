# Development notes

## Compatibility boundary

Do not copy, port or subclass implementation code/assets from Easy Villagers.

Interop should prefer, in this order:

1. Minecraft/NeoForge public data components and registries.
2. Resource IDs/tags.
3. Public APIs/events where available.
4. Small runtime adapters only when necessary.

The upgrade recipes intentionally copy the center ItemStack's components and preserve its `BLOCK_ENTITY_DATA`. This keeps the Easy Villagers payload opaque to us until a feature actually needs to interpret a known field.

The current Farmer integrations use one narrow reflection adapter for the Easy Villagers 1.21.1 Farmer surface (villager, crop, four output slots, output menu, item handler, Farmer speed and normal crop aging). It must remain isolated in `integration/EasyVillagersFarmerAdapter.java`; addon crop rules belong in our own block entity.

Do not invoke the unplaced Easy Villagers delegate's normal `tickServer()` or `setCrop()` methods when they can trigger its own block-entity sync packets. Resolve/validate through Easy Villagers, then mutate the delegate's stored state through the adapter and let the compat block entity own persistence/synchronization.

Farmer's Delight configuration access is isolated in `integration/FarmersDelightAdapter.java`.

## Stable NBT owned by this addon

- `EfdcSchema`
- `EfdcPaddyGrowth`
- `EfdcBaseProgress`
- `EfdcRopeOneProgress`
- `EfdcRopeTwoProgress`
- `EfdcRopeCount`

Unknown fields must survive load/save.

## Crop rules

### Paddy Farmer — implemented engine

- Farmer's Delight Rice only.
- Permanent virtual shallow-water environment; no bucket interaction state.
- Virtual progression `0..7`:
  - `0..3`: lower `farmersdelight:rice` age `0..3`;
  - `4..7`: upper `farmersdelight:rice_panicles` age `0..3`, while the lower crop remains mature.
- On mature harvest, use the actual panicles block loot table.
- After harvest, return to virtual stage `3` so the lower Rice stays planted and only the panicles regrow.
- Do **not** globally add Rice to `minecraft:villager_plantable_seeds`; the ordinary Easy Villagers Farmer would otherwise use its incompatible generic single-age crop engine.
- Preserve Easy Villagers `Villager`, `Crop` and `Items` NBT through the runtime adapter.
- GUI and item-capability output mutations must mark the owning compat block entity dirty.

### Rich Farmer — base engine implemented

- Normal terrestrial seeds are accepted only if Easy Villagers' own `getSeedCrop`/seed validation accepts them.
- Normal crop growth/harvest uses Easy Villagers' internal `ageCrop` operation, preserving its configured `farmSpeed`, age-property behavior, mature Farmer-villager requirement, loot and four output slots.
- The delegate is not allowed to run its own `tickServer()`/sync cycle because it is not actually placed in the world.
- Rich Soil is virtual and does not invent a flat speed multiplier.
- Read Farmer's Delight `richSoilBoostChance` live at runtime.
- Respect `randomTickSpeed`; each random-tick draw independently has the normal one-position-in-a-16³-section selection chance for this virtual Rich Soil.
- Respect `farmersdelight:unaffected_by_rich_soil` by resource tag, without importing Farmer's Delight implementation constants.
- For `CropBlock` subclasses, apply their own protected bone-meal age increment reflectively and cap at their real max age.
- World-dependent bonemeal hooks cannot be reproduced literally for an unplaced virtual crop; special non-`CropBlock` crops are handled by dedicated engines rather than guessed here.
- Tomato persistent vine is a dedicated Rich Farmer engine: `budding_tomatoes` age `0..3` transitions to `tomatoes` age `0..3`; harvest returns the mature vine to age 0 instead of consuming another seed.
- Up to two `farmersdelight:rope` items may be installed. Base/Rope 1/Rope 2 persist their own progress values; sneak interaction removes the topmost Rope before removing the crop.
- Red/Brown Mushroom Colonies: next Rich Farmer phase.

### Rich Paddy Farmer — planned combined behavior

- The block and upgrade path exist and preserve Paddy state.
- Current aquatic engine is the Paddy Rice engine.
- Rich Soil acceleration for Rice is intentionally deferred to the dedicated Rich Paddy phase.

## Optional integrations

### Argentum

Do not require the mod at runtime. Optional tag entries currently add:
- `argentum:yerba_semilla`
- `argentum:te_semilla`
- `argentum:batata`
- `argentum:membrillo_semilla`

### Jade

Provider will be registered only when Jade is present.
Tomato target display:
`Growth: Base 76% | Rope 1 53% | Rope 2 21%`
