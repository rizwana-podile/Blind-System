# SIGHTGUIDE Engineering Roadmap

## Phase 1: Foundation & Architecture Scaffold (Current)
- [x] Multi-module Gradle build scaffolding with Gradle Version Catalog.
- [x] Comprehensive documentation (Architecture, Accessibility, Privacy, Security, Voice).
- [x] GitHub CI/CD, issue templates, and security scanning workflows.

## Phase 2: Core Platform & Accessibility Primitives
- [ ] Core Common: Dispatchers, `AppResult<T>`, domain contracts.
- [ ] Core Design System: High-contrast themes (WCAG AAA), tactile surfaces, large typography.
- [ ] Core Accessibility: TalkBack semantics helpers, `HapticPatternManager`.
- [ ] Core Audio: Text-To-Speech engine with queueing, custom `EarconPlayer`.

## Phase 3: Hardware Sensors, Location & Persistence
- [ ] Core Sensors: Compass orientation, accelerometer, step counting.
- [ ] Core Location: GPS client, Kalman filter smoothing.
- [ ] Core Storage: Room Database schema, DAOs, DataStore preferences.
- [ ] Core Permissions: Voice-guided accessible permission requester.

## Phase 4: Voice-First Intent Engine & Dashboard
- [ ] Voice command tokenizer, intent matcher, and action dispatcher.
- [ ] Dashboard UI: High-contrast 96dp touch tiles, active status bar.

## Phase 5: Domain Features (Navigation, Camera, OCR, Nearby, Safety)
- [ ] Walking navigation with compass announcements and Maps fallback.
- [ ] CameraX obstacle identification and spatial audio cues.
- [ ] ML Kit OCR document reader with tactile playback controls.
- [ ] Geohash-indexed offline amenity search.
- [ ] SOS 5-second countdown alert, emergency contact dialer, and safe SMS.
- [ ] Consent-based caregiver dashboard and revocable location token.
