# Blue Gamba — Minecraft Fabric Mod

A client-side slot-machine demo for Minecraft Java 1.21.11 / Java 21.

## Features
- Open the GUI with **G** (keybind can be changed in Minecraft Controls).
- Blue chest-inspired GUI.
- Three animated ore slots, spinning for 2–4 seconds.
- Virtual starting balance: 1,000 coins.
- Rejects empty, invalid, zero, and negative bets.
- Multipliers: Coal 0.5x, Iron 1.2x, Gold 2x, Diamond 5x, Emerald 10x, Ancient Debris 25x.
- Three matching symbols in the middle row determine payout.
- This is a local client-side demo; coins are not server-synced and are reset when the game is restarted.

## Build
Requirements: Java 21 and Gradle 8.14 or compatible.

```bash
gradle build
```

The JAR will be in `build/libs/`.

## GitHub Actions
Push the project to GitHub. The workflow under `.github/workflows/build.yml` builds the mod and uploads the JAR as a workflow artifact.

## Project layout
- `src/main/java/de/bluegamba/BlueGambaClient.java`
- `src/main/java/de/bluegamba/GambaScreen.java`
- `src/main/resources/fabric.mod.json`

Note: This source package has not been compiled or tested against a live Minecraft installation in this environment. If Gradle reports a mapping/API error, check the versions in `gradle.properties` and the corresponding Fabric documentation.
