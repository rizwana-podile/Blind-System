# Contributing to SIGHTGUIDE

Thank you for helping build accessible assistive technology!

## Accessibility-First Guidelines
1. **Never commit unlabeled UI elements**: Every icon, button, and dynamic state change must have a meaningful `contentDescription` or TalkBack announcement.
2. **Touch Targets**: All interactive elements must measure at least $48 \times 48\,\text{dp}$.
3. **Contrast**: Colors must meet WCAG 2.2 AAA standard (7:1 ratio minimum).
4. **Offline Capability**: Features must not crash or display blank screens when network or GPS is unavailable.

## Pull Request Workflow
1. Fork the repo and create your feature branch (`feature/your-feature-name`).
2. Write unit tests for all domain logic.
3. Ensure `./gradlew test` passes cleanly.
4. Fill out the pull request template completely.
