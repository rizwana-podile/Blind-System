# SIGHTGUIDE Accessibility Specification

## 1. Compliance Level: WCAG 2.2 Level AAA

SIGHTGUIDE is engineered to meet and exceed WCAG 2.2 AAA guidelines for mobile applications:

| Guideline | Implementation Strategy in SIGHTGUIDE |
|---|---|
| **1.4.3 Contrast (Minimum)** & **1.4.6 Contrast (Enhanced)** | All text elements maintain a minimum contrast ratio of 7:1 against background colors. Default palette utilizes Deep Slate (`#0D1117`) and High-Vis Safety Yellow (`#FFE500`). |
| **2.5.5 Target Size (Enhanced)** | Minimum touch target size is $48 \times 48\,\text{dp}$. Primary dashboard buttons and cards are sized at $96 \times 96\,\text{dp}$ or full screen width to prevent mis-taps. |
| **1.3.1 Info and Relationships** | All visual structures are paired with full Compose Semantics, including `contentDescription`, `role`, and `stateDescription`. |
| **3.3.4 Error Prevention (Legal, Financial, Data)** | SOS dispatch and location sharing require deliberate, multi-step confirmation or a multi-second revocable countdown with continuous audio/haptic feedback. |

---

## 2. TalkBack Screen Reader Architecture

1. **Non-Visual Redundancy**: Every piece of visual status information (e.g., GPS connection strength, battery level, network state) is represented in semantics nodes.
2. **Dynamic Live Regions**: Time-critical announcements (e.g., "Approaching street crossing in 15 meters", "Obstacle ahead: stairs") utilize `LiveRegionMode.Assertive` or direct TTS high-priority queues.
3. **Custom Accessibility Actions**: Elements offer accessible custom actions rather than relying on complex gesture combinations.

---

## 3. Haptic Feedback Design System

The `HapticPatternManager` provides distinct vibration signatures for tactile feedback:

- **Action Success / Confirmation**: Short crisp double pulse (50ms on, 50ms off, 50ms on).
- **Navigation Cue (Turn Left)**: Pulse on the left / distinct ascending cadence.
- **Navigation Cue (Turn Right)**: Descending cadence.
- **Obstacle Proximity Warning**: Rapid triple buzz (80ms on, 40ms off, 80ms on, 40ms off, 80ms on).
- **SOS Countdown Pulse**: Deep rhythmic 1-second interval heartbeat vibration.

---

## 4. Auditory Feedback (Earcons)

Earcons are distinct acoustic chimes played through `EarconPlayer`:
- **Chime 1: Listening Started** (Gentle ascending chime).
- **Chime 2: Command Recognized** (High pleasant bell).
- **Chime 3: Destination / Waypoint Reached** (Harmonic triumph chime).
- **Chime 4: Caution / Hazard Detected** (Low resonant alert tone).
