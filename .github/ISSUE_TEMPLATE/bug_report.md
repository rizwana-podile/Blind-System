name: Bug Report
description: Report a defect, crash, or unexpected behavior in SIGHTGUIDE
title: "[BUG] "
labels: ["bug"]
body:
  - type: textarea
    id: description
    attributes:
      label: Bug Description
      description: A clear and concise description of what happened.
    validations:
      required: true
  - type: textarea
    id: reproduction
    attributes:
      label: Steps to Reproduce
      description: 1. Go to...\n2. Tap on...\n3. Hear error...
    validations:
      required: true
  - type: input
    id: device
    attributes:
      label: Device & Android Version
      placeholder: Pixel 7 - Android 14
    validations:
      required: true
