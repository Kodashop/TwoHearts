# TwoHearts — Build Progress

Native Android app (Java + SQLite, offline-first). This file is the handover
record: if a build session ends, the next one starts by reading this.

Repo: `synthlabsdigital-cmyk/2Hearts2` (branch `master`)
Package: `com.synthlabs.twohearts` · minSdk 24 · targetSdk 34 · Java 17

---

## How to get an APK

Push to `master` and the workflow `.github/workflows/android-build.yml` builds
`app-debug.apk` and uploads it as a build artifact (Actions → latest run →
Artifacts). No local Android Studio needed.

---

## Status by layer

| Layer | State | Notes |
|---|---|---|
| Gradle build + CI | Done | root/app `build.gradle`, wrapper props, GitHub Actions APK workflow |
| Manifest | Done | all activities + 3 receivers declared |
| Design system (XML) | Done | colors (light/night), dimens, themes, styles, 34 vector icons |
| Strings | Done | ~317 strings, all user-facing copy centralised |
| Core (config/theme/text size/lock state/dates) | Done | `core/` |
| Database + migrations | Done | `data/db/TwoHeartsDatabase.java`, v1 schema, seeded rows |
| Models | Done | 15 POJOs in `data/model/` |
| Repositories | Done | profile/dates, memories, notes, timeline, places, mood, reminders, notifications, period, vault, games |
| Domain logic | Done | `PeriodEngine` (predictions/phases), `MilestoneEngine` (days together) |
| Security | Done | `CryptoBox` (Keystore AES-GCM), `PinManager` (salted hash in EncryptedSharedPreferences) |
| Notifications | Done | channels, `Notifier`, `ReminderScheduler` (AlarmManager), alarm/action/boot receivers |
| UI layouts + activities | **Next** | 77 screens from the reference set, batch by batch |

## UI build order (next sessions)

1. Splash → onboarding (permissions, welcome, profile, relationship, personalization, app lock, story date, complete)
2. `MainActivity` shell + bottom nav (Home, Us, More) + app lock screen
3. Memories (list, detail, edit) · Notes (list, edit) · Timeline
4. Relationship counter · Important dates · Places · Mood
5. Reminders · Notification centre · Search
6. Period tracker (setup, home, calendar, log, history, settings, reminders, privacy)
7. Vault (lock, home, item view/edit)
8. Games (menu, play, result, stats)
9. Settings (profile, relationship, appearance, notifications, security, storage, about)

## Owner-editable configuration

- `core/AppConfig.java` — cycle/period defaults, reminder hours, snooze length, milestone days
- `res/values/strings.xml` — every piece of visible text
- `res/values/colors.xml` (+ `values-night/`) — the whole palette

## Conventions (keep these)

- UI never touches SQL; it goes through `data/repo/*`.
- Any new schema change bumps `TwoHeartsDatabase.VERSION` and adds an
  `onUpgrade` case — never drop tables (owner data must survive updates).
- Nothing sensitive in plain preferences: PIN → `PinManager`, vault text → `CryptoBox`.
- Period tracker data stays local to the device.
