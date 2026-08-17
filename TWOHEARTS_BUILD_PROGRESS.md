***Profile & Welcome screens implemented: WelcomeActivity and ProfileSetupActivity***

Files added:
- app/src/main/java/com/synthlabs/twohearts/ui/onboarding/WelcomeActivity.java
- app/src/main/java/com/synthlabs/twohearts/ui/onboarding/ProfileSetupActivity.java
- app/src/main/res/layout/activity_welcome.xml
- app/src/main/res/layout/activity_profile_setup.xml

Files modified:
- app/src/main/java/com/synthlabs/twohearts/ui/splash/SplashActivity.java (now checks for existing profile and routes to Welcome if needed)
- TWOHEARTS_BUILD_PROGRESS.md (appended summary)

Functionality implemented:
- Welcome screen with branding, intro copy and a Get started CTA that navigates to Profile Setup.
- Profile setup screen with name (required), nickname (optional), birthday (date picker) and photo placeholder.
- Validation: name is required; if empty, shows error and blocks save.
- Persistence: saves profile using existing ProfileRepository.saveProfile, storing into local SQLite.
- Navigation: After saving, the app navigates to MainActivity. Welcome is skipped on future launches when a profile exists.
- Back navigation: back from ProfileSetup returns to Welcome; back from Welcome exits the app (default behavior).

Repositories/classes connected:
- ProfileRepository (used to get existing profile and save new profile)
- Prefs (used as a minimal indicator to mark onboarding progressed)

Notes / assumptions:
- Photo selection and storage are intentionally minimal (uses default launcher icon). If we add photo picking, we'll honor the manifest permissions and implement storage in Profile.photoUri.
- I reused Prefs.KEY_PERMISSIONS_SHOWN to record progress instead of introducing a new pref key to avoid unnecessary changes; the app determines whether to show Welcome by checking for an existing profile row in the database.

Next steps:
- Please run a build and smoke-test on a device/emulator to verify onboarding flow.
- If acceptable, I will proceed with Relationship Setup as the next screen in Phase 1.
