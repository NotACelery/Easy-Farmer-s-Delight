# Easy Farmer's Delight Compat

<p align="center">
  <img src="easy-farmers-delight-compat-cover.webp" alt="Easy Farmer's Delight Compat" width="320">
</p>

Independent, unofficial compatibility addon for **Easy Villagers** and **Farmer's Delight** on **Minecraft 1.21.1 / NeoForge**.

> This project is not affiliated with, endorsed by, sponsored by, or maintained by the authors of Easy Villagers, Farmer's Delight, Jade, JEI, EMI, Argentum or Ars Nouveau. It does not redistribute their code or assets.

## 1.2.0 integration candidate

Version 1.2.0 expands the Farmer family with generalized Harvest Tools, virtual Melon/Pumpkin and Sugar Cane farming, the Villager Noise Switch, richer Jade diagnostics, and matching in-game documentation for JEI and EMI.

The gameplay foundation is built on the validated 1.0/1.1 Farmer/Cutter behavior. The viewer layer is source-complete in this candidate but still requires the local JEI-only / EMI-only / combined regression pass before publication.

## Farmer family

| Farmer | Supported behavior |
| --- | --- |
| Easy Villagers Farmer | Existing compatible crops. Optional tagged compatibility remains available for supported addons. |
| Paddy Farmer | Farmer's Delight Rice plus virtual Sugar Cane farming after installing Sand. |
| Rich Farmer | Normal crops with Rich Soil behavior, Tomato + up to 2 Rope sections, Mushroom Colonies, virtual Melon/Pumpkin, and a protected Harvest Tool slot. |
| Rich Paddy Farmer | Paddy behavior plus Rich Soil where applicable and the same protected Harvest Tool slot. Rich Soil intentionally does **not** accelerate Sugar Cane. |

## Harvest Tools

Rich Farmer and Rich Paddy Farmer accept:

- Knife (`#c:tools/knife`)
- Hoe (`#minecraft:hoes`)
- Axe (`#minecraft:axes`)

The accepted tool categories are broader than the tool used by any individual crop:

- normal crops / Tomato: optional Hoe, forwarding Fortune where their real loot table supports it;
- Rice: optional Knife;
- mature Mushroom Colonies: Knife required;
- ready Melon/Pumpkin fruit: Axe required;
- Sugar Cane: no tool.

Normal crop, Rice and Mushroom harvesting do not receive artificial tool durability loss. Melon/Pumpkin use the real Axe as the loot tool and damage it only after a successful harvest.

Existing worlds migrate Harvest Tool data from the legacy `EfdcKnife` key into `EfdcHarvestTool`.

The empty Harvest Tool slot uses an icon-only rotating tooltip. Rich Farmers show Knife/Hoe/Axe; Cutter shows Knife/Axe.

## Paddy Farmer

The Paddy Farmer preserves the Easy Villagers Farmer payload while adding crop behavior that requires an aquatic lifecycle.

### Rice

Rice is modeled as a complete virtual lifecycle: submerged lower Rice followed by upper panicles. Mature lower Rice remains after panicle harvest, and a fully mature crop attempts harvest on the next one-second Farmer cadence instead of waiting for another `farmSpeed` RNG success.

### Sugar Cane

Right-click an empty Paddy/Rich Paddy with Sand, then plant Sugar Cane.

Sugar Cane keeps a virtual height plus internal `0..15` age. Each successful Farmer growth event advances the age; a new section appears after the age cycle completes. At height 3, the upper two sections are harvested while the base remains.

Rich Soil does not accelerate Sugar Cane.

Sneak-use dismantles the installed Paddy content. Sugar Cane mode returns its Sand and represented Cane; Rice mode returns the installed crop.

The villager support and Sugar Cane Sand are visual islands submerged to the same waterline inside the Paddy enclosure.

## Rich Farmer

### Rich Soil

Rich Soil uses Farmer's Delight semantics where applicable. Normal crops retain the established Rich Farmer behavior. Melon/Pumpkin stems use a separated occasional Rich Soil boost; once the stem is mature, fruit generation runs at normal Farmer speed.

### Tomato + Rope

Tomato remains a persistent crop. Up to two Rope extensions can be installed, with Base / Rope 1 / Rope 2 progression tracked independently. A Fortune Hoe is optional.

### Mushroom Colonies

Red and Brown Mushrooms become their matching Farmer's Delight Mushroom Colony. Colonies can finish growing without a Knife, but mature harvesting waits for a Knife. Successful harvest produces the colony's configured mushrooms and resets the colony without damaging the Knife.

### Melon / Pumpkin

Melon Seeds and Pumpkin Seeds use a virtual stem-and-fruit lifecycle.

- stem grows from age 0 to 7;
- Rich Soil may accelerate the stem only;
- fruit generation begins after the stem reaches 7 and is not Rich Soil accelerated;
- ready fruit waits for an Axe;
- real block loot handles Silk Touch/Fortune behavior;
- the Axe is damaged only after successful harvest;
- full outputs leave the fruit ready and do not damage the tool.

The renderer places stem and fruit at the 1/3 and 2/3 crop-field positions and uses the vanilla attached-stem state when fruit is ready.

## Cutter

The Cutter automates Farmer's Delight Cutting Board recipes and Axe block actions.

- 4 input slots
- 1 protected Knife/Axe Cutting Tool slot
- 4 output slots
- one stored adult Villager
- 10-tick serial processing
- Farmer's Delight Cutting recipes first
- Axe fallback: strip, scrape, wax-off
- full output simulation before consuming input or damaging a tool

A Cutter with a missing or wrong required tool remains at 0% standby. It does not loop through failed processing attempts.

Cutter remains registered as a Farmer's Delight Cutting catalyst/workstation in supported recipe viewers.

## Villager Noise Switch

Recipe:

```text
G G G
G L G
R I R
```

- `G` = Glass Pane
- `L` = Lever
- `R` = Redstone Block
- `I` = Iron Block

Insert an Easy Villagers Villager, then right-click the block to toggle Villager vocalizations for your **local client**.

The preference is global to that Minecraft instance and persists independently of worlds, servers and dimensions. The Lever and Redstone floor are visual only: toggling does not alter server BlockState, emit Redstone, update neighbors or create Observer behavior.

## Jade

Optional Jade integration reports the real machine state, including:

- selected crop and growth;
- Rich Soil state;
- Sugar Cane mode, height and internal segment progress;
- Tomato Base / Rope 1 / Rope 2 progress;
- Melon/Pumpkin stem / fruit phase;
- `Ready to harvest` for relevant mature states;
- `Waiting for Knife/Axe` when a required tool is missing;
- Cutter `Wrong tool` diagnostics and processing progress;
- equipped Harvest/Cutting Tool;
- Cutter outputs;
- local Villager Noise Switch enabled/muted state.

Jade inspection uses non-destructive machine probes and never rolls Cutting recipe outputs.

## JEI / EMI

JEI and EMI are optional. Both read the same viewer-neutral data model.

### Farmer harvesting guides

Farmer documentation is deliberately split by machine instead of putting every crop into one global category:

- **Farmer Harvest Tools** — 3 short pages explaining Knife, Hoe and Axe. This category is only attached to Rich Farmer and Rich Paddy Farmer because those are the variants with a Harvest Tool slot.
- **Paddy Farmer Harvesting** — 2 pages for Rice and Sugar Cane, shared by Paddy Farmer and Rich Paddy Farmer. Rice explains the Rich Paddy Knife bonus; Sugar Cane explains the Sand setup and its lack of Rich Soil acceleration.
- **Rich Farmer Harvesting** — 5 pages for normal crops, Tomato/Rope, Mushroom Colonies, Melon and Pumpkin.

The wording is player-facing: pages explain what to insert, whether a tool is needed, useful enchantments and when durability is consumed without exposing internal state names or implementation details.

### Block Guide

Both viewers expose the same nine contextual guide pages:

1. Paddy Farmer — Rice
2. Paddy Farmer — Sugar Cane
3. Rich Farmer — Normal Crops
4. Rich Farmer — Tomatoes & Rope
5. Rich Farmer — Mushroom Colonies
6. Rich Farmer — Melon
7. Rich Farmer — Pumpkin
8. Cutter
9. Villager Noise Switch

The guide uses actual ingredients/tags so correctly tagged modded Knives/Hoes/Axes participate automatically. Harvest Tools have their own shared viewer category instead of being duplicated as a Block Guide page.

In EMI, Block Guide is contextual to the documented machine: opening **Recipes** for Paddy Farmer, Rich Farmer, Rich Paddy Farmer, Cutter or Villager Noise Switch exposes a **Block Guide** category beside normal crafting categories. Guide ingredients are presentation-only for lookup purposes, so they do not make Block Guide appear through unrelated **Uses** searches.

## Optional crop compatibility

Optional data-pack/tag integrations remain available for configured addons such as Argentum and Ars Nouveau without making them mandatory dependencies.

## Rendering

Compat Farmers, Cutter and Noise Switch use addon-owned models/rendering. Easy Villagers and Farmer's Delight code/assets are not redistributed; installed dependency resources are referenced at runtime where appropriate.

## Dependencies

Required:

- Minecraft **1.21.1**
- NeoForge **21.1.235+**
- Easy Villagers **1.1.42+**
- Farmer's Delight **1.2.9+**

Optional:

- Jade
- JEI
- EMI
- Argentum
- Ars Nouveau

## Installation

Install Easy Farmer's Delight Compat on both client and server together with Easy Villagers and Farmer's Delight. JEI, EMI and Jade remain optional client-facing integrations.

## Building

Java 21 + NeoForge ModDevGradle.

Windows:

```text
build-dev.bat
```

Linux/WSL:

```text
./build-dev.sh
```

The resulting JAR is written to `build/libs/`. Optional integrations are compile-only and are not bundled into the final mod JAR.

## License

Copyright © 2026 Celerbi. All rights reserved.
