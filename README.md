# Easy Farmer's Delight Compat

<p align="center">
  <img src="easy-farmers-delight-compat-cover.webp" alt="Easy Farmer's Delight Compat" width="320">
</p>

**Easy Farmer's Delight Compat** is an unofficial addon that brings **Farmer's Delight crops and mechanics into Easy Villagers-style automation** for Minecraft **1.21.1 / NeoForge**.

If you already know how the Easy Villagers Farmer works, the idea is simple: this mod gives you new Farmer variants for crops that need special handling, plus a Cutter for Farmer's Delight Cutting Board recipes.

> This is an independent community project. It is not affiliated with or endorsed by the authors of Easy Villagers, Farmer's Delight, Jade, JEI, EMI, Ars Nouveau, or other supported mods.

## What does the mod add?

The main additions are:

- **Paddy Farmer** — made for Rice and Sugar Cane.
- **Rich Farmer** — a stronger Farmer that uses Rich Soil and supports more Farmer's Delight crops.
- **Rich Paddy Farmer** — combines the Paddy Farmer with Rich Soil bonuses where they make sense.
- **Harvest Tools** — Rich Farmers can hold a Knife, Hoe, or Axe when a crop needs one.
- **Cutter** — automatically processes Farmer's Delight Cutting Board recipes and common Axe actions.
- **Villager Noise Switch** — lets you mute Villager voices on your client.
- Optional **Jade**, **JEI**, and **EMI** support with useful in-game explanations.

---

# Farmer variants

## Paddy Farmer

The **Paddy Farmer** is the Farmer variant for crops that do not fit a normal farm field.

### Crafting

Upgrade an Easy Villagers Farmer with:

```text
G G G
G F G
I W I
```

- `G` = Glass Pane
- `F` = Easy Villagers Farmer
- `I` = Iron Ingot
- `W` = Water Bucket

The empty Bucket is returned after crafting.

### Rice

Put Farmer's Delight **Rice** into the Paddy Farmer and it will handle the complete growing and harvesting cycle automatically.

Rice keeps growing after each harvest instead of needing to be replanted manually.

### Sugar Cane

To use Sugar Cane:

1. Start with an empty Paddy Farmer.
2. Add **Sand**.
3. Add **Sugar Cane**.

The Sugar Cane grows up to its normal three-block height. When it is ready, the Farmer harvests the upper sections and leaves the bottom section growing.

Sugar Cane does **not** receive a Rich Soil speed bonus, even inside a Rich Paddy Farmer.

---

## Rich Farmer

The **Rich Farmer** is an upgraded Farmer built around Farmer's Delight **Rich Soil**.

It supports regular Easy Villagers farming while adding special handling for several Farmer's Delight crops.

### Crafting

Upgrade an Easy Villagers Farmer with:

```text
G G G
G F G
I R I
```

- `G` = Glass Pane
- `F` = Easy Villagers Farmer
- `I` = Iron Block
- `R` = Rich Soil

If you use a **Paddy Farmer** in the center instead, you receive a **Rich Paddy Farmer**.

### Regular crops

The Rich Farmer can continue handling normal Farmer crops while benefiting from Rich Soil where supported.

You may also give it a **Hoe**. A Hoe is optional for these crops, but enchantments such as Fortune can help when the crop normally supports them.

### Tomatoes and Rope

Plant **Tomato Seeds** in the Rich Farmer just like a regular crop.

Tomatoes stay planted after harvesting. You can also add up to **two Rope sections** so the tomato plant can grow higher and produce from each section independently.

A Hoe is optional.

### Mushroom Colonies

A Rich Farmer can grow both:

- Red Mushroom Colonies
- Brown Mushroom Colonies

Give it the matching Mushroom to start growing the colony.

The colony can grow normally without a tool, but once it is mature the Farmer will wait for a **Knife** before harvesting it. The Knife is required for the harvest but is **not damaged** by this action.

### Melons and Pumpkins

The Rich Farmer can also automate:

- Melon Seeds
- Pumpkin Seeds

The Farmer first grows the stem, then waits for the fruit to appear.

When the Melon or Pumpkin is ready, an **Axe is required** to harvest it. Axe enchantments such as Fortune or Silk Touch work normally where Minecraft allows them, and the Axe only loses durability after a successful harvest.

---

## Rich Paddy Farmer

The **Rich Paddy Farmer** is created by upgrading a Paddy Farmer with the same Rich Soil recipe used for a Rich Farmer.

It keeps the Paddy Farmer's support for **Rice and Sugar Cane**, while also gaining the Harvest Tool slot and Rich Soil bonuses where applicable.

For Rice, a **Knife is optional**. Using one allows the Rich Paddy Farmer to take advantage of the Knife-sensitive Rice drops without damaging the Knife.

Sugar Cane still grows at its normal Paddy Farmer speed.

---

# Harvest Tools

Rich Farmer and Rich Paddy Farmer have a dedicated **Harvest Tool** slot.

You do not need to remember every rule. JEI and EMI show a short Harvest Tools guide in-game, but the simple version is:

| Tool | What it is for |
| --- | --- |
| **Knife** | Optional for Rich Paddy Rice, required for Mushroom Colonies |
| **Hoe** | Optional for regular crops and Tomatoes; useful for Fortune where supported |
| **Axe** | Required for Melons and Pumpkins |

The Farmer only uses a tool when the current crop actually needs it.

---

# Cutter

The **Cutter** is an automated Farmer's Delight Cutting Board.

It stores a Villager, accepts multiple input items, and uses a **Knife or Axe** as its Cutting Tool.

### Crafting

```text
G G G
G C G
B L B
```

- `G` = Glass Pane
- `C` = Farmer's Delight Cutting Board
- `B` = Bricks
- `L` = compatible Log

### How to use it

1. Place a Villager inside the Cutter.
2. Put a **Knife or Axe** in the Cutting Tool slot.
3. Add items to the input slots.
4. The Cutter processes them automatically and stores the results in its output slots.

The Cutter supports normal Farmer's Delight Cutting Board recipes.

With an Axe, it can also perform familiar Axe actions such as:

- stripping compatible Logs and wood blocks;
- removing wax from Copper;
- scraping oxidized Copper.

Compatible modded wood can work too when the mod provides a normal Axe stripping action.

If the Cutter needs a different tool, it simply waits instead of wasting the input or damaging the wrong tool.

---

# Villager Noise Switch

Too many Villagers making noise around your base?

The **Villager Noise Switch** lets you mute Villager voices for **your own Minecraft client**.

### Crafting

```text
G G G
G L G
R I R
```

- `G` = Glass Pane
- `L` = Lever
- `R` = Redstone Block
- `I` = Iron Block

### How to use it

1. Put an Easy Villagers Villager inside the block.
2. Right-click the switch to toggle Villager sounds.

Your preference is remembered when you change worlds or servers.

The Lever and Redstone inside the model are decorative; the block does not act as a real Redstone switch.

---

# JEI and EMI guides

**JEI and EMI are optional**, but they are strongly recommended if you want to learn the mod while playing.

The mod adds its own simple guide pages showing:

- what each Farmer can grow;
- how Rice and Sugar Cane work;
- which Harvest Tool a crop needs;
- how Tomatoes and Rope work;
- how Mushroom Colonies are harvested;
- how Melons and Pumpkins work;
- how to use the Cutter;
- how to use the Villager Noise Switch.

In EMI, open the **Recipes** for one of the mod's blocks and you will find a **Block Guide** tab beside the normal crafting information.

The guides are separated by Farmer type so you only see information that is useful for the machine you are checking.

---

# Jade support

**Jade is optional.** If installed, looking at the mod's blocks gives you useful information without opening their menus.

Depending on the block, Jade can show things such as:

- current crop and growth progress;
- whether a crop is ready to harvest;
- whether a Knife or Axe is missing;
- the equipped Harvest Tool;
- Sugar Cane height;
- Tomato and Rope growth;
- Melon/Pumpkin growth stage;
- Cutter progress and wrong-tool warnings;
- whether Villager sounds are currently enabled or muted.

---

# Optional mod compatibility

The mod can recognize compatible crops, tools, and wood from other mods when they use the normal Minecraft/NeoForge compatibility systems.

There is also optional support for mods such as **Ars Nouveau** and **Argentum** where applicable. They are **not required** to use Easy Farmer's Delight Compat.

---

# Requirements

Required:

- Minecraft **1.21.1**
- NeoForge **21.1.235 or newer**
- Easy Villagers **1.1.42 or newer**
- Farmer's Delight **1.2.9 or newer**

Optional:

- Jade
- JEI
- EMI
- Ars Nouveau
- Argentum

Install the mod on both the **client and server** when playing multiplayer.

---

# Version 1.2.0

Version 1.2.0 greatly expands the farming side of the mod with Sugar Cane, Melons, Pumpkins, Harvest Tools, the Villager Noise Switch, improved Cutter behavior, better Jade information, and dedicated JEI/EMI guides.

See [CHANGELOG.md](CHANGELOG.md) for the full release history.

---

# Building from source

For contributors who want to build the project themselves, Java 21 is required.

Windows:

```text
build-dev.bat
```

Linux / WSL:

```text
bash build-dev.sh
```

The built JAR is created in `build/libs/`.

## License

Copyright © 2026 Celerbi. All rights reserved.
