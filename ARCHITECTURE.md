# SIGHTGUIDE Architecture Document

## 1. Architectural Philosophy

SIGHTGUIDE follows **Clean Architecture** principles combined with **Unidirectional Data Flow (MVI/MVVM)** and strict modular decoupling. Accessibility is treated as an architectural foundation rather than an adornment:

1. **Accessibility-Driven Semantics**: Every composable surface provides meaningful TalkBack semantics, explicit role mappings, and custom accessibility actions.
2. **Deterministic State Modeling**: UI state is modeled as immutable data classes exposed via `StateFlow` from ViewModels.
3. **Hardware & Network Resilience**: Every hardware sensor (Camera, GPS, Magnetometer, Speech Recognizer) is wrapped behind domain interfaces that guarantee graceful fallback when sensors are missing, permissions denied, or signals lost.
4. **Zero-Knowledge Privacy**: No location or camera data leaves the device unless explicitly consented to and cryptographically signed for caregiver dispatch.

---

## 2. Module Boundaries & Dependency Graph

```
                                      :app
                                       │
        ┌─────────────┬────────────────┼────────────────┬─────────────┐
        ▼             ▼                ▼                ▼             ▼
:feature:dashboard :feature:nav  :feature:camera  :feature:reader :feature:safety
        │             │                │                │             │
        └─────────────┴────────────────┼────────────────┴─────────────┘
                                       ▼
             ┌──────────────────────────────────────────────────┐
             │                  :core:* Modules                 │
             ├──────────────────────────────────────────────────┤
             │ :core:accessibility │ :core:audio   │ :core:location  │
             │ :core:sensors       │ :core:storage │ :core:security  │
             │ :core:designsystem  │ :core:logging │ :core:common    │
             └──────────────────────────────────────────────────┘
```

### Module Responsibilities

- **`:core:common`**: Pure Kotlin domain primitives (`AppResult<T>`, `DispatcherProvider`, `DateTime` helpers). Contains no Android UI dependencies.
- **`:core:designsystem`**: Accessible Material3 theme, high-contrast color tokens, standard 48dp+ interactive buttons, tactile touch surfaces.
- **`:core:accessibility`**: Semantic modifiers, TalkBack focus announcers, custom accessibility action coordinators, and `HapticPatternManager`.
- **`:core:audio`**: `TextToSpeechManager` (queue management, dynamic rate/pitch adjustments, priority speech), and `EarconPlayer` for spatial chimes.
- **`:core:location`**: GPS & FusedLocation client wrapper, Kalman filter for pedestrian position smoothing, dead-reckoning coordinate estimator.
- **`:core:sensors`**: Magnetometer and accelerometer sensor fusion for true compass heading, step detector, and device orientation tracking.
- **`:core:storage`**: Room Database (`SightGuideDatabase`), Room DAOs, DataStore Preferences for user settings.
- **`:core:security`**: Cryptographic token generator for caregiver pairing, encrypted local storage, sanitized audit trail logging.
- **`:core:logging`**: Structured logging engine with automatic PII scrubbing (phone numbers, latitude/longitude coords).
- **`:feature:dashboard`**: Accessible home grid with tile-based navigation and high-contrast system status cards (Battery, GPS, Network, SOS).
- **`:feature:navigation`**: Pedestrian turn-by-turn guidance, distance-to-waypoint audio prompts, compass announcements, external Maps intent fallback.
- **`:feature:camera`**: CameraX preview management, on-device ML Kit object detection, proximity estimation, obstacle announcements.
- **`:feature:reader`**: Live OCR document scanning, receipt/menu/sign segmenter, tactile audio controls (play/pause/repeat/jump).
- **`:feature:nearby`**: Geohash spatial index of essential amenities (pharmacies, hospitals, ATMs, transit) with relative bearing and distance.
- **`:feature:safety`**: 5-second countdown SOS alert, emergency contact manager, location-bearing SMS broadcaster, cancel gesture detector.
- **`:feature:voice`**: Speech recognition listener, intent tokenization, command router architecture.
- **`:feature:caregiver`**: Consent-based caregiver pairing, temporary location sharing tokens, revocable privacy dashboard.
- **`:feature:settings`**: Voice pitch/rate sliders, tactile feedback intensity tester, color contrast switcher.
