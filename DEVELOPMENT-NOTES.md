# Development notes

## Compatibility boundary

Do not copy, port or subclass implementation code/assets from Easy Villagers.

Interop should prefer, in this order:

1. Minecraft/NeoForge public data components and registries.
2. Resource IDs/tags.
3. Public APIs/events where available.
4. Small runtime adapters only when necessary.

The upgrade recipes intentionally copy the center ItemStack's components and preserve its `BLOCK_ENTITY_DATA`. This keeps the Easy Villagers payload opaque until a feature actually needs a known field.

The Farmer integrations use one narrow reflection adapter for the Easy Villagers 1.21.1 Farmer surface: villager, crop, four output slots, output menu, item handler, Farmer speed and normal crop aging. Keep that boundary isolated in `integration/EasyVillagersFarmerAdapter.java`; addon-specific crop rules belong in our own block entity.

Do not invoke the unplaced Easy Villagers delegate's normal `tickServer()` or `setCrop()` path when it can trigger its own block-entity synchronization. Resolve/validate through Easy Villagers, mutate the stored state through the adapter, and let `CompatFarmerBlockEntity` own persistence and synchronization.

Farmer's Delight configuration access remains isolated in `integration/FarmersDelightAdapter.java`.

## Stable NBT owned by this addon

- `EfdcSchema`
- `EfdcPaddyGrowth`
- `EfdcBaseProgress`
- `EfdcRopeOneProgress`
- `EfdcRopeTwoProgress`
- `EfdcRopeCount`

Unknown fields must survive load/save and upgrade round trips.

## Crop rules

### Paddy Farmer

- Supports Farmer's Delight Rice only.
- Permanent virtual shallow-water environment; no bucket interaction state.
- Virtual progression `0..7`:
  - `0..3`: lower `farmersdelight:rice` age `0..3`;
  - `4..7`: upper `farmersdelight:rice_panicles` age `0..3`, with the lower crop remaining mature.
- Mature harvest uses the actual panicles loot table.
- After harvest, return to virtual stage `3`, preserving the mature lower Rice and regrowing only the upper section.
- Growth uses Easy Villagers' configured `farmSpeed` RNG.
- Once stage `7` is reached, harvesting occurs on the next one-second Farmer cadence without another `farmSpeed` RNG success. This prevents a visually mature Rice plant from stalling for multiple work rolls.
- Do **not** globally add Rice to `minecraft:villager_plantable_seeds`; the ordinary Easy Villagers Farmer does not implement the two-part Rice lifecycle.

### Rich Farmer

- Normal terrestrial crops are accepted only if Easy Villagers resolves/validates them.
- Generic crop growth/harvest uses Easy Villagers' internal crop-aging operation, preserving its `farmSpeed`, Farmer requirement, loot and four output slots.
- Rich Soil is virtual; there is no arbitrary flat multiplier.
- Read Farmer's Delight `richSoilBoostChance` live at runtime.
- Rich Soil gets an independent opportunity on the same 20-tick / `farmSpeed` cadence as Farmer work, then rolls the live Farmer's Delight chance.
- Do not reproduce the physical chunk-section `1/4096` block-selection roll inside the virtual Farmer. That old implementation made Rich variants practically indistinguishable from base Farmers.
- Respect `farmersdelight:unaffected_by_rich_soil`.
- Apply the crop's own Bone Meal age increment reflectively when available.
- World-dependent multi-block behavior that cannot exist literally inside a one-block Farmer is modeled by dedicated virtual engines.

#### Tomato

- Dedicated persistent Rich Farmer crop.
- `budding_tomatoes` age `0..3` transitions into `tomatoes` age `0..3`.
- Mature harvest returns the same Tomato section to age `0`; another seed is not consumed.
- Up to two Rope sections may be installed.
- Base / Rope 1 / Rope 2 persist independent progress and use independent `farmSpeed` work rolls.
- Render Rope-grown sections with Farmer's Delight's Tomato-on-Rope block instead of overlapping separate Rope and Tomato models.
- Sneak interaction removes the highest Rope before removing the crop selection.

#### Mushroom Colonies

- Rich Farmer accepts Red Mushroom and Brown Mushroom.
- Convert the selected item to Farmer's Delight's matching Mushroom Colony block state.
- Colony progresses through its normal age values.
- Mature colony harvest returns mushrooms to output, resets colony age and continues without consuming another mushroom.
- Rich Soil can accelerate the colony using its normal age/Bone Meal behavior.

### Rich Paddy Farmer

- Preserve Paddy state during upgrade.
- Use the Paddy Rice engine plus an independent Rich Soil opportunity.
- Farmer's Delight's live `richSoilBoostChance` controls each boost.
- Rice uses its real Bone Meal increment across the complete virtual `0..7` lifecycle, not only the lower crop.

## Rendering

- Compat Farmer blocks own a horizontal `FACING` state.
- Follow Easy Villagers' Farmer spatial convention: villager behind, crop in front, scaled inside the one-block enclosure.
- Render crops through their own baked model, RenderType and tint pipeline rather than generic `renderSingleBlock()` calls.
- Paddy variants have an internal water basin and iron retaining lip.
- Only Rich variants use the inset second glass shell.
- Rich Farmer uses Rich Soil flush with the internal floor.
- Models must define valid particle textures for block breaking.
- No Easy Villagers model or texture asset is copied.

## Optional integrations

### Argentum

Optional `minecraft:villager_plantable_seeds` entries:

- `argentum:yerba_semilla`
- `argentum:te_semilla`
- `argentum:batata`
- `argentum:membrillo_semilla`

All entries use `required: false`.

### Ars Nouveau

Optional seed-tag entry:

- `ars_nouveau:magebloom_crop`

Magebloom is a normal age-based crop for Easy Villagers purposes. The tag entry makes it available to the base Farmer; Rich Farmer naturally adds Rich Soil acceleration on top.

### Jade

- Jade remains optional and compile-only.
- `EfdcJadePlugin` registers server data for `CompatFarmerBlockEntity` and a client tooltip component for `CompatFarmerBlock`.
- If Jade is available only on the client, the provider can fall back to the compat BlockEntity state already synchronized for rendering.
- Tomato target format remains one line:

```text
Growth: Base 76% | Rope 1 53% | Rope 2 21%
```

- Rich variants also report `Rich Soil: Active`.

## Release-state rule

`0.1.0-dev` is feature-complete enough for the final compatibility pass, but should remain a development version until dedicated-server and multiplayer validation succeeds.

Do not introduce new gameplay features during the final test pass unless they fix a concrete compatibility, duplication, crash, persistence or UX problem found by testing.
