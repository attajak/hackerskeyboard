#!/bin/bash
# Hacker's Keyboard - Termux Build Script
set -e

echo "--- 1. Building Native Library (C++) ---"
mkdir -p app/build_native
cd app/build_native
# Using native clang/clang++ and ninja in Termux
cmake -G Ninja -DCMAKE_C_COMPILER=clang -DCMAKE_CXX_COMPILER=clang++ ..
ninja
cd ../..

echo "--- 2. Installing Native Library to jniLibs ---"
mkdir -p app/src/main/jniLibs/arm64-v8a
cp app/build_native/libjni_pckeyboard.so app/src/main/jniLibs/arm64-v8a/

echo "--- 3. Building APK using Gradle ---"
# We skip the externalNativeBuild in Gradle because we built it manually
gradle assembleDebug

echo ""
echo "--- Build Finished Successfully! ---"
echo "APK Location: app/build/outputs/apk/debug/app-debug.apk"
