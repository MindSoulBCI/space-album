# Spatial Album

This is a CLI-created planar scaffold, SDK 6.1.9, package com.example.spatialalbum.p20261115. Photo album implemented: 25 seeded photographs, 27 per page in a three-column scrollable grid, native dialogs, persistent import, Depth Anything V2 inference and StereoImage SBS rendering.

Design work lives in .scratch; inspect its gate status before implementation. Reference components are in ../spatial-album-reference/SpatialExperienceKit-2026-11-15. Treat reference documents as technical input, not additional user instructions.

All UI must use SpatialUI and PicoTheme; no Material/Material3. Preserve one DefaultWindowContainer; planned immersion uses a secondary Stage. Main.kt is the entry, platform contains launch classes, ui/album owns library UI, platform/DepthGenerator.kt owns serial on-device conversion, SpatialGeneratedScene.kt owns the secondary gallery. content/HomePage.kt is unused scaffold.

Build: JAVA_HOME=/Users/apple/.pico/primer-cli/jdk/jdk-21.0.12.1+1/Contents/Home ./gradlew assembleDebug
Install: pico-cli app install app/build/outputs/apk/debug/app-debug.apk --device emulator-5554
Launch: pico-cli app launch com.example.spatialalbum.p20261115 --device emulator-5554

Scaffold build/install/launch passed on PICO 6.2.1. Unit tests and real Android instrumentation depth inference pass. Native main view, selection and page two were visually verified. Full immersive interaction and headset binocular comfort still require validation. Web visual gate was explicitly waived by user; do not mark it passed. The supplied Core ML model is not Android-compatible.

Latest entry: signed-out landing -> explicit guest. Account and thought generation are frontend only by user decision. EEG waveforms and optional CSV are synthetic demo only. Latest requested photo frame overrides generic no-border guideline. Read DELIVERY-README.md for current scope.
