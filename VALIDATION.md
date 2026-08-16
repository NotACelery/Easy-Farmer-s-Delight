# Validation status — 0.1.0-dev foundation

Validated on 2026-08-16:

- Java sources compile cleanly against Minecraft 1.21.1 / NeoForge 21.1.235 API artifacts using Java 21.
- All JSON resources parse successfully.
- Generated `META-INF/neoforge.mods.toml` parses successfully as TOML.
- No Java source imports Easy Villagers, Farmer's Delight, Jade or Argentum implementation packages.
- The packaged JAR contains no classes/assets copied from those mods.
- Rich farmer placeholder models reference Farmer's Delight's runtime `farmersdelight:block/rich_soil` texture rather than bundling it.
- Argentum entries in `minecraft:villager_plantable_seeds` are optional (`required: false`).

Not yet validated:

- Full NeoForge client launch with the required dependency JARs.
- Dedicated server launch.
- In-game crafting/NBT round trip against a live Easy Villagers Farmer.
- Farming engines, menus and renderers (not implemented in this foundation milestone).

The environment used for this milestone does not have a complete Gradle dependency cache or outbound Gradle dependency resolution, so the normal `gradle build/runClient` lifecycle could not be executed here. The development JAR is therefore a foundation/testing artifact, not a release candidate.
