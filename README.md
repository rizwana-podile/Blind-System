# SIGHTGUIDE 🦯👁️🔊

**Accessibility-First Smartphone Assistive Platform for Blind & Low-Vision Users**

[![Android CI](https://github.com/sightguide/sightguide-android/actions/workflows/android-ci.yml/badge.svg)](https://github.com/sightguide/sightguide-android/actions/workflows/android-ci.yml)
[![Accessibility Audit](https://github.com/sightguide/sightguide-android/actions/workflows/accessibility-audit.yml/badge.svg)](https://github.com/sightguide/sightguide-android/actions/workflows/accessibility-audit.yml)
[![WCAG AAA](https://img.shields.io/badge/WCAG-2.2%20AAA-brightgreen.svg)](ACCESSIBILITY.md)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

---

## Overview

**SIGHTGUIDE** is an everyday assistive Android operating layer engineered specifically for blind and low-vision individuals. Rather than a fragmented collection of separate utilities, SIGHTGUIDE unites voice interaction, pedestrian navigation, real-time computer vision obstacle assistance, text-to-speech OCR document reading, offline amenity lookup, and opt-in emergency safety dispatch into a cohesive, highly accessible experience.

### Core Capabilities

1. **Voice-First Interaction**: Natural speech commands routed through an offline-capable intent parser ("Where am I?", "Read this sign", "Find my keys", "Take me to the nearest pharmacy").
2. **Pedestrian Navigation**: Dead reckoning, compass heading announcements, step counting, geofenced waypoints, and seamless fallback to Google Maps.
3. **Camera Vision & Obstacle Awareness**: Live CameraX analysis identifying persons, vehicles, doors, stairways, and obstacles with spatial auditory and haptic warnings.
4. **Document & Text Reading (OCR)**: On-device ML Kit text recognition for mail, packaging labels, signs, restaurant menus, and receipts with full pause/repeat/skip playback controls.
5. **Nearby Amenities**: Geohashed offline database locating pharmacies, hospitals, transit stops, ATMs, and grocery stores with relative bearing and distance.
6. **Safety & SOS Dispatch**: Guarded 5-second countdown SOS alert, emergency contact direct dialer, location-bearing SMS broadcast, and battery/signal status monitoring.
7. **Consent-Based Caregiver Relay**: Opt-in, cryptographically signed, revocable location sharing with trusted family or caregivers—zero covert tracking.
8. **Offline-First Resilience**: Critical functions (compass, OCR, TTS, saved places, safety contacts) function seamlessly without an active internet connection.

---

## High-Level Architecture

```
[ User Voice / Touch Input ]
            │
            ▼
   ┌───────────────────┐
   │ SIGHTGUIDE Core   │
   │ Intent Engine &   │
   │ TalkBack Semantics│
   └────────┬──────────┘
            │
  ┌─────────┼───────────────┬────────────────┬──────────────┐
  ▼         ▼               ▼                ▼              ▼
Navigate  Camera Vision   OCR Reader      Nearby Places   Safety & SOS
(Sensors) (Obstacles)     (Text-To-Speech)(Offline POIs)  (Countdown)
```

For in-depth architectural details, see [ARCHITECTURE.md](ARCHITECTURE.md).

---

## Design System & Accessibility Standards

- **Target Compliance**: WCAG 2.2 Level AAA.
- **Visual Contrast**: 7:1 minimum contrast across all screens (Dark Slate `#0D1117` and High-Vis Safety Yellow `#FFE500` / Pure White `#FFFFFF`).
- **Touch Targets**: Minimum $48 \times 48\,\text{dp}$; primary dashboard tiles are $96 \times 96\,\text{dp}$ or full-width touch cards.
- **Haptic Profiles**: Discrete vibration patterns for turn cues, successful actions, obstacle alerts, and emergency countdowns.
- **Earcons**: Custom high-clarity acoustic chimes for system status transitions.

For details, see [ACCESSIBILITY.md](ACCESSIBILITY.md).

---

## Safety Disclaimer

> **IMPORTANT ASSISTIVE NOTICE**: SIGHTGUIDE is an assistive technology designed to augment situational awareness. It is **NOT** a certified life-safety device and does not claim guaranteed obstacle detection, collision prevention, or emergency dispatch. Users should always employ their primary mobility aids (white cane, guide dog) and established orientation techniques.

---

## Documentation Index

- [Architecture Guide](ARCHITECTURE.md)
- [Accessibility Specification](ACCESSIBILITY.md)
- [Privacy & Consent Architecture](PRIVACY.md)
- [Security Model](SECURITY.md)
- [Testing & Quality Assurance](TESTING.md)
- [Voice Commands Handbook](VOICE_COMMANDS.md)
- [Caregiver Coordination Guide](CAREGIVER_GUIDE.md)
- [Local Developer Setup](SETUP.md)
- [Roadmap & Milestones](ROADMAP.md)
- [Contribution Guidelines](CONTRIBUTING.md)
