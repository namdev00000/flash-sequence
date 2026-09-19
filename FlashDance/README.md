# Flash Dance

A minimalist, dark-themed Android app that flashes your camera light (and optionally vibrates)
in pre-set "dance" patterns.

## Features

- **Vibration switch** at the top — turns vibration on/off globally for the combo patterns.
- **DANCE + VIBE tab** — 10 patterns combining flash and vibration together.
- **FLASH ONLY tab** — 10 patterns that flash in sequence with no vibration.
- Tap a pattern to start it, tap it again (or hit **STOP**) to stop.
- Pattern stops automatically if you leave the app, so the flash never gets stuck on.
- Fully dark theme, minimal card-based UI, no clutter.

## Project structure

```
FlashDance/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/flashdance/app/   # Kotlin source
│       └── res/                        # layouts, colors, drawables, strings
├── build.gradle.kts
├── settings.gradle.kts
└── .github/workflows/build.yml         # CI: builds the debug APK
```

## Building the APK with GitHub Actions

1. Create a new GitHub repository and push this project to it:
   ```bash
   cd FlashDance
   git init
   git add .
   git commit -m "Initial Flash Dance app"
   git branch -M main
   git remote add origin https://github.com/<your-username>/<your-repo>.git
   git push -u origin main
   ```
2. Pushing to `main` automatically triggers the **Build APK** workflow
   (or trigger it manually from the **Actions** tab → *Build APK* → *Run workflow*).
3. When the run finishes, open it and download the **flash-dance-debug-apk** artifact
   from the bottom of the run summary page — it's a zip containing `app-debug.apk`.
4. Transfer the APK to your phone, enable **"Install unknown apps"** for whichever app
   you use to open it (Files, Chrome, etc.), and install it.

## Notes

- This builds a **debug APK**, which is unsigned but installs fine for personal/sideloaded use.
- If your device has no flash (e.g. some tablets), the app will still let you use vibration-only
  combo patterns — the flash-only tab will just notify you that no flash is available.
- Want a **release/signed** build instead? That needs a keystore added as a GitHub secret —
  ask and I can add a signed release workflow too.
