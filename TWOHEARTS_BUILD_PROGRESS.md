Updated UI shell: added MainActivity, SplashActivity, bottom navigation and placeholder fragments for Home/Us/Games/Notes/More. These are the first Phase 1 UI foundation screens to be wired to the existing app architecture.

Files added in this batch:
- app/src/main/java/com/synthlabs/twohearts/ui/MainActivity.java
- app/src/main/java/com/synthlabs/twohearts/ui/splash/SplashActivity.java
- app/src/main/java/com/synthlabs/twohearts/ui/home/HomeFragment.java
- app/src/main/java/com/synthlabs/twohearts/ui/us/UsFragment.java
- app/src/main/java/com/synthlabs/twohearts/ui/games/GamesFragment.java
- app/src/main/java/com/synthlabs/twohearts/ui/notes/NotesFragment.java
- app/src/main/java/com/synthlabs/twohearts/ui/more/MoreFragment.java
- app/src/main/res/layout/activity_main.xml
- app/src/main/res/layout/activity_splash.xml
- app/src/main/res/layout/fragment_home.xml
- app/src/main/res/layout/fragment_us.xml
- app/src/main/res/layout/fragment_games.xml
- app/src/main/res/layout/fragment_notes.xml
- app/src/main/res/layout/fragment_more.xml
- app/src/main/res/menu/bottom_nav_menu.xml
- app/src/main/res/values/strings_ui.xml

Notes:
- These screens are minimal, placeholder UI that follow the TwoHearts design intent and reuse the existing app theme. They are wired to the existing application class (TwoHeartsApp) and will allow the app to start and navigate between primary destinations.
- No backend/core systems were modified. Existing repositories and services will be connected when implementing each feature screen.

Next steps:
- Verify the project builds locally (./gradlew assembleDebug) and run on a device/emulator.
- Replace placeholder fragment contents with real UI for Home Dashboard and onboarding in the next incremental batch.
