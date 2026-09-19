# FlashDance

A minimalist, dark-themed Android app that flashes the camera light (and optionally vibrates) in pre-set dance patterns.

## Features

- Vibration switch at the top.
- DANCE + VIBE tab — 10 flash + vibration patterns.
- FLASH ONLY tab — 10 flash-only patterns.
- Tap a pattern to start it; tap it again or press STOP to stop.
- Pattern stops automatically when the app leaves the foreground.
- Dark, minimal card-based UI.
- Kotlin + XML Views.
- Target SDK 35 (Android 15).

## Build

Open the project in Android Studio and let Gradle sync.

Or from the project directory:

```bash
./gradlew assembleDebug
```

Windows:

```bat
gradlew.bat assembleDebug
```

The debug APK will be under:

`app/build/outputs/apk/debug/`

## Notes for Android 15 / Funtouch OS

The app uses Android's `CameraManager.setTorchMode()` API and a foreground/visible-app lifecycle check. Camera permission is requested before using the torch.

Some Vivo/Funtouch OS devices may impose OEM-specific restrictions on camera/torch behavior. The app always attempts to switch the torch off when a pattern stops or the activity leaves the foreground.
