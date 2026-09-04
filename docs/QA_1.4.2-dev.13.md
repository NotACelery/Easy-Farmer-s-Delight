# QA — Easy Farmer's Delight 1.4.2-dev.13 (NeoForge 1.21.1)

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

## Placed Grafting Support — dev.2 additions

- Place the Grafting Support on normal Dirt/Stone and verify the full support model renders correctly in-world.
- Check inventory, first-person right/left hand, third-person right/left hand, dropped item, item frame/fixed context, and Rich Farmer installed rendering for clipping or incorrect scale.
- Insert Oak Leaves, Dark Oak Leaves and at least one ordinary non-productive leaf (Birch recommended). Verify newly inserted Oak/Dark Oak on non-Rich Soil render as plain leaves with no Apple bud/fruit overlay.
- Verify any leaves can be inserted visually, but Birch/other unsupported leaves remain permanently decorative.
- Place the support directly above Farmer's Delight Rich Soil; verify Oak/Dark Oak and supported Croptopia fruit leaves progress through all four stages.
- Move/recreate the same support without Rich Soil underneath; verify productive leaves do not advance. If Rich Soil is removed after growth has started, the current age must stall rather than reset.
- At mature age, right-click with empty hand or a non-Shears item; verify no fruit is harvested.
- Right-click mature fruit with Shears; verify fruit is returned to the player, the canopy resets to its post-harvest age and Shears durability is consumed normally.
- Repeat with Unbreaking I/II/III Shears and verify vanilla durability prevention applies.
- Left-click a support containing leaves; verify the leaves are returned first and the Grafting Support remains placed.
- Left-click/break the now-empty support and verify the support item drops normally.
- Break a support with a canopy through another destruction path and verify both support and canopy are preserved as drops.
- Check Jade on: empty support, decorative leaves, compatible leaves without Rich Soil, growing Orchard, mature Orchard.

## Visual pass — dev.4
- Grafting Support sits flush on the supporting block; roots visually contact the surface.
- Rope rails render continuously with no transparent/missing-looking fragments.
- Apple/Dark Oak orchards: AGE 0 buds, AGE 1 blossoms, AGE 2 young fruit and AGE 3 mature fruit are readable from all four horizontal sides.
- Mature apples sit outside the canopy instead of being hidden inside the leaf cube.
- Check the same progression both standalone on Rich Soil and inside a Rich Farmer.


## Two-block support / canopy hitbox — dev.5
- Placement must fail if the block directly above the Grafting Support cannot be replaced; the support reserves exactly two vertical blocks.
- With a canopy installed, aim directly at the rendered leaves and verify the upper block is selectable instead of behaving like phantom geometry.
- Right-click mature fruit with Shears while targeting the leaves; verify the same harvest/reset/durability behavior as clicking the lower trunk/support.
- Left-click the leaves; verify the canopy item is returned and the lower Grafting Support remains placed.
- After removing the canopy, verify the invisible reserved upper block no longer presents a selectable outline, but still prevents another block from occupying the reserved space.
- Break the empty lower support and verify its upper reserved block is removed with it.
- Point Jade at both the lower support and the canopy hitbox and verify both report the same Orchard state.
- Verify the central rootstock trunk, root struts and four stakes share the same Y=0 ground line with no visible floating gap.


## dev.6 Rich Farmer visual regression

- Install a Grafting Support in a Rich Farmer with no canopy and confirm the rootstock/post feet sit on the internal soil surface and no geometry protrudes below the Farmer block.
- Compare the same Grafting Support placed standalone and inside a Rich Farmer; the support must keep the same bottom-aligned geometry, with only the intentional Rich Farmer scale reduction.
- Add Oak or Dark Oak Leaves and confirm the Rich Farmer canopy is visibly smaller than dev.5 and proportionate to the compact support.
- Check all four Apple Orchard ages from multiple sides; buds/fruit must remain outside the canopy and must not intersect the Farmer shell.
- Repeat with at least one Croptopia orchard leaf to confirm the reduced internal canopy scale does not clip or float.
