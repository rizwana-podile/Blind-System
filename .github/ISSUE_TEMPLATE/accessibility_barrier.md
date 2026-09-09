name: Accessibility Barrier Report
description: Report a barrier preventing blind or low-vision users from using SIGHTGUIDE
title: "[A11Y BARRIER] "
labels: ["accessibility", "priority-high"]
body:
  - type: textarea
    id: barrier_description
    attributes:
      label: Barrier Description
      description: Describe how this barrier affects TalkBack, braille displays, speech rate, haptic cues, or contrast.
    validations:
      required: true
  - type: dropdown
    id: barrier_type
    attributes:
      label: Barrier Category
      options:
        - Screen Reader (TalkBack) Inaudible or Incorrect Announcement
        - Touch Target Too Small (< 48dp)
        - Inadequate Visual Contrast (< 7:1)
        - Missing or Confusing Audio/Haptic Feedback
        - Voice Assistant Unresponsive to Command
        - Other Accessibility Barrier
    validations:
      required: true
  - type: textarea
    id: suggested_fix
    attributes:
      label: Suggested Accessible Experience
      description: How should SIGHTGUIDE communicate this information or action?
