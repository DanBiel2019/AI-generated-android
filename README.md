# AI-generated-android

A minimal Android app scaffold (Kotlin, Gradle Kotlin DSL, AndroidX).

## Building

This needs the real Android SDK (a network-connected environment, e.g. your
own machine or CI). It was not built here — see
`tools/offline-apk-demo/` for why, and for an offline fallback that produces
a (very minimal, hand-assembled) APK without it.

```
./gradlew assembleDebug
```

The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Project layout

- `app/src/main/java/com/example/aigeneratedandroid/MainActivity.kt` - the one screen
- `app/src/main/res/` - layout and strings
- `app/build.gradle.kts` - module build config (compileSdk 34, minSdk 24)

## tools/offline-apk-demo/

A proof-of-concept pipeline that builds a signed APK using only `apt`-
installable tools (no Android SDK Manager / `dl.google.com` access). It
hand-assembles app code in `smali` (Dalvik assembly) instead of compiling
Kotlin/Java, since no `d8`/`dx` compiler is available that way. Useful as a
reference/fallback, not for real development — use the Gradle project above
for that.
