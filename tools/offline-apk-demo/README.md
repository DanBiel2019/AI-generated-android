# Offline APK build (no Android SDK download required)

This is a proof-of-concept pipeline that produces a signed, installable APK
using only tools installable via `apt` plus the JDK — no `dl.google.com`
access (Android SDK Manager, `d8`/`dx`) needed.

It exists because the sandbox this was built in blocks network access to
Google's SDK distribution servers. It's useful as a fallback/CI recipe, but
it is **not** a substitute for real Kotlin/Java app development: there is no
`d8`/`dx` compiler available through this path, so all app logic has to be
hand-written in `smali` (Dalvik assembly) instead of Kotlin or Java. Use the
Gradle project at the repo root for actual development.

## What it demonstrates

App code (`smali/`) is assembled directly to `classes.dex`, then packaged,
aligned, and signed — skipping the usual `javac`/`kotlinc` → `d8` step
entirely.

## Requirements

```
apt-get install -y android-sdk-build-tools android-sdk-platform-23 libandroid-23-java libsmali-java
```

This provides `aapt`, `zipalign`, `apksigner`, `smali`/`baksmali`, and
`android.jar` (API 23).

## Build

```
./build.sh
```

Produces `HelloSmali-signed.apk` in this directory: a minimal launchable
activity that displays a TextView. Verify with:

```
apksigner verify --print-certs HelloSmali-signed.apk
aapt dump badging HelloSmali-signed.apk
```
