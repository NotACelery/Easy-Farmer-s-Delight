# Easy Farmer's Delight Compat

<p align="center">
  <img src="easy-farmers-delight-compat-cover.webp" alt="Easy Farmer's Delight Compat" width="320">
</p>

Independent, unofficial compatibility addon for **Easy Villagers** and **Farmer's Delight** on **Minecraft 1.21.1 / NeoForge**.

> This project is not affiliated with, endorsed by, sponsored by, or maintained by the authors of Easy Villagers, Farmer's Delight, Jade, Argentum or Ars Nouveau. It does not redistribute their code or assets.

## Release — 1.0.0

The initial feature set is complete and has passed the final gameplay and multiplayer test pass.

### Farmer family

| Farmer | Supported crops / behavior |
| --- | --- |
| Easy Villagers Farmer | Existing vanilla-compatible crops, Cabbage, Onion and compatible tagged crops. This addon also adds optional Argentum crops and Ars Nouveau Magebloom to `minecraft:villager_plantable_seeds`. |
| Paddy Farmer | Farmer's Delight Rice using its complete lower-rice + panicle lifecycle. |
| Rich Farmer | Everything accepted by the normal Farmer, accelerated by Rich Soil, plus persistent Tomato + up to 2 Rope sections and Red/Brown Mushroom Colonies. |
| Rich Paddy Farmer | Rice with the Paddy lifecycle plus Rich Soil acceleration across the complete virtual Rice progression. |

## Features

### Paddy Farmer

Recipe:

```text
G G G
G F G
I W I
```

- `G` = Glass Pane
- `F` = Easy Villagers Farmer
- `I` = Iron Ingot
- `W` = Water Bucket

The Water Bucket leaves its empty Bucket as the crafting remainder.

Paddy Farmer:

- preserves the Farmer villager and output data during crafting;
- accepts Farmer's Delight Rice directly;
- models lower Rice ages `0..3` and panicle ages `0..3` as one virtual `0..7` lifecycle;
- harvests mature panicles using Farmer's Delight's real loot table;
- keeps the mature lower Rice after harvest so only the upper portion regrows;
- harvests fully mature Rice on the next Farmer cadence instead of waiting for another `farmSpeed` RNG success;
- exposes the four Easy Villagers output slots through the normal output menu and NeoForge item-handler capability.

Rice is intentionally **not** added globally to `minecraft:villager_plantable_seeds`, because the normal Easy Villagers Farmer does not implement Rice's two-block lifecycle.

### Rich Farmer

Upgrade recipe:

```text
G G G
G F G
B R B
```

- `G` = Glass Pane
- `F` = Easy Villagers Farmer
- `B` = Iron Block
- `R` = Farmer's Delight Rich Soil

The upgrade preserves the Farmer item/block-entity payload instead of resetting its villager, crop or output.

Rich Farmer:

- accepts the same normal crops validated by Easy Villagers;
- uses Easy Villagers' configured `farmSpeed` for normal crop work;
- reads Farmer's Delight's live `richSoilBoostChance`;
- gives Rich Soil its own `farmSpeed`-scaled opportunity and applies the crop's real Bone Meal age increment when supported;
- respects `farmersdelight:unaffected_by_rich_soil`;
- keeps mature crops waiting when the four output slots cannot accept the complete harvest, preventing silent item loss.

### Tomato + Rope

Tomato is a dedicated persistent Rich Farmer crop:

- Tomato Seeds grow through Farmer's Delight's budding stage into the reusable Tomato vine;
- harvesting does not consume another seed;
- mature harvest produces 1-2 Tomatoes and preserves Farmer's Delight's Rotten Tomato chance;
- up to **2 Rope** sections may be installed;
- Base, Rope 1 and Rope 2 keep independent progress and independent work rolls;
- each mature section waits for sufficient output capacity before harvesting;
- sneak-right-click removes the topmost Rope first, then the Tomato crop selection once no Rope remains.

### Mushroom Colonies

Rich Farmer accepts Red and Brown Mushrooms and converts them into the corresponding Farmer's Delight Mushroom Colony. Colonies grow through their normal age states, harvest repeatedly without consuming another mushroom, benefit from Rich Soil, and wait at maturity when the output inventory is full.

### Rich Paddy Farmer

Rich Paddy uses the same Rich upgrade structure, with Paddy Farmer in the center:

```text
G G G
G P G
B R B
```

It preserves Paddy state and applies Rich Soil acceleration across the **entire Rice lifecycle**, including upper panicle stages. Mature Rice also waits instead of deleting drops when the output inventory cannot hold the complete harvest.

### Optional crop compatibility

The addon adds optional `minecraft:villager_plantable_seeds` entries without requiring the corresponding mods.

**Argentum**

- `argentum:yerba_semilla`
- `argentum:te_semilla`
- `argentum:batata`
- `argentum:membrillo_semilla`

**Ars Nouveau**

- `ars_nouveau:magebloom_crop`

Magebloom can be farmed by the normal Easy Villagers Farmer and by Rich Farmer; Rich Farmer also applies Rich Soil acceleration.

### Jade

Jade support is optional. When Jade is installed, compat Farmers can display:

- selected crop;
- growth percentage;
- Rich Soil status;
- Tomato Base / Rope 1 / Rope 2 progress on a single line.

## Rendering

The three compat Farmers use original models/rendering owned by this addon while following the spatial behavior expected from an Easy Villagers Farmer:

- horizontal facing and rotation;
- visible villager and crop;
- crop-specific render type and tint handling;
- shallow-water Paddy basin;
- Rich Soil floor on Rich variants;
- inset second glass shell only on Rich variants;
- visual Tomato-on-Rope sections;
- valid block-breaking particle textures.

No Easy Villagers models or textures are copied into this project. Farmer's Delight resources such as Rich Soil are referenced from the installed dependency at runtime.

## Dependencies

Required:

- Minecraft **1.21.1**
- NeoForge **21.1.235+**
- Easy Villagers **1.1.42+**
- Farmer's Delight **1.2.9+**

Optional integrations:

- Jade
- Argentum
- Ars Nouveau

## Installation

Install the mod on **both client and server** together with Easy Villagers and Farmer's Delight. Optional integrations are detected when their corresponding mods are present.

## Building

The project uses Java 21 and NeoForge ModDevGradle.

Windows:

```text
build-dev.bat
```

Linux/WSL:

```text
./build-dev.sh
```

The resulting JAR is written to `build/libs/`. Runtime dependency mods are not embedded or shaded into this JAR. Jade is a compile-only optional API dependency.

## License

Copyright © 2026 Celerbi. All rights reserved.
