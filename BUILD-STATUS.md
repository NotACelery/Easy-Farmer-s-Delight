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
