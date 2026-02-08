# Space Shooter

A modern 2D space shooter game for Android built with Java and `SurfaceView`.

## Features
*   **Levels**: Difficulty increases as you score points.
*   **Power-Ups**: Collect Shield (Blue) and Rapid Fire (Magenta) power-ups.
*   **Visual Effects**: Explosions and particle effects.
*   **High Score**: Locally saved high scores.

## Building the APK
To build the game, open the project in Android Studio or run the following command in the terminal:

```bash
gradle assembleDebug
```

The APK file will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

## Installation
1.  Enable "Install from Unknown Sources" on your Android device settings.
2.  Copy the `app-debug.apk` file to your device.
3.  Tap the file to install.
4.  Launch the app "Space Shooter" from your app drawer.

## Controls
*   **Movement**: Drag your finger anywhere on the screen to move the ship horizontally.
*   **Shooting**: The ship fires automatically.
*   **Restart**: Tap the screen on the "Game Over" screen to restart.
