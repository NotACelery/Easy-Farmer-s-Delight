# Build status — 0.1.0-dev

- Minecraft: 1.21.1
- NeoForge baseline: 21.1.235
- Java: 21
- Paddy Farmer Rice engine: implemented
- Rich Farmer normal crop engine: implemented
- Rich Farmer Rich Soil base behavior: implemented
- Creative tab: implemented
- Paddy/Rich recipe matcher slot-order hotfix: implemented
- Farmer's Delight Rice IDs corrected for 1.21.1 (`rice` / `rice_panicles`)
- Tomato persistent base lifecycle: implemented
- Tomato Rope 1 / Rope 2 independent progress: implemented
- Mushroom Colonies: not yet implemented
- Rich Paddy Rich Soil boost: not yet implemented
- Source compilation check: passed
- Included test JAR: `build/libs/easyfarmersdelightcompat-1.21.1-0.1.0-dev.jar`
- Windows rebuild helper: `build-dev.bat`
- Linux/WSL rebuild helper: `build-dev.sh`

The included JAR is assembled from the compiled addon classes, project resources and generated NeoForge metadata. Compile-only helper stubs used to satisfy external loader/library signatures in the isolated build environment are not packaged in the JAR.

For a normal local build, run `build-dev.bat`; it resolves the real Gradle/NeoForge dependencies and writes the resulting JAR to `build/libs/`.

## 0.1.0-dev — source-based Farmer renderer correction

- Farmer variants now have a horizontal `FACING` state and rotate like Easy Villagers' Farmer.
- Client content renderer now follows Easy Villagers' 1.21.1 spatial layout: villager behind, crop in front, both at 0.45 scale.
- Crop rendering now uses the crop's own RenderType/model/tint pipeline instead of `renderSingleBlock`.
- Rich Farmer floor is flush at Y=0..1 rather than raised.
- Paddy variants use an internal iron retaining wall and water basin; only Rich variants receive the inset second glass shell.
- Blocks use `noOcclusion` like the Easy Villagers Farmer.
