# Hacker's Keyboard - Project Context

## Project Overview

**Hacker's Keyboard** is a legacy Android software keyboard application. It is based on the AOSP Gingerbread (Android 2.3) soft keyboard and is designed to provide a full 5-row layout experience similar to computer keyboards. This makes it particularly useful for technical tasks on Android devices, such as using SSH clients (e.g., ConnectBot), where keys like Tab, Ctrl, Esc, and arrow keys are essential.

*   **Status:** The project is considered "ancient" (originated in 2011) and is in maintenance mode. It targets older Android API levels and may have compatibility issues with modern Android versions.
*   **License:** Apache License 2.0 (based on AOSP).

## Architecture & Technology

The project follows a standard Android project structure but includes significant legacy components and native code usage.

*   **Language:** Java (Primary), C++ (JNI/NDK), Shell/Perl (Scripts).
*   **Build System:** Gradle (Wrapper available: `gradlew`).
*   **Native Build:** CMake (for C++ dictionary handling).
*   **Minimum SDK:** 14 (Android 4.0).
*   **Target SDK:** 26 (Android 8.0).

### Directory Structure

*   **`app/`**: The main application module.
    *   **`src/main/java/`**: Java source code for the Input Method Service (IMS) and UI.
    *   **`src/main/cpp/`**: C++ source code for binary dictionary handling (`BinaryDictionary.cpp`, `char_utils.cpp`).
    *   **`src/main/res/`**: Extensive resources including keyboard layouts (`xml/kbd_*.xml`), drawables, and localized strings.
    *   **`CMakeLists.txt`**: Configuration for building the `jni_pckeyboard` shared library.
    *   **`build.gradle`**: Module-level build configuration.
*   **`java/`**: Contains utility scripts (Shell/Perl) for maintenance tasks like versioning (`AutoVersion.sh`) and map checking.
*   **`dictionaries/`**: XML definitions for dictionaries.
*   **`.github/`**: GitHub templates (Issue templates).

## Key Files

*   **`README.md`**: Primary documentation outlining the project's history, purpose, and installation.
*   **`app/src/main/AndroidManifest.xml`**: Defines the `LatinIME` service, permissions (Vibrate, Dictionary Access), and settings activities.
*   **`app/build.gradle`**: Specifies the Android SDK versions, dependencies (`appcompat-v7`), and NDK configuration.
*   **`app/src/main/cpp/CMakeLists.txt`**: Instructions for CMake to compile the native dictionary library.
*   **`java/AutoVersion.sh`**: A script that generates an XML resource file with the current Git tag and date, used for displaying version info.

## Building and Running

The project uses the standard Gradle build wrapper.

1.  **Build Debug APK:**
    ```bash
    ./gradlew assembleDebug
    ```
2.  **Build Release APK:**
    ```bash
    ./gradlew assembleRelease
    ```
3.  **Run Tests:**
    ```bash
    ./gradlew test
    ```

**Note:** Ensure you have the Android SDK and NDK installed. The project targets API level 26, so you may need to ensure your SDK manager has the appropriate build tools and platforms installed.

## Development Conventions

*   **Legacy Codebase:** Much of the code is derived from older AOSP versions. Be cautious when refactoring, as newer Android patterns might not align 1:1 with the existing structure.
*   **Native Code:** Performance-critical dictionary operations are handled in C++. Changes to dictionary logic will likely require touching `app/src/main/cpp`.
*   **Versioning:** Version information is seemingly automated via `java/AutoVersion.sh`.
