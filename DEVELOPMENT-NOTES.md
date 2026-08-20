# Development notes — 1.2.0

## Compatibility boundary

Do not copy, port or redistribute implementation code/assets from Easy Villagers or Farmer's Delight.

Interop preference:

1. Minecraft/NeoForge public data/components/registries.
2. Resource IDs and tags.
3. Public APIs/events.
4. Small runtime adapters only where necessary.

Easy Villagers Farmer access stays isolated in `integration/EasyVillagersFarmerAdapter.java`.
Farmer's Delight configuration access stays isolated in `integration/FarmersDelightAdapter.java`.
Crop-specific behavior belongs in `CompatFarmerBlockEntity`.

## Stable addon NBT

Current owned keys include:

- `EfdcSchema`
- `EfdcPaddyGrowth`
- `EfdcBaseProgress`
- `EfdcRopeOneProgress`
- `EfdcRopeTwoProgress`
- `EfdcRopeCount`
- `EfdcHarvestTool`
- `EfdcFruitReady`
- `EfdcPaddySand`
- `EfdcSugarCaneHeight`
- `EfdcSugarCaneAge`

Harvest Tool read fallback:

```text
EfdcHarvestTool
-> EfdcKnife
```

Save only the current key. Unknown Easy Villagers payload data must survive round trips.

## Harvest Tool policy

Accepted categories:

```text
Harvest Tool = Knife + Hoe + Axe
Cutting Tool = Knife + Axe
```

Gameplay routing is crop-specific:

```text
normal crops / Tomato -> optional Hoe
Rice                  -> optional Knife
Mushroom Colony       -> required Knife
Melon / Pumpkin       -> required Axe
Sugar Cane            -> none
```

Do not pass an arbitrary equipped tool into generic crop loot: that would allow Fortune Axe/Knife leakage.

Tool acceptance and UI/tooltips must derive from `FarmerToolSupport`.

## Mature harvest policy

Growth and harvest are separate phases.

Once a compat-managed crop is mature, it must not require another `farmSpeed` success merely to attempt harvesting.

Normal crops, Tomato sections and Mushroom Colonies attempt mature harvest on the next one-second Farmer cadence. Required-tool/output/villager blockers keep the crop mature in standby.

## Paddy

### Rice

Virtual progression:

```text
lower Rice 0..3
panicles 0..3
mature harvest
return to mature lower-Rice stage
```

Mature panicle harvest uses real loot and preserves the lower plant.

### Sugar Cane

State:

```text
Sand installed
Sugar Cane height 0..3
Sugar Cane internal age 0..15
```

A Farmer growth success advances internal age. Completing age 15 creates the next section. Height 3 harvests the upper two and returns to height 1.

Rich Soil never accelerates Sugar Cane.

Sneak-use dismantling is prioritized in the block interaction path and returns installed state losslessly.

## Rich Farmer special crops

### Tomato

Base / Rope 1 / Rope 2 progress independently. Hoe is optional for compatible Fortune loot.

### Mushroom Colonies

Mature Colony requires Knife. Knife is not damaged. A mature blocked Colony stays ready instead of continuing growth RNG.

### Melon / Pumpkin

Virtual stem 0..7 followed by separate fruit generation.

Rich Soil only affects the stem phase. Fruit generation remains normal Farmer speed. Ready fruit requires Axe and uses the real block loot table. Tool damage occurs only after successful output commit.

Renderer locations: stem center = 1/3 crop width; fruit center = 2/3. Ready fruit uses vanilla attached-stem state.

## Cutter

Processing may start only when at least one queued input is processable with the currently equipped Cutting Tool.

Inspection uses the non-RNG `CutterOperationProbe`; never call Cutting `rollResults()` merely to decide if a tool is valid.

Missing/wrong required tool means:

```text
progress = 0
standby
```

Processability cache must invalidate when relevant tool/input/output/villager state changes.

Output simulation remains transactional.

## Villager Noise Switch

`villagersMuted` is a client-local global preference.

Never encode the local ON/OFF state in server BlockState or BlockEntity NBT.

Visual Lever/Redstone is renderer-only. There must be no redstone signal, comparator output, neighbor update, Observer-detectable state, or cross-player synchronization of the preference.

Persist immediately on successful toggle.

## Jade

Jade may read server machine state but must not mutate it.

Noise Switch mute display reads client preference locally; server data only answers physical facts such as whether the switch contains a Villager.

Cutter diagnostics use non-RNG operation probes.

Keep the historical Jade provider UID `farmer_knife` for Harvest Tool preference compatibility. The old `FarmerKnifeJadeProvider` implementation is obsolete.

## JEI / EMI

Shared viewer model:

- `ToolUse`
- `FarmerHarvestInfo`
- `GuideIngredient`
- `BlockGuideInfo`
- `RecipeViewerData`

JEI and EMI are rendering adapters only. They must not maintain separate gameplay or guide rules.

Farmer Harvesting has 7 documentation entries.
Block Guide has 10 pages.

Documentation recipes intentionally do not represent deterministic crafting where real loot/RNG is authoritative.

Cutter remains a Farmer's Delight Cutting catalyst/workstation.

## Optional viewer safety

JEI/EMI/Jade are optional.

Final validation must launch:

- JEI only
- EMI only
- JEI + EMI
- neither

No optional integration class may cause dedicated-server classloading failures when the corresponding mod is absent.

## Localization

All viewer/Jade text must maintain exact key parity across:

- `en_us`
- `en_gb`
- `en_au`
- `en_ca`
- `en_nz`
- `es_cl`
- `es_ar`
- `es_es`
- `es_mx`
