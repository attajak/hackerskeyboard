# Upgrade Summary - Hacker's Keyboard

This document summarizes the changes made to modernize the Hacker's Keyboard project from its legacy structure (2011/Gingerbread base) to a modern Android standards (2026/Android 14 base).

## 1. Build System & Gradle
- **Android Gradle Plugin (AGP):** Upgraded from `3.2.1` to `8.1.1`.
- **Gradle Wrapper:** Upgraded to **`8.4`** (supports Java 17/21).
- **Repositories:** Replaced deprecated `jcenter()` with **`mavenCentral()`**.
- **Modern Structure:** Migrated repository management to `settings.gradle`.

## 2. SDK & Versions (Android 14 Ready)
- **Compile SDK:** **34** (Android 14).
- **Target SDK:** **34** (Android 14).
- **Min SDK:** **21** (Android 5.0).
- **Namespace:** Added `org.pocketworkstation.pckeyboard` namespace in `app/build.gradle`.

## 3. AndroidX Migration
- **Enabled AndroidX:** Added `android.useAndroidX=true` and `android.enableJetifier=true` in `gradle.properties`.
- **Dependencies:** Migrated all `com.android.support` libraries to their **`androidx.*`** equivalents.
- **Source Code:** Updated imports in `LatinIME.java` (e.g., `androidx.core.app.NotificationCompat`).

## 4. Android 12+ Compatibility
- **Manifest Security:** Added `android:exported="true"` to all Services and Activities with `intent-filter` in `AndroidManifest.xml`.
- **Manifest Cleanup:** Removed the `package` attribute from the manifest tag (now handled by Gradle namespace).

## 5. Native Code (C++)
- **NDK:** Set `ndkVersion` to **`26.1.10909125`**.
- **CMake:** Maintained CMake configuration for binary dictionary support.

---

## Instructions for Future Development
To continue development on a standard PC/Mac:
1. Open the project folder in **Android Studio** (Hedgehog or newer).
2. Allow Gradle to sync and download required dependencies.
3. Build the APK using `./gradlew assembleDebug` or via the IDE.

*Note: This project was prepared and cleaned on a Termux environment to ensure a lightweight and standard structure for external IDEs.*
