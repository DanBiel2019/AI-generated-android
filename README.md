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

- `app/src/main/java/com/example/aigeneratedandroid/microlearning/` - the personal
  microlearning feed system:
  - `model/` - `IdeaCard`, `UserProfile`, `DailyFeed` data classes
  - `data/` - `ContentBank` (curated idea cards), `ProfileStore` (onboarding answers,
    SharedPreferences-backed), `FeedbackStore` (thumbs up/down/save history and the
    per-topic/per-style weights the personalization engine learns from)
  - `curation/` - `ContentCurationEngine`, picks the day's cards by weighting
    `ContentBank` against the profile and feedback, date-seeded so the feed is stable
    within a day and changes the next
  - `narration/` - `NarrationFormatter`, prepares a card's text for `TextToSpeech`
  - `ui/` - `DailyFeedActivity` (the app's one screen: swipeable `ViewPager2` feed) and
    `FeedPagerAdapter`
- `app/src/main/res/` - layout and strings
- `app/build.gradle.kts` - module build config (compileSdk 34, minSdk 24)

The profile is currently seeded from a one-time onboarding conversation (see
`UserProfile.default()`); editing it in-app is the natural next step once this scaffold
is verified building on a machine with the real Android SDK.

## tools/offline-apk-demo/

A proof-of-concept pipeline that builds a signed APK using only `apt`-
installable tools (no Android SDK Manager / `dl.google.com` access). It
hand-assembles app code in `smali` (Dalvik assembly) instead of compiling
Kotlin/Java, since no `d8`/`dx` compiler is available that way. Useful as a
reference/fallback, not for real development — use the Gradle project above
for that.
