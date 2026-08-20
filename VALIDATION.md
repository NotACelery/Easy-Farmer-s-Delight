# Validation status — 1.2.0 integration candidate

Validation date: **2026-08-19**

## Architecture / packaging

- Java 21 / Minecraft 1.21.1 / NeoForge 21.1.235 baseline.
- Easy Villagers Farmer integration remains isolated behind `EasyVillagersFarmerAdapter`.
- Farmer's Delight configuration access remains isolated behind `FarmersDelightAdapter`.
- No Easy Villagers or Farmer's Delight implementation classes/assets are redistributed.
- Upgrade recipes preserve source ItemStack components / block-entity data.
- Compat BlockEntity owns persistence and synchronization.
- Jade is compile-only/optional and is not bundled.
- Argentum and Ars Nouveau tag entries are optional.

## Gameplay validation — passed

Validated during development and final release testing:

- Paddy Farmer, Rich Farmer and Rich Paddy Farmer placement/orientation/rendering;
- villager and crop visualization;
- Rice lower crop + panicle lifecycle and repeated harvest;
- mature Rice harvest timing;
- Rich Soil acceleration on terrestrial crops and Rice;
- Tomato persistent harvesting and independent Base / Rope 1 / Rope 2 progression;
- Red/Brown Mushroom Colony repeated harvesting;
- Farmer's Delight non-vanilla crops used during the test pass;
- Ars Nouveau Magebloom acceptance, harvest and Rich Soil acceleration;
- Jade crop/growth/Rich Soil/Tomato progress integration;
- output inventory automation and persistence during the multiplayer/server pass;
- full-output behavior: mature crops wait for enough capacity to hold the complete harvest instead of deleting remainder items.

## Multiplayer / server release pass

The final multiplayer/dedicated-server test pass was completed before promoting the project to **1.0.0**. The detailed `MULTIPLAYER-TEST-CHECKLIST.md` remains in the repository for future regression testing.

## Release note

The approximately 30% Rich Paddy advantage observed in one accelerated Rice comparison is not a guaranteed multiplier. Rich Soil uses Farmer's Delight configuration and RNG-driven work opportunities.


## 1.2.0 regression matrix still required locally

- Paddy/Rich Paddy geometry: support island and Sand top faces are exactly flush with the model waterline; neither protrudes above water; Paddy villager scale is 90% and profession hats stay inside the glass.
- Growth pacing: Easy Villagers `farmer.farm_speed` is read live; failure falls back to 10 without disabling the adapter; Sugar Cane requires 16 successful age advances per new section and Rich Soil never accelerates it.
- Melon/Pumpkin rendering: stem center is at local 1/3, fruit center at local 2/3, ready fruit uses the matching attached-stem state facing the fruit, and the fruit remains fully inside the Farmer soil area.
- Jade: Rice, Sugar Cane heights 0..3 plus internal next-segment progress, Tomato 0/1/2 Rope, mature Mushroom `Ready to harvest` + waiting Knife, Melon/Pumpkin stem/growing fruit/ready fruit, Cutter missing/wrong/correct tool and Noise Switch local state.
- Multiplayer: two clients may see opposite Noise Switch visual/Jade states on the same world block.
- JEI only, EMI only, JEI+EMI and neither viewer installed.
- Viewer discoverability through machine blocks plus Sand, Sugar Cane, Rope, Melon Seeds, Pumpkin Seeds and tool ingredients.
- Verify Cutter remains a Farmer's Delight Cutting workstation/catalyst.
- Verify all nine locale JSON files contain identical keys.
- Final `build-dev.bat` and in-game pass must succeed before publication.

## Cutter standby regression

- [ ] Beef + no tool: progress stays at 0; Jade says `Waiting for Knife`.
- [ ] Beef + Axe: progress stays at 0; Jade says wrong tool / Knife required.
- [ ] Strippable Log + no tool: progress stays at 0; Jade says `Waiting for Axe`.
- [ ] Strippable Log + Knife: progress stays at 0; Jade says wrong tool / Axe required.
- [ ] Correct tool inserted: processing starts from 0 and completes once.
- [ ] Leave a wrong-tool Cutter loaded for several minutes: no 0→100→0 loop and no repeated work sounds.
- [ ] Change only the tool to the correct category: machine wakes immediately without replacing the input.

## Melon / Pumpkin Rich Soil cadence

- [ ] At `tick rate 20` and `randomTickSpeed 3`, compare Rich stem growth with a normal Farmer stem over a long run. Rich Soil should be an occasional bonus, not a frequent farmSpeed-coupled bonemeal burst.
- [ ] Set `randomTickSpeed 0`: Rich Soil gives no virtual stem boost, while ordinary Easy Villagers stem growth still proceeds.
- [ ] Mature stem fruit generation is unchanged by Rich Soil.

## Melon Fortune measurement

- [ ] Test Fortune by **fixed fruit count**, not only a fixed time window. Record at least 100 harvested Melons per tool.
- [ ] Unenchanted Axe: expected vanilla branch is 3–7 slices before explosion decay.
- [ ] Fortune III Axe: real vanilla loot table must be used and should converge above the unenchanted average over a large sample.
- [ ] Silk Touch Axe: 1 Melon block; Fortune branch is not used.

## Mature harvest cadence regression
- [ ] Mature normal crop harvests within the next one-second Farmer cadence.
- [ ] Mature Mushroom Colony + valid Knife harvests within the next one-second Farmer cadence.
- [ ] Mature Mushroom Colony + missing/wrong Knife remains mature and waits without rerolling growth.
- [ ] Inserting the correct Knife into a waiting mature Mushroom Colony resumes harvest within the next cadence.
- [ ] Mature Tomato base harvests without a second `farmSpeed` RNG gate.
- [ ] Mature Tomato Rope 1 / Rope 2 harvest without a second `farmSpeed` RNG gate.
- [ ] A Tomato section harvested on a cadence does not also advance to age/progress 1 on that same cadence.
- [ ] Full outputs leave mature crops intact and retry on later one-second cadences.
