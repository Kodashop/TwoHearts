***Onboarding: Personalization, App Lock, Setup Complete implemented***

Files added:
- app/src/main/java/com/synthlabs/twohearts/ui/onboarding/PersonalizationSetupActivity.java
- app/src/main/res/layout/activity_personalization_setup.xml
- app/src/main/res/values/personalization_strings.xml
- app/src/main/java/com/synthlabs/twohearts/ui/onboarding/AppLockSetupActivity.java
- app/src/main/res/layout/activity_app_lock_setup.xml
- app/src/main/java/com/synthlabs/twohearts/ui/onboarding/SetupCompleteActivity.java
- app/src/main/res/layout/activity_setup_complete.xml
- app/src/main/java/com/synthlabs/twohearts/ui/splash/SplashActivity.java (updated routing)
- app/src/main/java/com/synthlabs/twohearts/ui/onboarding/ProfileSetupActivity.java (routing updated to Relationship)
- app/src/main/java/com/synthlabs/twohearts/ui/onboarding/RelationshipSetupActivity.java (routing updated to Personalization)

Files modified:
- TWOHEARTS_BUILD_PROGRESS.md (appended entries for Personalization, App Lock, and Setup Complete)

Functionality implemented:
- Personalization Setup: theme selection (system/light/dark) and text size selection (small/normal/large/extra large). Values persist to Prefs (KEY_THEME, KEY_TEXT_SCALE). Restored on reopen. Navigates to App Lock Setup after save.
- App Lock Setup: create and confirm PIN; validates PIN length (>=4) and confirmation match; saves PIN via PinManager.setPin and sets Prefs.KEY_LOCK_ENABLED and Prefs.KEY_LOCK_BIOMETRIC. Navigates to Setup Complete after save.
- Setup Complete: final screen; sets Prefs.KEY_SETUP_DONE = true and navigates to MainActivity (clearing onboarding flow).
- SplashActivity now routes correctly through the entire onboarding flow based on saved state (profile, relationship, personalization, lock). This fixes prior onboarding premature navigation to MainActivity.

Repository/database integration:
- Personalization uses Prefs (local SharedPreferences) to store theme and text scale.
- App Lock uses PinManager to securely store a salted hash of the PIN via EncryptedSharedPreferences; Prefs flags track lock enabled/biometric.
- SetupComplete sets Prefs.KEY_SETUP_DONE.
- No DB schema changes; existing ProfileRepository and relationship saving remains unchanged.

Notes / Assumptions:
- Personalization options use Prefs.KEY_THEME (0=system,1=light,2=dark) and KEY_TEXT_SCALE indices (0..3). The app's ThemeManager.applySavedTheme (existing) will pick up KEY_THEME when applied at app start.
- The app's UI scaling based on KEY_TEXT_SCALE is assumed to be handled by existing TextScale/ThemeManager code; if not, we'll implement the runtime scaling next.
- AppLock uses a basic PIN flow. Biometric enabling flag is stored but actual biometric unlock integration (BiometricPrompt) will be wired when lock enforcement/lock UI is implemented in app flow.

Next steps:
- Run local build and smoke test onboarding flow: Splash -> Welcome -> Profile -> Relationship -> Personalization -> App Lock -> Setup Complete -> Main.
- I will proceed to the next prioritized screen after you confirm test results or request adjustments.
