# Validation status — 1.2.0 integration candidate

Validation basis: Minecraft **1.21.1**, NeoForge **21.1.235**, Java **21**.

The gameplay base was committed before the final JEI/EMI viewer-completion pass. This file separates already confirmed gameplay from source-implemented behavior that still needs a local build/launch regression.

## Confirmed in-game

- [x] Paddy / Rich Paddy final geometry
- [x] villager support and Sugar Cane Sand remain inside the enclosure and flush with the waterline
- [x] Paddy / Rich Paddy sneak-use dismantling
- [x] Sugar Cane revised virtual growth cadence feels comparable to normal farming
- [x] Melon/Pumpkin stem + fruit 1/3 / 2/3 layout
- [x] vanilla attached/curved stem render when fruit is ready
- [x] Cutter stays at 0% with missing/wrong required tool
- [x] Cutter no longer loops failed 0→100→0 processing
- [x] Jade `Waiting for ...`
- [x] Jade `Wrong tool: ... required`
- [x] Harvest Tool / Cutting Tool rotating icon-only tooltips
- [x] Villager Noise Switch basic local toggle behavior
- [x] Melon Silk Touch behavior

## Source-implemented; focused gameplay regression still required

### Mature harvest cadence

- [ ] mature normal crop harvests within the next one-second Farmer cadence
- [ ] mature Tomato base section harvests within the next one-second cadence
- [ ] mature Rope 1 / Rope 2 section harvests without another `farmSpeed` RNG roll
- [ ] mature Mushroom Colony + valid Knife harvests within the next one-second cadence
- [ ] blocked mature crop remains mature without consuming resources

### Harvest Tool routing

- [ ] normal crop without Hoe
- [ ] normal crop with Fortune Hoe
- [ ] Hoe is not damaged by crop harvesting
- [ ] Rice without Knife
- [ ] Rice with Knife
- [ ] Rice does not accidentally use Hoe/Axe enchants
- [ ] Mushroom Colony waits for Knife
- [ ] Mushroom Knife is not damaged
- [ ] Melon/Pumpkin wait for Axe
- [ ] Fortune/Silk Touch remain delegated to real block loot
- [ ] Melon/Pumpkin Axe durability only after successful harvest
- [ ] Unbreaking behaves normally
- [ ] full output prevents harvest and tool damage
- [ ] legacy `EfdcKnife` migrates into `EfdcHarvestTool`

### Melon / Pumpkin timing

- [ ] at normal tick rate, Rich Soil is an occasional stem-only advantage rather than a farmSpeed-coupled second growth engine
- [ ] `randomTickSpeed 0` disables the virtual Rich Soil stem bonus while ordinary Farmer growth still proceeds
- [ ] mature stem fruit generation is not Rich Soil accelerated
- [ ] Pumpkin timing matches the Melon model

### Sugar Cane persistence

- [ ] save/reload preserves Sand, height and internal age
- [ ] chunk/dimension unload/reload preserves state
- [ ] Rich Paddy and Paddy have the same Sugar Cane speed

### Cutter

- [ ] Knife Cutting recipe
- [ ] Axe-compatible Cutting recipe
- [ ] stripping fallback
- [ ] scraping fallback
- [ ] wax-off fallback
- [ ] correct tool starts the 10-tick serial process
- [ ] full outputs block operation atomically
- [ ] tool durability happens only after success
- [ ] mixed input queue does not falsely report the whole Cutter blocked when the current tool can process at least one entry
- [ ] changing tool/input/output invalidates standby cache
- [ ] Jade inspection does not alter Cutting RNG

### Villager Noise Switch

- [ ] no-Villager localized failure
- [ ] preference survives Minecraft restart
- [ ] preference survives world/server/dimension changes
- [ ] breaking every Noise Switch does not clear the preference
- [ ] two clients can hold opposite preferences for the same block
- [ ] Jade shows each client's local state
- [ ] Observer does not react
- [ ] Comparator output remains zero
- [ ] no neighbor update / real Redstone signal
- [ ] hostile/player/environment sounds remain unaffected

## Jade viewer regression

- [ ] generic mature `Ready to harvest`
- [ ] Melon/Pumpkin `Stem Growth`, `Fruit: Growing`, `Fruit: Ready`
- [ ] Rich Soil `Stem only`
- [ ] Sugar Cane height + internal next-segment percentage
- [ ] Noise Switch enabled / muted / missing-villager states
- [ ] multiplayer-local Noise Switch state

Cutter `Output full` is deliberately not a Jade line in 1.2.0 until a deterministic non-RNG check can be guaranteed for chance-result Cutting recipes.

## JEI / EMI source implementation

Implemented in source:

- [x] shared `ToolUse`
- [x] expanded `FarmerHarvestInfo`
- [x] `GuideIngredient`
- [x] `BlockGuideInfo`
- [x] shared `RecipeViewerData`
- [x] three Harvest Tool + two Paddy + five Rich Farmer viewer entries
- [x] nine contextual Block Guide entries
- [x] JEI Block Guide category
- [x] JEI catalysts
- [x] EMI Block Guide recipe/category
- [x] EMI Block Guide per-block source routing (no Block Guide workstations)
- [x] wrapped EMI guide text
- [x] optional/required tool metadata
- [x] durability metadata
- [x] real-loot / illustrative-output metadata
- [x] Cutter remains registered as a Farmer's Delight Cutting catalyst/workstation in the integration code
- [x] Cutter Axe Actions reduced to one two-row summary page in JEI/EMI, with grouped copper/log semantics and dynamic modded stripping entries

### Required Farmer guide scoping check

- [ ] Paddy Farmer shows Paddy Farmer Harvesting only
- [ ] Rich Paddy Farmer shows Paddy Farmer Harvesting + Farmer Harvest Tools
- [ ] Rich Farmer shows Rich Farmer Harvesting + Farmer Harvest Tools
- [ ] Paddy pages contain Rice and Sugar Cane only
- [ ] Rich Farmer pages contain normal crops, Tomato/Rope, Mushroom Colonies, Melon and Pumpkin only
- [ ] Mushroom page clearly says a mature Colony requires a Knife
- [ ] viewer text is casual/player-facing and does not expose virtual age or loot-table implementation language

### Required viewer launch matrix

- [ ] JEI only
- [ ] EMI only
- [ ] JEI + EMI
- [ ] neither JEI nor EMI
- [ ] Jade + JEI
- [ ] Jade + EMI
- [ ] dedicated server without client-only viewer mods

### Required Cutter Axe Actions check

- [ ] JEI shows exactly one Cutter Axe Actions page
- [ ] EMI shows exactly one Cutter Axe Actions page
- [ ] copper row tooltip says `Any waxed/oxidized copper item` (localized equivalent outside English)
- [ ] log row tooltip says `Any compatible log` (localized equivalent outside English)
- [ ] eight vanilla logs + Bamboo Block rotate through the log input
- [ ] corresponding stripped outputs rotate in the same order
- [ ] compatible modded logs such as Ars Nouveau woods appear when installed

### Required Block Guide page check

- [ ] Paddy Farmer — Rice
- [ ] Paddy Farmer — Sugar Cane
- [ ] Rich Farmer — Normal Crops
- [ ] Rich Farmer — Tomatoes & Rope
- [ ] Rich Farmer — Mushroom Colonies
- [ ] Rich Farmer — Melon
- [ ] Rich Farmer — Pumpkin
- [ ] Cutter
- [ ] Villager Noise Switch

### Required discoverability check

EMI Block Guide navigation:

- [ ] Paddy Farmer **Recipes** shows `Crafting | Block Guide`
- [ ] Rich Farmer **Recipes** shows `Crafting | Block Guide`
- [ ] Rich Paddy Farmer **Recipes** shows `Crafting | Block Guide`
- [ ] Cutter **Recipes** shows `Crafting | Block Guide`
- [ ] Villager Noise Switch **Recipes** shows `Crafting | Block Guide`
- [ ] right-click **Uses** on those five blocks does not surface Block Guide merely as a workstation/catalyst
- [ ] right-click **Uses** on Sand, Sugar Cane, Rope, Melon Seeds, Pumpkin Seeds, Knife, Hoe or Axe does not surface Block Guide pages

JEI keeps its normal catalyst/focus discoverability checks for the same guide data.

## Resource / localization integrity

Before publication:

- [ ] all JSON parses without duplicate keys
- [ ] all nine locale files have identical key sets
- [ ] no raw translation key appears in Jade/JEI/EMI
- [ ] long Spanish Block Guide text remains inside JEI/EMI bounds
- [ ] all Farmer Harvest and Block Guide IDs are unique

## Build gate

- [ ] `build-dev.bat` succeeds from the committed 1.2.0 gameplay baseline + viewer-completion patch
- [ ] final JAR launches
- [ ] no optional-integration `ClassNotFoundException`
- [ ] no dedicated-server client-class loading crash

Only after the unchecked release-gate items above pass should 1.2.0 be promoted from integration candidate to stable/public.
