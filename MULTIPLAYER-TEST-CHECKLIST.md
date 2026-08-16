# Multiplayer / Dedicated Server Release Checklist

Target: **Easy Farmer's Delight Compat 1.0.0+ regression testing**  
Minecraft: **1.21.1**  
NeoForge baseline: **21.1.235**

The 1.0.0 release pass has been completed. Keep this document as the regression checklist for future updates, dependency upgrades and bug-fix releases. Do not change balance simply because RNG differs between short runs.

## 1. Server boot matrix

### Required-only server

Install on both server and client:

- Easy Farmer's Delight Compat
- Easy Villagers
- Farmer's Delight
- NeoForge

Verify:

- [ ] Dedicated server reaches ready state without client-only classloading crashes.
- [ ] Client can join.
- [ ] All three compat Farmer blocks exist.
- [ ] No missing registry / serializer / BlockEntity errors in server log.

### Optional-mod absence

With **Jade, Argentum and Ars Nouveau absent**:

- [ ] Server still starts.
- [ ] Client still joins.
- [ ] Farmers operate normally.
- [ ] No missing optional-class or missing-resource crash occurs.

### Optional-mod presence

Repeat relevant tests with:

- [ ] Jade client-side.
- [ ] Jade client + server.
- [ ] Ars Nouveau client + server.
- [ ] Argentum client + server.

## 2. Placement / facing / rendering sync

For Paddy Farmer, Rich Farmer and Rich Paddy Farmer:

- [ ] Player A places block facing north.
- [ ] Player A places block facing south.
- [ ] Player A places block facing east.
- [ ] Player A places block facing west.
- [ ] Player B sees the same orientation without relogging.
- [ ] Villager orientation is correct for both clients.
- [ ] Crop orientation is correct for both clients.
- [ ] Paddy water/iron basin looks correct for both clients.
- [ ] Only Rich variants show the inset second glass shell.
- [ ] No black/purple missing textures appear while breaking the blocks.

## 3. Villager synchronization

- [ ] Player A inserts an Easy Villagers villager; Player B sees it immediately.
- [ ] Player B removes it; Player A sees removal immediately.
- [ ] Reinsert and verify Farmer profession behavior.
- [ ] Test a baby villager if available.
- [ ] Leave the chunk and return; villager state remains.
- [ ] Restart server; villager state remains.

## 4. Normal Rich Farmer crops

Test at minimum:

- [ ] Wheat or another vanilla crop.
- [ ] Farmer's Delight Cabbage.
- [ ] Farmer's Delight Onion.

For each:

- [ ] Player A selects crop; Player B sees it.
- [ ] Growth animation/state stays synchronized.
- [ ] Harvest output appears exactly once.
- [ ] No duplicate output occurs when two players watch/interact.
- [ ] Removing crop selection returns the expected planting item.
- [ ] Rich Soil acceleration remains observable over a long/high-tick test.

## 5. Paddy Rice

### Paddy Farmer

- [ ] Rice can be inserted.
- [ ] Lower Rice stages are visible to both clients.
- [ ] Panicle stages are visible to both clients.
- [ ] Mature Rice does not remain stalled waiting for another random work success.
- [ ] Mature panicles harvest once.
- [ ] Lower mature Rice remains after harvest.
- [ ] Output is synchronized to both players.

### Rich Paddy Farmer

- [ ] Same lifecycle as Paddy.
- [ ] Rich Soil acceleration spans lower Rice + panicles.
- [ ] Long-run output is measurably ahead of normal Paddy under the same conditions.
- [ ] No additional/drop duplication occurs from Rich Soil boost + normal work landing close together.

## 6. Tomato + Rope

- [ ] Insert Tomato Seeds into Rich Farmer.
- [ ] Budding stage is visible to both clients.
- [ ] Persistent Tomato vine transition is visible to both clients.
- [ ] Install Rope 1; both clients see it.
- [ ] Install Rope 2; both clients see it.
- [ ] Base, Rope 1 and Rope 2 do not remain permanently synchronized.
- [ ] Each mature section produces output exactly once.
- [ ] Tomato plant remains after harvest.
- [ ] Sneak-remove Rope 2 returns one Rope.
- [ ] Sneak-remove Rope 1 returns one Rope.
- [ ] Next crop removal returns Tomato Seeds.
- [ ] Two players attempting Rope/crop removal at nearly the same time cannot duplicate Rope or seeds.

## 7. Mushroom Colonies

Test Red and Brown separately:

- [ ] Mushroom is accepted only by Rich Farmer special handling.
- [ ] Correct Mushroom Colony renders.
- [ ] Colony age progresses for both clients.
- [ ] Mature colony harvest produces mushrooms exactly once.
- [ ] Colony resets and grows again without consuming another mushroom.
- [ ] Rich Soil acceleration works without skipping into invalid state.
- [ ] Crop removal returns the original mushroom item.

## 8. Ars Nouveau — Magebloom

With Ars Nouveau installed:

- [ ] Normal Easy Villagers Farmer accepts Magebloom through the seed tag.
- [ ] Rich Farmer accepts Magebloom.
- [ ] Magebloom growth is visible and harvests normally.
- [ ] Rich Farmer accelerates Magebloom via Rich Soil.

Without Ars Nouveau installed:

- [ ] The optional tag entry does not prevent datapack/tag loading.

## 9. Argentum

With Argentum installed, test each available crop:

- [ ] Yerba Mate
- [ ] Té
- [ ] Batata
- [ ] Membrillo

For each:

- [ ] Base Easy Villagers Farmer accepts it.
- [ ] Rich Farmer accepts it.
- [ ] Rich Farmer applies Rich Soil acceleration when the crop permits it.

Without Argentum installed:

- [ ] Optional tag entries do not prevent datapack/tag loading.

## 10. Output inventory / automation

For each Farmer variant:

- [ ] Output GUI opens for Player A.
- [ ] Output GUI can be observed/used by Player B after Player A closes it.
- [ ] Items persist after GUI close.
- [ ] Hopper extracts from output.
- [ ] Modded item pipes using NeoForge item capability can extract if available.
- [ ] Extraction marks state dirty and persists through chunk unload.
- [ ] Extraction persists through full server restart.
- [ ] Filling all four output slots does not delete or duplicate later drops.

## 11. Upgrade preservation

Prepare Farmer/Paddy items containing meaningful state/output, then craft upgrades.

### Farmer -> Rich Farmer

- [ ] Villager preserved.
- [ ] Villager profession/level/XP preserved.
- [ ] Crop selection preserved where compatible.
- [ ] Output inventory preserved.
- [ ] Unknown Easy Villagers BlockEntity data survives.

### Paddy -> Rich Paddy

- [ ] Villager preserved.
- [ ] Rice selection preserved.
- [ ] Rice progress preserved.
- [ ] Output inventory preserved.
- [ ] Addon NBT survives.

Break/place upgraded blocks and repeat after server restart.

## 12. Jade

### Jade on client + server

- [ ] Crop name correct.
- [ ] Growth percentage correct.
- [ ] Rich Soil status correct.
- [ ] Tomato Base/Rope progress correct and on one line.
- [ ] Tooltip updates while crop grows without reopening/relogging.

### Jade client-only fallback

If the server allows a client-only Jade setup:

- [ ] No server crash/classloading error.
- [ ] Farmer tooltip still uses synchronized BlockEntity fallback data.
- [ ] Values do not become permanently stale.

## 13. JEI / EMI

Run separately if necessary to avoid viewer conflicts.

### JEI

- [ ] Paddy Farmer recipe is discoverable.
- [ ] Rich Farmer recipe is discoverable.
- [ ] Rich Paddy upgrade is discoverable through the Rich recipe flow.
- [ ] Ingredient/result display is correct.
- [ ] Water Bucket remainder is represented correctly if supported.

### EMI

- [ ] Paddy Farmer recipe is discoverable.
- [ ] Rich Farmer recipe is discoverable.
- [ ] Rich Paddy upgrade is discoverable through the Rich recipe flow.
- [ ] Ingredient/result display is correct.
- [ ] Water Bucket remainder is represented correctly if supported.

If a recipe is functional in crafting but absent from JEI/EMI, record it as a **recipe-viewer compatibility issue**, not a crafting failure.

## 14. Concurrency / abuse tests

- [ ] Two players right-click the same empty Farmer with villager items nearly simultaneously.
- [ ] Two players try to select/remove a crop nearly simultaneously.
- [ ] Two players try to add/remove Tomato Rope nearly simultaneously.
- [ ] Break Farmer while another player has its output menu open.
- [ ] Break/place Farmer repeatedly with non-empty output.
- [ ] Chunk unload during active growth does not duplicate a harvest on reload.
- [ ] Server stop during active growth does not duplicate a harvest after restart.

Expected result for every case: no crash, no item duplication, no silent item loss, no permanent visual desync.

## 15. Final log review

After testing, inspect `logs/latest.log` and server log for:

- [ ] `ERROR`
- [ ] `Exception`
- [ ] registry errors
- [ ] recipe serializer errors
- [ ] BlockEntity load/save warnings
- [ ] missing model/texture warnings
- [ ] optional dependency classloading errors
- [ ] network/synchronization errors

## Release pass criteria

A future build is ready for release when:

- [ ] Dedicated server boots reliably.
- [ ] No reproducible crash exists.
- [ ] No duplication or persistent desync exists.
- [ ] Farmer state survives chunk unload and server restart.
- [ ] Required gameplay works with optional mods absent.
- [ ] Optional integrations work when their mods are installed.
- [ ] Any JEI/EMI visibility issue is either fixed or explicitly documented before release.
