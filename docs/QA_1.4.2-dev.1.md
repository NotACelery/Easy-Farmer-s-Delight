# QA — Easy Farmer's Delight 1.4.2-dev.1 (NeoForge 1.21.1)

## Build and startup

- Build with Java 21 and the project Gradle configuration.
- Start once with Easy Villagers + Farmer's Delight only. Confirm no Croptopia classes/items are required.
- Start once with Croptopia NeoForge 1.21.1-4.2.4.
- Check that all 28 bundled Orchard definitions load with Croptopia present (2 vanilla + 26 Croptopia) and only the 2 vanilla definitions load without it.

## Grafting Support

- Craft using `RRR / SLS / SHS` (Rope / Stick+Log+Stick / Stick+Hanging Roots+Stick).
- Confirm any valid `#minecraft:logs` ingredient creates the same stackable item/model.
- Install only into an empty Rich Farmer; reject Paddy/Rich Paddy and conflicting crop/attached-log modes.
- Visually inspect the rootstock, roots, four stakes and Rope support from all Farmer facings.
- With support but no leaves, Jade must show Grafting Support + waiting for compatible leaves.
- Shift-right-click with an empty hand and with a held item; the support must return cleanly.

## Vanilla Apple Orchard

- Test Oak Leaves and Dark Oak Leaves separately.
- Confirm stages 0→1→2→3 visibly read as bud → flower → young Apple → mature Apples.
- Jade must show Apple Orchard and a growth percentage while growing.
- At age 3 without Shears, Jade must show Ready + Waiting for Shears and the fruit must remain mature.
- Insert Shears and confirm harvest resets to age 0 and outputs 1–2 Apples with only occasional 3-Apple rolls.
- Fill output before maturity; verify no fruit reset and no Shears damage until space exists.
- Test enchanted Shears, especially Unbreaking III, over repeated harvests and verify durability prevention occurs normally.
- Shift-right-click a complete Orchard: first interaction returns the exact inserted leaves, second returns the Grafting Support.

## Croptopia 4.2.4 Orchards

- Test representative leaves at minimum: Banana, Cherry, Coconut, Lemon, Croptopia Apple and Walnut.
- Sweep all 26 `croptopia:*_crop` leaves to ensure each installs and renders.
- Confirm all Croptopia canopies use their source AGE 0/1/2/3 models.
- Confirm each mature Orchard outputs its matching fruit; Croptopia Apple must output `minecraft:apple`.
- Confirm every Croptopia Orchard waits for Shears exactly like the vanilla Apple Orchard.
- Confirm Jade names the Orchard from the fruit (Banana Orchard, Cherry Orchard, etc.).
- Remove Croptopia after making a separate disposable test save and confirm the mod itself still starts; do not use that save as a content-preservation expectation for removed Croptopia items.

## Cutter — Croptopia Cinnamon

- Cinnamon Log + Axe → Stripped Cinnamon Log + 1 Cinnamon.
- Cinnamon Wood + Axe → Stripped Cinnamon Wood + 1 Cinnamon.
- Verify one operation consumes one source block and one Axe durability attempt.
- Block enough output slots that both results cannot fit: input and Axe durability must remain unchanged.
- Confirm ordinary vanilla/modded log stripping from 1.4.1 still works.

## Integrations and regressions

- Jade: empty support, growing Orchard, mature Orchard with/without Shears, Rich Soil state.
- JEI and EMI: Grafting Support recipe, Apple Orchard guide, Croptopia Orchard guide when installed, Cinnamon guide when installed, Shears Harvest Tool page.
- Verify normal crops, Tomato/Rope, Mushroom Colonies, Melon/Pumpkin, regrowing crops and attached-log crops still operate.
- Verify Rich Paddy Rice/Sugar Cane are unchanged.
- Verify Farmer inventory/tool transfer still accepts Knife/Hoe/Axe and now also Shears.
