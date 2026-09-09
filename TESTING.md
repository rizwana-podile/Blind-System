# SIGHTGUIDE Testing & Verification Protocol

## 1. Automated Test Architecture

The testing suite consists of 4 distinct test layers:

### A. Core Unit Tests (`src/test/java`)
- **Domain Logic**: Mathematical verification of compass heading conversions, Kalman filter coordinates, bearing calculations, geohash spatial lookups, and voice command tokenization.
- **Persistence**: In-memory Room DB tests validating queries, cascade deletions, and index lookups.
- **StateFlow & MVI**: Turbine testing of ViewModels verifying emitted state transitions and one-shot UI events.

### B. Compose Accessibility & Semantics Tests (`src/androidTest/java`)
- Automated verification that every interactive composable exposes `semantics.contentDescription` or `semantics.text`.
- Minimum bounding box assertion: Ensures all clickable components have `width >= 48.dp` and `height >= 48.dp`.

### C. Hardware Degradation Tests
- Tests simulating absence of GPS fix (fallback to last known position + dead reckoning).
- Tests simulating camera disconnection or permission denial.
- Tests simulating text-to-speech engine failure with auditory fallback tone.

---

## 2. Running Test Suites

```bash
# Run all core and feature unit tests
./gradlew test

# Run accessibility-specific tests
./gradlew :core:accessibility:test :core:designsystem:test

# Generate code coverage reports
./gradlew koverHtmlReport
```
