# SIGHTGUIDE Voice Command Handbook

SIGHTGUIDE includes a voice command engine designed to recognize natural phrasing. The assistant operates without requiring internet access for core commands.

---

## 1. General & Navigation Commands

| User Voice Query | Assistant Action | Spoken Feedback |
|---|---|---|
| *"Where am I?"* | Computes nearest reverse-geocoded address or landmark and compass direction. | *"You are near 450 Market Street, facing North-East."* |
| *"What's my direction?"* | Reads current compass heading and bearing. | *"Facing North-West, 315 degrees."* |
| *"Navigate to [destination]"* | Computes pedestrian route to saved place or amenity. | *"Starting walking route to [destination]. Turn slightly right in 20 meters."* |
| *"How far is it?"* | Reads remaining distance and estimated walking time. | *"Destination is 180 meters away, approximately 2 minutes walking."* |
| *"Repeat that"* / *"Repeat"* | Replays the last spoken guidance instruction. | (Repeats last instruction verbatim) |
| *"Stop navigation"* | Cancels current navigation route. | *"Navigation stopped. Returning to dashboard."* |

---

## 2. Camera & Obstacle Recognition Commands

| User Voice Query | Assistant Action | Spoken Feedback |
|---|---|---|
| *"What is in front of me?"* / *"Describe"* | Analyzes active camera frame for obstacles, persons, and terrain. | *"A person is standing 3 meters ahead on your left. Clear path on the right."* |
| *"Find my [object]"* (e.g. bottle, keys) | Locks vision detector onto target object category. | *"Searching for bottle. Pan camera slowly to the right... Bottle detected 1 meter ahead."* |
| *"Are there stairs?"* | Runs edge/stairway detection heuristic. | *"No stairs detected in the current view."* |

---

## 3. Reader (OCR) Commands

| User Voice Query | Assistant Action | Spoken Feedback |
|---|---|---|
| *"Read this"* / *"Read document"* | Takes snapshot, runs ML Kit text recognition, starts reading. | *"Document detected: [Reads recognized text with continuous speech]."* |
| *"Read the sign"* | Scans for prominent text bounding boxes. | *"Sign reads: Exit to 5th Avenue."* |
| *"Pause"* / *"Resume"* | Toggles speech reading playback. | *"Reading paused."* / *"Resuming reading."* |
| *"Stop reading"* | Clears reading queue and returns to reader view. | *"Reading stopped."* |

---

## 4. Nearby Places Commands

| User Voice Query | Assistant Action | Spoken Feedback |
|---|---|---|
| *"What's around me?"* | Lists nearest 3 amenities within 500 meters. | *"Nearest places: Walgreens Pharmacy, 120 meters North; Subway Restaurant, 200 meters East."* |
| *"Find nearest pharmacy"* | Queries offline database for pharmacy category. | *"Nearest pharmacy is CVS Pharmacy, 140 meters away. Would you like to navigate there?"* |
| *"Find an ATM"* | Queries offline database for ATM/bank category. | *"Chase ATM is 90 meters ahead on your right."* |

---

## 5. Safety & Emergency Commands

| User Voice Query | Assistant Action | Spoken Feedback |
|---|---|---|
| *"Call my emergency contact"* | Dials primary trusted contact with confirmation. | *"Calling primary contact: Jane Doe."* |
| *"Emergency"* / *"SOS"* | Starts 5-second countdown with warning rumble. | *"SOS triggered. Dispatching emergency alert in 5 seconds. Say 'Cancel' to abort."* |
| *"Cancel SOS"* | Aborts pending emergency dispatch. | *"Emergency alert cancelled."* |
| *"Share my location"* | Enables consent-based caregiver location relay. | *"Location sharing enabled with trusted caregiver Jane Doe."* |
| *"Stop sharing location"* | Revokes caregiver location relay immediately. | *"Location sharing has been stopped."* |
