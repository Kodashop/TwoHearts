***Relationship Setup implemented***

Files added:
- app/src/main/java/com/synthlabs/twohearts/ui/onboarding/RelationshipSetupActivity.java
- app/src/main/res/layout/activity_relationship_setup.xml
- app/src/main/res/values/relationship_strings.xml

Files modified:
- TWOHEARTS_BUILD_PROGRESS.md (appended summary of Relationship Setup completion)

Functionality implemented:
- Full Relationship Setup screen with inputs:
  - Space name (required)
  - Greeting (optional)
  - Start date (DatePicker)
  - Relationship status (Spinner with options)
- Validation: space name required; start date required.
- Persistence: saves relationship via ProfileRepository.saveRelationship(..) into the existing relationship table (id=1). Restores existing values when reopened via ProfileRepository.getRelationship().
- Navigation: after successful save navigates to MainActivity (clearing back stack). Back navigation returns to the previous onboarding screen if invoked.
- UI uses native Android XML and theme attributes; no external dependencies added.

Notes:
- Relationship status options are defined in relationship_strings.xml. If you prefer different wording or additional options, tell me and I will update them.
- I intentionally navigate to MainActivity after save, consistent with the previous ProfileSetup flow. We can instead continue to Personalization Setup when ready.

Next steps:
- Run a local build and test the flow: Splash -> Welcome -> Profile Setup -> Relationship Setup -> MainActivity.
- After you confirm, I'll implement Personalization Setup or adjust navigation if you want the flow to continue to that next.
