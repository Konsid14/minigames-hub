# MiniGames Hub 🎮

An offline Android mini-games collection written in Kotlin.

## Games
- **Snake** — swipe to change direction, collect food and avoid collisions.
- **Tic-Tac-Toe** — two-player X/O game with win and draw detection.
- **Memory** — find all eight matching pairs.
- **Rock-Paper-Scissors** — play against the computer.

## Build the APK
Every push to `main` starts the **Build MiniGames Hub APK** GitHub Actions workflow.

1. Open the **Actions** tab in this repository.
2. Select the latest successful **Build MiniGames Hub APK** run.
3. Under **Artifacts**, download **MiniGames-Hub-APK**.
4. Extract the downloaded ZIP; the APK inside is `app-debug.apk`.

You can also start a build manually from **Actions → Build MiniGames Hub APK → Run workflow**.

## Build locally
Requires JDK 17, Android SDK (API 35), and Gradle 8.9:

```bash
gradle assembleDebug
```

The APK is created at `app/build/outputs/apk/debug/app-debug.apk`.
