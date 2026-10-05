# Handoff: Personal Microlearning Feed

**Repo:** https://github.com/DanBiel2019/AI-generated-android
**Branch with the work:** `claude/personal-microlearning-feed-zhyxq7` (pushed, **not yet merged into `main`**)
**Written by:** a cloud Claude Code session, at the request of another session relaying a handoff ask on the user's behalf.

## What this is

A single-user Android microlearning feed (Deepstash-style): daily idea cards pulled from
books/articles the user likes, with ASCII-art visuals, TTS narration, and a thumbs-up/down/save
feedback loop that reweights future selection.

## What's done

All under `app/src/main/java/com/example/aigeneratedandroid/microlearning/`:

- **`model/`** — `UserProfile` (onboarding answers), `IdeaCard`, `DailyFeed`.
- **`data/ContentBank.kt`** — 30 hand-written idea cards across 5 topics (Business &
  Entrepreneurship, Technology & AI, Leadership, Creativity, Systems & Measurement), each with
  title, TTS-ready insight text, ASCII art, source/author attribution, and a micro-challenge.
- **`data/ProfileStore.kt`** / **`FeedbackStore.kt`** — SharedPreferences + JSON persistence.
  Feedback nudges per-topic and per-style weights (clamped 0.2–3.0 so a disliked topic never
  fully disappears).
- **`curation/ContentCurationEngine.kt`** — weighted sampling without replacement; seeded by
  the date (`LocalDate.toEpochDay()`) so the feed is stable all day and different tomorrow; 5-day
  cooldown so cards don't repeat too soon.
- **`narration/NarrationFormatter.kt`** — formats a card for Android's `TextToSpeech`.
- **`ui/DailyFeedActivity.kt`** + **`FeedPagerAdapter.kt`** — the one screen: swipeable
  `ViewPager2`, one idea per page, 👍/👎/🔖/🔊 buttons, a summary page with the day's theme
  and a challenge.
- `app/build.gradle.kts` — added `viewpager2`, `recyclerview`, and core-library desugaring
  (`desugar_jdk_libs:2.0.4`), the last one required because `java.time` (used for the
  date-seeded curation) isn't available on minSdk 24 without it.

The user's onboarding answers are seeded as the hardcoded default in `UserProfile.default()`:
loved books (Crucial Conversations/Accountability, The Phoenix Project, 7 Habits, A More
Beautiful Question), followed authors (Patterson, Kim, Covey, Berger, **Simon Sinek** — see
Gotchas), 5 topics above, story/counterintuitive learning style, podcast-flavored delivery,
10-minute sessions in 2-minute chunks, advanced depth.

## What's in progress / not started

- **No in-app onboarding UI.** The profile is only editable by hand-editing
  `UserProfile.default()` and reinstalling — there's no screen to re-run the quiz or tweak
  topics/books from the device.
- **No automated tests.** Nothing in `ContentCurationEngine`, `FeedbackStore`, etc. has unit
  test coverage yet.
- **Visuals are ASCII-art text only** — no generated or illustrated graphics, per the original
  spec's "ASCII art or description" option.
- **Build has never been verified end-to-end.** See Gotchas — this needs a real run of
  `./gradlew assembleDebug` on a machine with the Android SDK.

## Next steps (suggested, not decided)

1. Run `./gradlew assembleDebug` on a machine with the real Android SDK and fix whatever the
   cloud sandbox couldn't catch (it has no SDK and no network path to `dl.google.com`, so the
   Gradle Android plugin itself couldn't even resolve there — see Gotchas).
2. Sanity-check the feed on a device/emulator: does `ViewPager2` paging feel right, does TTS
   actually speak, do the feedback buttons visibly persist across app restarts.
3. Decide if/when to merge `claude/personal-microlearning-feed-zhyxq7` into `main` (not done —
   no PR has been opened for it).
4. If wanted: build the onboarding-edit screen, add more `ContentBank` cards over time, consider
   swapping SharedPreferences+JSON for Room if the data model grows.

## Decisions & gotchas

- **minSdk is 24** but the curation engine uses `java.time.LocalDate` for deterministic daily
  seeding — required adding `isCoreLibraryDesugaringEnabled = true` plus the
  `desugar_jdk_libs` dependency in `app/build.gradle.kts`. Without that, this won't compile on
  minSdk 24.
- **Feed selection is deterministic per calendar day** (`Random(date.toEpochDay())`), not
  per-launch — reopening the app the same day should show the same 5–10 cards.
- **Mystery-author correction:** the first pass guessed the user's half-remembered "motivational
  speaker with glasses, Global Leadership Summit" author as John Maxwell. The user corrected
  this to **Simon Sinek** (commit `a6e7bd2`) — the Leadership topic now has two Sinek cards
  (*Start With Why*, *Leaders Eat Last*) instead of a wrong guess.
- **The cloud sandbox this was built in has no Android SDK and the Gradle Android plugin
  couldn't be resolved** (no reachable path to Google's Maven in that environment) — every file
  was reviewed by hand, but `assembleDebug` has never actually been run against this code. Don't
  assume it builds clean; budget time to fix compile errors on first real build.
- **No PR has been opened.** The branch is pushed to `origin` but this cloud session did not
  merge or PR it into `main` — that was a deliberate choice (branch-push rules for this session
  require explicit user permission before touching `main`), not an oversight.
