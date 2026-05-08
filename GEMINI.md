# Hacker's Keyboard - Termux Development Notes

This project has been modernized to support Android 15 (API 35) and optimized for building within the **Termux** environment.

## 🛠 Termux Build Workflow

Due to architecture mismatches in standard Android SDK/NDK binaries (x86_64 vs AArch64), this project uses a hybrid build approach:

1.  **Manual Native Build:** The C++ code is compiled using Termux's native `clang` and `ninja`.
2.  **Gradle APK Build:** The Java/Kotlin code is built using Gradle, packaging the manually compiled `.so` library from `app/src/main/jniLibs`.

### Quick Build Command
```bash
./build_termux.sh
```

## ⚙️ Key Configurations

-   **Target/Compile SDK:** Set to **34** (Compatible with Termux system `aapt2`).
-   **Java Version:** Uses **Java 17**.
-   **AndroidX:** Project is fully migrated to AndroidX.
-   **Settings UI:** Implemented using `androidx.preference.PreferenceFragmentCompat` and `BaseSettingsActivity`.
-   **Native Build:** Custom native build for `arm64-v8a` is handled by `build_termux.sh` to bypass NDK toolchain architecture issues.

## ⚠️ Known Issues & Solutions

-   **Settings Crash:** Fixed by using `Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP` when launching settings from the `InputMethodService`.
-   **R.styleable constant error:** Converted `switch` statements to `if-else` in `LatinKeyboardBaseView.java` and `LatinKeyboardView.java` due to non-final resource IDs in library projects/modern AGP.
-   **Kotlin Duplicate Classes:** Resolved via `resolutionStrategy` in `app/build.gradle` forcing Kotlin 1.8.22.
-   **Exported Components:** All activities/services with intent filters now explicitly have `android:exported="true"`.
-   **PendingIntent:** All `PendingIntent` calls use `FLAG_IMMUTABLE`.
-   **Stripping Library:** Native library stripping is disabled (`doNotStrip`) to avoid `llvm-objcopy` architecture errors.

## 🚀 Future Roadmap
-   Continue refinement of UI/UX using Material Design components.
-   Maintain compatibility with future Android SDK releases.
