name: Feature Request
description: Propose an assistive feature, navigation enhancement, or voice command
title: "[FEATURE] "
labels: ["enhancement"]
body:
  - type: textarea
    id: problem
    attributes:
      label: Problem Statement
      description: What challenge or obstacle does this feature solve for blind or low-vision users?
    validations:
      required: true
  - type: textarea
    id: proposed_solution
    attributes:
      label: Proposed Solution
      description: How should the feature work, sound, vibrate, and interact?
    validations:
      required: true
  - type: textarea
    id: voice_commands
    attributes:
      label: Voice Command Examples (if applicable)
      description: e.g., "Where is the door?", "Check lighting level", "Read expiration date"
