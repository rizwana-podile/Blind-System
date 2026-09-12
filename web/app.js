/**
 * SIGHTGUIDE — User & Family Dual Assistive Platform
 * Real-Time Cross-Dashboard Sync, Web Speech API, Web Audio Synthesizer,
 * Live Map Visualizer, Camera Vision AI, OCR Reader & Emergency SOS.
 */

// Application State
const state = {
  activeDashboard: 'user', // 'user' | 'family' | 'dual'
  activeUserPanel: 'panel-navigate',
  speechRate: 1.0,
  speechPitch: 1.0,
  isHighContrastDark: true,
  isAudioEnabled: true,
  isVoiceListening: false,
  lastSpokenText: 'Welcome to SIGHTGUIDE. Voice assistant and accessible dashboard ready.',
  
  // Navigation & Geolocation State
  userLocation: {
    lat: 37.7749,
    lng: -122.4194,
    address: 'Near 450 Market Street, San Francisco, CA',
    heading: 358,
    speedMps: 1.2,
    accuracyM: 3
  },
  activeDestination: {
    name: 'Community Pharmacy',
    distanceM: 120,
    clock: "1 o'clock",
    instruction: "Walking to Community Pharmacy: 120m at 1 o'clock."
  },
  
  // Camera & Vision
  isCameraActive: false,
  cameraStream: null,
  detectedObjects: [
    { label: 'Person', confidence: 0.94, distance: '2.0m ahead left', box: [40, 120, 110, 190] },
    { label: 'Water Bottle', confidence: 0.89, distance: '1.2m at 1 o\'clock', box: [180, 150, 70, 90] },
    { label: 'Path', confidence: 0.98, distance: 'Clear straight ahead', box: [80, 200, 180, 50] }
  ],
  
  // OCR Document Reader
  activeDocSample: 'medicine',
  readerIsPlaying: false,
  readerIsPaused: false,
  
  // Safety & Emergency SOS
  isSosActive: false,
  sosCountdownSeconds: 5,
  sosCountdownInterval: null,
  sosAlarmInterval: null,
  isLocationSharingActive: true,
  
  // Pairing & Audit
  pairingCode: 'H7-9K2',
  
  // Feed
  notifications: [
    {
      id: 1,
      type: 'info',
      icon: '🛡️',
      title: 'Consent-Based Location Sharing Active',
      detail: 'Secure encrypted stream connected to Family Dashboard #JD-8942.',
      time: '1:00 PM'
    },
    {
      id: 2,
      type: 'info',
      icon: '🔋',
      title: 'Device Battery Healthy',
      detail: 'User phone at 88% capacity. Optimized power mode engaged.',
      time: '1:05 PM'
    }
  ],
  
  // Voice Memos
  memos: [
    {
      id: 1,
      title: 'Doctor Appointment Note',
      time: 'Yesterday at 4:15 PM',
      text: 'Reminder: Dr. Miller appointment on Thursday at 2 PM. Take medical prescription card.'
    },
    {
      id: 2,
      title: 'Grocery List',
      time: 'Today at 10:30 AM',
      text: 'Need whole wheat bread, chamomile tea, organic oats, and honey.'
    }
  ]
};

// Amenities Database
const amenitiesData = [
  { id: 1, name: "Community Pharmacy", category: "pharmacy", distance: 120, clock: "1 o'clock", address: "142 Health Ave", hours: "Open 24/7", icon: "💊" },
  { id: 2, name: "Metro Transit Central", category: "transit", distance: 180, clock: "11 o'clock", address: "4th & Market St", hours: "5 AM - 1 AM", icon: "🚇" },
  { id: 3, name: "City General Hospital", category: "hospital", distance: 320, clock: "2 o'clock", address: "500 Civic Blvd", hours: "Emergency 24/7", icon: "🏥" },
  { id: 4, name: "Accessible ATM & Bank", category: "bank", distance: 90, clock: "12 o'clock", address: "220 Commerce Way", hours: "ATM 24 Hours", icon: "🏧" },
  { id: 5, name: "Fresh Harvest Grocery", category: "grocery", distance: 240, clock: "10 o'clock", address: "88 Green Street", hours: "7 AM - 10 PM", icon: "🥦" },
  { id: 6, name: "Sunset Clinic & Pharmacy", category: "pharmacy", distance: 350, clock: "3 o'clock", address: "890 Sunset Blvd", hours: "8 AM - 9 PM", icon: "💊" }
];

// Sample Documents for Reader
const sampleDocuments = {
  medicine: {
    type: "Document: Prescription Medicine",
    highlight: "Exp: 11/2027",
    text: "ACETAMINOPHEN 500 MG\nPAIN RELIEVER / FEVER REDUCER\nDOSAGE: Take 1 tablet every 6 hours with a full glass of water.\nDO NOT EXCEED 4 TABLETS IN 24 HOURS.\nEXPIRATION DATE: 11/2027\nBATCH: #AC-99214\nKEEP OUT OF REACH OF CHILDREN."
  },
  receipt: {
    type: "Document: Store Receipt",
    highlight: "Total: $14.64",
    text: "WALGREENS STORE #4829\n1 ASPIRIN 325MG     $8.99\n1 COTTON BANDAGES   $4.50\nSUBTOTAL           $13.49\nTAX                 $1.15\nTOTAL:             $14.64\nTHANK YOU FOR SHOPPING WITH US!"
  },
  menu: {
    type: "Document: Cafe Menu",
    highlight: "Entrees from $16",
    text: "DAILY LUNCH SPECIALS\n1. Vegetable Spring Rolls - $7.50\n2. Steamed Edamame - $6.00\n3. Teriyaki Salmon Bowl - $19.50\n4. Tofu Veggie Stir-Fry - $16.00\nBEVERAGES:\nGreen Tea, Sparkling Mineral Water"
  },
  sign: {
    type: "Document: Caution Sign",
    highlight: "Caution: Wet Floor Ahead",
    text: "CAUTION\nWET FLOOR AHEAD\nPLEASE USE HANDRAIL AND WATCH YOUR STEP\nMAINTENANCE IN PROGRESS"
  }
};

/* ==========================================================================
   1. AUDITORY SYNTHESIZER & EARCONS (Web Audio API)
   ========================================================================== */
let audioCtx = null;

function getAudioContext() {
  if (!audioCtx) {
    const AudioContextClass = window.AudioContext || window.webkitAudioContext;
    if (AudioContextClass) {
      audioCtx = new AudioContextClass();
    }
  }
  if (audioCtx && audioCtx.state === 'suspended') {
    audioCtx.resume();
  }
  return audioCtx;
}

function playTone(freq, type, durationMs, gainLevel = 0.15) {
  if (!state.isAudioEnabled) return;
  try {
    const ctx = getAudioContext();
    if (!ctx) return;
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();

    osc.type = type;
    osc.frequency.setValueAtTime(freq, ctx.currentTime);

    gain.gain.setValueAtTime(gainLevel, ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + (durationMs / 1000));

    osc.connect(gain);
    gain.connect(ctx.destination);

    osc.start();
    osc.stop(ctx.currentTime + (durationMs / 1000));
  } catch (e) {
    console.warn("Audio earcon error:", e);
  }
}

const earcons = {
  listeningStart() {
    playTone(440, 'sine', 90, 0.18);
    setTimeout(() => playTone(660, 'sine', 130, 0.2), 80);
  },
  commandRecognized() {
    playTone(523.25, 'sine', 90, 0.18);
    setTimeout(() => playTone(783.99, 'sine', 160, 0.22), 80);
  },
  checkinPing() {
    playTone(587.33, 'triangle', 140, 0.22);
    setTimeout(() => playTone(880, 'sine', 240, 0.25), 120);
  },
  hazardAlert() {
    playTone(320, 'sawtooth', 140, 0.28);
    setTimeout(() => playTone(320, 'sawtooth', 140, 0.28), 160);
  },
  sosAlarmBeep() {
    playTone(880, 'sawtooth', 180, 0.35);
    setTimeout(() => playTone(440, 'sawtooth', 180, 0.35), 180);
  }
};

/* ==========================================================================
   2. HUMANIZED SPEECH SYNTHESIS ENGINE (Web Speech API)
   ========================================================================== */
function speak(text, onEndCallback = null) {
  if (!text) return;
  state.lastSpokenText = text;

  // Update live announcer for screen readers
  const announcer = document.getElementById('live-announcer');
  if (announcer) {
    announcer.textContent = text;
  }

  // Update voice transcript bar
  const transcript = document.getElementById('voice-transcript-content');
  if (transcript) {
    transcript.textContent = `"${text}"`;
  }

  if (!('speechSynthesis' in window) || !state.isAudioEnabled) {
    if (onEndCallback) onEndCallback();
    return;
  }

  try {
    window.speechSynthesis.cancel();
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.rate = state.speechRate;
    utterance.pitch = state.speechPitch;

    // Pick a natural voice if available
    const voices = window.speechSynthesis.getVoices();
    if (voices && voices.length > 0) {
      const preferred = voices.find(v => v.lang.startsWith('en') && (v.name.includes('Natural') || v.name.includes('Google') || v.name.includes('Samantha')));
      if (preferred) utterance.voice = preferred;
    }

    if (onEndCallback) {
      utterance.onend = onEndCallback;
      utterance.onerror = onEndCallback;
    }

    window.speechSynthesis.speak(utterance);
  } catch (err) {
    console.warn("Speech synthesis error:", err);
    if (onEndCallback) onEndCallback();
  }
}

/* ==========================================================================
   3. SPEECH RECOGNITION (Voice Assistant Input)
   ========================================================================== */
let speechRecognizer = null;

function setupSpeechRecognition() {
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
  if (SpeechRecognition) {
    speechRecognizer = new SpeechRecognition();
    speechRecognizer.continuous = false;
    speechRecognizer.interimResults = false;
    speechRecognizer.lang = 'en-US';

    speechRecognizer.onstart = () => {
      state.isVoiceListening = true;
      updateVoiceHeroUI();
      earcons.listeningStart();
    };

    speechRecognizer.onresult = (event) => {
      const transcript = event.results[0][0].transcript.trim().toLowerCase();
      console.log("Voice transcript received:", transcript);
      handleVoiceCommand(transcript);
    };

    speechRecognizer.onerror = (event) => {
      console.warn("Speech recognition error:", event.error);
      state.isVoiceListening = false;
      updateVoiceHeroUI();
    };

    speechRecognizer.onend = () => {
      state.isVoiceListening = false;
      updateVoiceHeroUI();
    };
  }
}

function toggleVoiceListening() {
  getAudioContext();
  if (state.isVoiceListening) {
    if (speechRecognizer) speechRecognizer.stop();
    state.isVoiceListening = false;
    updateVoiceHeroUI();
  } else {
    if (speechRecognizer) {
      try {
        speechRecognizer.start();
      } catch (e) {
        promptSimulatedVoice();
      }
    } else {
      promptSimulatedVoice();
    }
  }
}

function promptSimulatedVoice() {
  state.isVoiceListening = true;
  updateVoiceHeroUI();
  earcons.listeningStart();

  const sampleCommands = [
    "where am i",
    "navigate to pharmacy",
    "describe scene",
    "read document",
    "trigger emergency sos"
  ];
  const chosen = prompt("Voice Assistant Microphone Simulation:\nType a command or press OK for sample:\n- where am i\n- navigate to pharmacy\n- describe scene\n- read document\n- emergency sos", sampleCommands[0]);
  
  setTimeout(() => {
    state.isVoiceListening = false;
    updateVoiceHeroUI();
    if (chosen) {
      handleVoiceCommand(chosen.trim().toLowerCase());
    }
  }, 400);
}

function handleVoiceCommand(cmd) {
  earcons.commandRecognized();
  const label = document.getElementById('voice-transcript-content');
  if (label) label.textContent = `"${cmd}"`;

  if (cmd.includes('where am i') || cmd.includes('location')) {
    speak(`You are near 450 Market Street, facing ${getHeadingName(state.userLocation.heading)}.`);
  } else if (cmd.includes('navigate') || cmd.includes('pharmacy') || cmd.includes('walk')) {
    switchUserSubpanel('panel-navigate');
    speak("Navigating to Community Pharmacy. 120 meters at 1 o'clock. Walk forward.");
  } else if (cmd.includes('camera') || cmd.includes('describe') || cmd.includes('see')) {
    switchUserSubpanel('panel-camera');
    describeCurrentScene();
  } else if (cmd.includes('read') || cmd.includes('ocr') || cmd.includes('text') || cmd.includes('document')) {
    switchUserSubpanel('panel-reader');
    readCurrentDocument();
  } else if (cmd.includes('nearby') || cmd.includes('places') || cmd.includes('store')) {
    switchUserSubpanel('panel-nearby');
    speak("Found 6 nearby amenities. Community Pharmacy is closest at 120 meters at 1 o'clock.");
  } else if (cmd.includes('sos') || cmd.includes('help') || cmd.includes('emergency')) {
    switchUserSubpanel('panel-safety');
    startSosCountdown();
  } else if (cmd.includes('stop') || cmd.includes('cancel')) {
    window.speechSynthesis.cancel();
    if (state.isSosActive) cancelSosCountdown();
    speak("Action stopped.");
  } else {
    speak(`I heard: "${cmd}". Say where am I, navigate to pharmacy, describe scene, read document, or emergency SOS.`);
  }
}

function updateVoiceHeroUI() {
  const btn = document.getElementById('btn-user-voice');
  const mainLabel = document.getElementById('voice-button-label');
  const subLabel = document.getElementById('voice-button-sub');

  if (state.isVoiceListening) {
    btn?.classList.add('listening');
    if (mainLabel) mainLabel.textContent = "Listening... Speak Now";
    if (subLabel) subLabel.textContent = "Speak clearly into your microphone";
  } else {
    btn?.classList.remove('listening');
    if (mainLabel) mainLabel.textContent = "Tap to Speak to SIGHTGUIDE";
    if (subLabel) subLabel.textContent = '"Where am I?", "Navigate to pharmacy", "Read this"';
  }
}

/* ==========================================================================
   4. NAVIGATION & COMPASS
   ========================================================================== */
function getHeadingName(degrees) {
  const deg = (degrees + 360) % 360;
  if (deg >= 337.5 || deg < 22.5) return 'North';
  if (deg >= 22.5 && deg < 67.5) return 'Northeast';
  if (deg >= 67.5 && deg < 112.5) return 'East';
  if (deg >= 112.5 && deg < 157.5) return 'Southeast';
  if (deg >= 157.5 && deg < 202.5) return 'South';
  if (deg >= 202.5 && deg < 247.5) return 'Southwest';
  if (deg >= 247.5 && deg < 292.5) return 'West';
  return 'Northwest';
}

function updateCompassUI() {
  const needle = document.getElementById('compass-needle');
  const headingText = document.getElementById('compass-heading-text');
  const famHeading = document.getElementById('fam-heading-val');
  const pinArrow = document.getElementById('pin-bearing-arrow');

  const heading = Math.round(state.userLocation.heading);
  const dirName = getHeadingName(heading).toUpperCase();

  if (needle) {
    needle.style.transform = `rotate(${heading}deg)`;
  }
  if (headingText) {
    headingText.textContent = `${dirName} ${heading}°`;
  }
  if (famHeading) {
    famHeading.textContent = `${dirName} (${heading}°)`;
  }
  if (pinArrow) {
    pinArrow.style.transform = `rotate(${heading}deg)`;
  }
}

function turnCompass(delta) {
  state.userLocation.heading = (state.userLocation.heading + delta + 360) % 360;
  updateCompassUI();
  const dir = getHeadingName(state.userLocation.heading);
  speak(`Turned. Now facing ${dir}, ${Math.round(state.userLocation.heading)} degrees.`);
}

/* ==========================================================================
   5. CAMERA & VISION AI SIMULATOR
   ========================================================================== */
function initCamera() {
  const video = document.getElementById('camera-video');
  const canvas = document.getElementById('camera-canvas');
  const badge = document.getElementById('camera-badge');

  if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
    navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } })
      .then(stream => {
        state.cameraStream = stream;
        state.isCameraActive = true;
        if (video) {
          video.srcObject = stream;
          video.play();
        }
        if (badge) badge.textContent = "Vision AI: Live Camera Active";
        drawCameraBoundingBoxes();
      })
      .catch(err => {
        console.log("Webcam unavailable or permission denied, using simulated vision feed.");
        state.isCameraActive = false;
        if (badge) badge.textContent = "Vision AI: Simulated Feed";
        drawCameraBoundingBoxes();
      });
  } else {
    drawCameraBoundingBoxes();
  }
}

function toggleCameraStream() {
  const video = document.getElementById('camera-video');
  const badge = document.getElementById('camera-badge');

  if (state.cameraStream) {
    state.cameraStream.getTracks().forEach(t => t.stop());
    state.cameraStream = null;
    state.isCameraActive = false;
    if (video) video.srcObject = null;
    if (badge) badge.textContent = "Vision AI: Simulation Mode";
    speak("Camera feed toggled to simulated sensor mode.");
    drawCameraBoundingBoxes();
  } else {
    initCamera();
    speak("Camera activated.");
  }
}

function drawCameraBoundingBoxes() {
  const canvas = document.getElementById('camera-canvas');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  canvas.width = canvas.parentElement.clientWidth || 400;
  canvas.height = canvas.parentElement.clientHeight || 220;

  ctx.clearRect(0, 0, canvas.width, canvas.height);

  // If camera is not live, draw a simulated realistic urban street environment
  if (!state.isCameraActive) {
    // Simulated pavement & path
    const grad = ctx.createLinearGradient(0, 0, 0, canvas.height);
    grad.addColorStop(0, '#1a2332');
    grad.addColorStop(1, '#0b1118');
    ctx.fillStyle = grad;
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    // Walking path guide lines
    ctx.strokeStyle = 'rgba(255, 229, 0, 0.4)';
    ctx.lineWidth = 3;
    ctx.setLineDash([8, 8]);
    ctx.beginPath();
    ctx.moveTo(canvas.width * 0.25, canvas.height);
    ctx.lineTo(canvas.width * 0.45, canvas.height * 0.35);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(canvas.width * 0.75, canvas.height);
    ctx.lineTo(canvas.width * 0.55, canvas.height * 0.35);
    ctx.stroke();
    ctx.setLineDash([]);
  }

  // Draw object bounding boxes
  // 1. Person Box
  ctx.strokeStyle = '#FFE500';
  ctx.lineWidth = 2.5;
  ctx.strokeRect(30, 40, 90, 140);
  ctx.fillStyle = 'rgba(255, 229, 0, 0.85)';
  ctx.fillRect(30, 20, 90, 20);
  ctx.fillStyle = '#000000';
  ctx.font = 'bold 11px sans-serif';
  ctx.fillText('Person 94%', 35, 34);

  // 2. Water Bottle / Small Item
  ctx.strokeStyle = '#388BFD';
  ctx.strokeRect(canvas.width - 120, 90, 50, 80);
  ctx.fillStyle = 'rgba(56, 139, 253, 0.85)';
  ctx.fillRect(canvas.width - 120, 72, 80, 18);
  ctx.fillStyle = '#FFFFFF';
  ctx.fillText('Bottle 89%', canvas.width - 116, 85);
}

function describeCurrentScene() {
  const desc = "A person is standing 2 meters ahead on your left. A water bottle is on a table 1.2 meters at 1 o'clock. Walking path ahead is clear.";
  const textEl = document.getElementById('scene-description-text');
  if (textEl) textEl.textContent = desc;
  speak(desc);
}

function findBottleTarget() {
  speak("Locating water bottle. Target detected 1.2 meters ahead at 1 o'clock on table surface.");
}

function checkStairs() {
  speak("Scanning terrain for steps and elevation. No stairs or curb drop-offs detected within 4 meters. Pavement is level.");
}

/* ==========================================================================
   6. OCR & DOCUMENT READER
   ========================================================================== */
function loadDocumentSample(key) {
  const doc = sampleDocuments[key];
  if (!doc) return;
  state.activeDocSample = key;

  const typePill = document.getElementById('doc-type-pill');
  const highlightPill = document.getElementById('doc-highlight-pill');
  const ocrContent = document.getElementById('ocr-text-content');

  if (typePill) typePill.textContent = doc.type;
  if (highlightPill) highlightPill.textContent = doc.highlight;
  if (ocrContent) ocrContent.textContent = doc.text;

  document.querySelectorAll('.sample-pill').forEach(pill => {
    pill.classList.toggle('active', pill.getAttribute('data-sample') === key);
  });

  speak(`Loaded ${doc.type}.`);
}

function readCurrentDocument() {
  const doc = sampleDocuments[state.activeDocSample];
  if (!doc) return;
  state.readerIsPlaying = true;
  state.readerIsPaused = false;
  earcons.commandRecognized();
  speak(`Reading ${doc.type}. ${doc.text.replace(/\n/g, '. ')}`, () => {
    state.readerIsPlaying = false;
  });
}

function pauseReader() {
  if (window.speechSynthesis.speaking && !window.speechSynthesis.paused) {
    window.speechSynthesis.pause();
    state.readerIsPaused = true;
    speak("Paused.");
  }
}

function stopReader() {
  window.speechSynthesis.cancel();
  state.readerIsPlaying = false;
  state.readerIsPaused = false;
  speak("Stopped document reading.");
}

/* ==========================================================================
   7. NEARBY AMENITIES
   ========================================================================== */
function renderAmenities(filterCategory = 'all') {
  const list = document.getElementById('amenity-list-container');
  if (!list) return;
  list.innerHTML = '';

  const filtered = filterCategory === 'all'
    ? amenitiesData
    : amenitiesData.filter(a => a.category === filterCategory);

  filtered.forEach(item => {
    const card = document.createElement('div');
    card.className = 'amenity-item';
    card.innerHTML = `
      <div class="amenity-icon">${item.icon}</div>
      <div class="amenity-info">
        <div class="amenity-name">${item.name}</div>
        <div class="amenity-meta">${item.distance}m • At ${item.clock}</div>
        <div class="amenity-address">${item.address} • ${item.hours}</div>
      </div>
      <button class="btn-acc-pill btn-primary-pill btn-nav-amenity" data-id="${item.id}" aria-label="Navigate to ${item.name}">
        🧭 Navigate
      </button>
    `;
    list.appendChild(card);
  });

  // Attach navigation listeners
  document.querySelectorAll('.btn-nav-amenity').forEach(btn => {
    btn.addEventListener('click', () => {
      const id = parseInt(btn.getAttribute('data-id'));
      const target = amenitiesData.find(a => a.id === id);
      if (target) {
        state.activeDestination = {
          name: target.name,
          distanceM: target.distance,
          clock: target.clock,
          instruction: `Walking to ${target.name}: ${target.distance}m at ${target.clock}.`
        };

        // Update guidance UI
        const guideEl = document.getElementById('guidance-text');
        const subEl = document.getElementById('guidance-sub-text');
        if (guideEl) guideEl.innerHTML = `Walking to <strong>${target.name}</strong>: ${target.distance}m at ${target.clock}.`;
        if (subEl) subEl.textContent = `Remaining: ${target.distance} meters • Approx ${Math.ceil(target.distance / 60)} min walk`;

        // Switch to Navigate panel
        switchUserSubpanel('panel-navigate');
        speak(`Route set to ${target.name}. ${target.distance} meters at ${target.clock}. Walk straight.`);

        // Add real-time notification to Family dashboard
        addFamilyNotification(
          'info',
          '🧭',
          'User Destination Updated',
          `User started walking navigation to ${target.name} (${target.distance}m away).`
        );
      }
    });
  });
}

/* ==========================================================================
   8. EMERGENCY SOS & SAFETY ENGINE (Cross-Dashboard Real-Time)
   ========================================================================== */
function startSosCountdown() {
  if (state.isSosActive) return;
  state.isSosActive = true;
  state.sosCountdownSeconds = 5;

  const card = document.getElementById('user-sos-countdown-card');
  const digit = document.getElementById('sos-countdown-digit');
  const masterBtn = document.getElementById('btn-user-trigger-sos');

  if (card) card.classList.remove('hidden');
  if (masterBtn) masterBtn.classList.add('hidden');
  if (digit) digit.textContent = state.sosCountdownSeconds;

  speak(`Emergency SOS activated. Dispatching in 5 seconds. Tap cancel to abort.`);
  earcons.hazardAlert();

  state.sosCountdownInterval = setInterval(() => {
    state.sosCountdownSeconds--;
    if (digit) digit.textContent = state.sosCountdownSeconds;

    if (state.sosCountdownSeconds > 0) {
      earcons.hazardAlert();
      speak(state.sosCountdownSeconds.toString());
    } else {
      clearInterval(state.sosCountdownInterval);
      dispatchEmergencySosAlarm();
    }
  }, 1000);
}

function cancelSosCountdown() {
  if (!state.isSosActive) return;
  clearInterval(state.sosCountdownInterval);
  clearInterval(state.sosAlarmInterval);
  state.isSosActive = false;

  const card = document.getElementById('user-sos-countdown-card');
  const masterBtn = document.getElementById('btn-user-trigger-sos');
  if (card) card.classList.add('hidden');
  if (masterBtn) masterBtn.classList.remove('hidden');

  speak("Emergency SOS cancelled. You are safe.");
  addFamilyNotification(
    'info',
    '✅',
    'Emergency Alert Cancelled by User',
    'User aborted the SOS countdown. No emergency dispatch required.'
  );
}

function dispatchEmergencySosAlarm() {
  const digit = document.getElementById('sos-countdown-digit');
  if (digit) digit.textContent = "ALARM";

  // Trigger continuous audio siren
  earcons.sosAlarmBeep();
  state.sosAlarmInterval = setInterval(() => {
    earcons.sosAlarmBeep();
  }, 1200);

  speak("CRITICAL ALERT! Emergency SOS dispatched to Family Dashboard and Caregiver Jane Doe.");

  // Broadcast to Family Dashboard in real-time
  const familyBanner = document.getElementById('family-sos-alert-banner');
  const familyTime = document.getElementById('family-sos-time-sub');
  const familyCoords = document.getElementById('family-sos-coords');
  const familyBadge = document.getElementById('family-badge-alert');

  const now = new Date().toLocaleTimeString();
  if (familyBanner) familyBanner.classList.remove('hidden');
  if (familyTime) familyTime.textContent = `User triggered SOS at ${now}`;
  if (familyCoords) familyCoords.textContent = `Location: ${state.userLocation.lat}° N, ${Math.abs(state.userLocation.lng)}° W (${state.userLocation.address})`;
  if (familyBadge) {
    familyBadge.textContent = "🚨 SOS ACTIVE";
    familyBadge.style.background = "#FF3B30";
  }

  // Prepend critical alarm notification to Family feed
  addFamilyNotification(
    'alarm',
    '🚨',
    'CRITICAL EMERGENCY SOS TRIGGERED!',
    `User initiated emergency help near 450 Market St. Immediate caregiver attention requested.`
  );
}

function acknowledgeFamilySos() {
  clearInterval(state.sosAlarmInterval);
  const familyBanner = document.getElementById('family-sos-alert-banner');
  const familyBadge = document.getElementById('family-badge-alert');
  const userCard = document.getElementById('user-sos-countdown-card');
  const userMasterBtn = document.getElementById('btn-user-trigger-sos');

  if (familyBanner) familyBanner.classList.add('hidden');
  if (familyBadge) {
    familyBadge.textContent = "Caregiver Center";
    familyBadge.style.background = "";
  }
  if (userCard) userCard.classList.add('hidden');
  if (userMasterBtn) userMasterBtn.classList.remove('hidden');

  state.isSosActive = false;

  // Speak aloud on User's device so blind user knows family acknowledged
  speak("Emergency alert has been acknowledged by your caregiver Jane Doe. Help is coordinating.");

  addFamilyNotification(
    'warning',
    '✅',
    'Emergency Alert Acknowledged',
    'Caregiver Jane Doe acknowledged the SOS alarm.'
  );
}

function toggleLocationSharing() {
  state.isLocationSharingActive = !state.isLocationSharingActive;
  const statusP = document.getElementById('user-sharing-status-p');
  const btn = document.getElementById('btn-user-toggle-sharing');
  const famConsent = document.getElementById('fam-consent-val');
  const userPill = document.getElementById('u-sharing-pill');

  if (state.isLocationSharingActive) {
    if (statusP) statusP.innerHTML = 'Status: <strong>ACTIVELY SHARING</strong> with Family Dashboard. Your location is securely transmitted.';
    if (btn) {
      btn.textContent = '🛑 Stop Sharing Location';
      btn.className = 'btn-acc-pill btn-danger-pill';
    }
    if (famConsent) famConsent.textContent = 'Active Sharing';
    if (userPill) userPill.textContent = '🛡️ Sharing: ON';
    speak("Location sharing activated. Family dashboard is receiving your live position.");
    addFamilyNotification('info', '🛡️', 'Location Sharing Resumed', 'User re-enabled consent-based location sharing.');
  } else {
    if (statusP) statusP.innerHTML = 'Status: <strong style="color:var(--accent-red)">SHARING PAUSED</strong>. Family dashboard is not receiving updates.';
    if (btn) {
      btn.textContent = '▶️ Resume Sharing Location';
      btn.className = 'btn-acc-pill btn-primary-pill';
    }
    if (famConsent) famConsent.textContent = 'Sharing Paused';
    if (userPill) userPill.textContent = '🛡️ Sharing: OFF';
    speak("Location sharing paused. Family dashboard will not track your location.");
    addFamilyNotification('warning', '⚠️', 'Location Sharing Paused by User', 'User temporarily paused location transmission.');
  }
}

/* ==========================================================================
   9. FAMILY DASHBOARD REMOTE ACTIONS
   ========================================================================== */
function sendCheckInPing() {
  earcons.checkinPing();

  // Speaks out loud on User device!
  speak("Family notification from Jane Doe: Check-in ping received. Are you doing okay? Tap anywhere or speak to reply.");

  addFamilyNotification(
    'info',
    '👋',
    'Caregiver Sent Check-In Ping',
    'Ping dispatched to user device with auditory chime.'
  );

  alert("Check-in ping sent! User device chime & voice message activated.");
}

function callUserPhone() {
  speak("Incoming direct call from caregiver Jane Doe.");
  alert("Initiating secure audio phone call to User (+1 555-0192)...");
}

function sendSpokenMessageToUser() {
  const input = document.getElementById('input-family-msg');
  if (!input) return;
  const msg = input.value.trim();
  if (!msg) {
    alert("Please type a message to speak on the user's device.");
    return;
  }

  // Play chime and speak aloud on user's device
  earcons.checkinPing();
  speak(`Message from your family member Jane Doe: "${msg}"`);

  addFamilyNotification(
    'info',
    '🗣️',
    'Spoken Voice Message Sent to User',
    `Caregiver sent audio voice memo: "${msg}"`
  );

  input.value = '';
}

function addFamilyNotification(type, icon, title, detail) {
  const list = document.getElementById('notif-feed-list');
  if (!list) return;

  const now = new Date().toLocaleTimeString();
  const item = document.createElement('div');
  item.className = `notif-item notif-${type}`;
  item.innerHTML = `
    <span class="notif-icon">${icon}</span>
    <div class="notif-body">
      <div class="notif-title">${title}</div>
      <div class="notif-detail">${detail}</div>
      <div class="notif-timestamp">Today at ${now}</div>
    </div>
  `;
  list.insertBefore(item, list.firstChild);
}

/* ==========================================================================
   10. VOICE MEMOS
   ========================================================================== */
function recordNewMemo() {
  speak("Recording voice memo. Speak your note after the chime.");
  earcons.listeningStart();

  const note = prompt("Speak or type your new voice memo:", "Call pharmacy tomorrow regarding prescription refill");
  if (note && note.trim()) {
    earcons.commandRecognized();
    const memoObj = {
      id: Date.now(),
      title: note.trim().slice(0, 24) + "...",
      time: "Just now",
      text: note.trim()
    };
    state.memos.unshift(memoObj);
    renderMemosList();
    speak(`Saved memo: "${note.trim()}".`);
  }
}

function renderMemosList() {
  const list = document.getElementById('memos-list-container');
  if (!list) return;
  list.innerHTML = '';

  state.memos.forEach(m => {
    const item = document.createElement('div');
    item.className = 'memo-item';
    item.innerHTML = `
      <span class="memo-icon">🎵</span>
      <div class="memo-info">
        <span class="memo-title">${m.title}</span>
        <span class="memo-time">${m.time}</span>
      </div>
      <button class="btn-acc-pill btn-play-memo" data-text="${m.text}" aria-label="Play memo ${m.title}">
        ▶ Play
      </button>
    `;
    list.appendChild(item);
  });

  document.querySelectorAll('.btn-play-memo').forEach(btn => {
    btn.addEventListener('click', () => {
      const text = btn.getAttribute('data-text');
      speak(text);
    });
  });
}

/* ==========================================================================
   11. LIVE MAP SIMULATOR (Walking Drift)
   ========================================================================== */
function startLiveMapSimulation() {
  const pin = document.getElementById('user-live-pin');
  const pingLabel = document.getElementById('fam-last-ping');
  let step = 0;

  setInterval(() => {
    step++;
    if (pingLabel) pingLabel.textContent = "Just now";

    // Subtle natural walking drift if user is active
    if (pin && state.isLocationSharingActive) {
      const offsetX = Math.sin(step * 0.3) * 8;
      const offsetY = Math.cos(step * 0.3) * 6;
      pin.style.transform = `translate(calc(-50% + ${offsetX}px), calc(-50% + ${offsetY}px))`;
    }
  }, 3000);
}

/* ==========================================================================
   12. VIEW & PANEL ROUTING
   ========================================================================== */
function switchUserSubpanel(panelId) {
  state.activeUserPanel = panelId;

  document.querySelectorAll('.user-subpanel').forEach(p => {
    p.classList.remove('active');
  });
  const target = document.getElementById(panelId);
  if (target) target.classList.add('active');

  document.querySelectorAll('.feat-tile').forEach(t => {
    t.classList.toggle('active', t.getAttribute('data-panel') === panelId);
  });

  if (panelId === 'panel-camera') {
    setTimeout(drawCameraBoundingBoxes, 100);
  }
}

function setDashboardMode(mode) {
  state.activeDashboard = mode;
  document.body.className = document.body.className.replace(/layout-(single|family|dual)/g, '');

  const tabUser = document.getElementById('tab-user');
  const tabFam = document.getElementById('tab-family');
  const tabDual = document.getElementById('tab-dual');

  tabUser?.classList.remove('active');
  tabFam?.classList.remove('active');
  tabDual?.classList.remove('active');

  if (mode === 'user') {
    document.body.classList.add('layout-single');
    tabUser?.classList.add('active');
    speak("Switched to Blind User Dashboard.");
  } else if (mode === 'family') {
    document.body.classList.add('layout-family');
    tabFam?.classList.add('active');
    speak("Switched to Family Caregiver Dashboard.");
  } else if (mode === 'dual') {
    document.body.classList.add('layout-dual');
    tabDual?.classList.add('active');
    speak("Dual side-by-side view enabled. Both User and Family dashboards are visible.");
  }
}

/* ==========================================================================
   13. INITIALIZATION & EVENT LISTENERS
   ========================================================================== */
window.addEventListener('DOMContentLoaded', () => {
  // 1. Audio Auto-Play Unlock Banner
  const audioBanner = document.getElementById('audio-start-banner');
  const btnActivateAudio = document.getElementById('btn-activate-audio');

  const unlockAudio = () => {
    getAudioContext();
    if (audioBanner) audioBanner.classList.add('hidden');
    earcons.commandRecognized();
    speak("Welcome to SIGHTGUIDE. Audio engine active. How can I assist your navigation today?");
  };

  btnActivateAudio?.addEventListener('click', unlockAudio);
  audioBanner?.addEventListener('click', unlockAudio);

  // 2. Master Dashboard Switcher (User vs Family vs Dual)
  document.getElementById('tab-user')?.addEventListener('click', () => setDashboardMode('user'));
  document.getElementById('tab-family')?.addEventListener('click', () => setDashboardMode('family'));
  document.getElementById('tab-dual')?.addEventListener('click', () => setDashboardMode('dual'));

  // 3. Global Action Buttons
  document.getElementById('btn-theme-toggle')?.addEventListener('click', () => {
    state.isHighContrastDark = !state.isHighContrastDark;
    if (state.isHighContrastDark) {
      document.body.classList.remove('theme-light');
      document.body.classList.add('theme-dark');
      document.getElementById('theme-name').textContent = "High-Vis Yellow";
      speak("High contrast dark theme enabled.");
    } else {
      document.body.classList.remove('theme-dark');
      document.body.classList.add('theme-light');
      document.getElementById('theme-name').textContent = "High-Contrast Blue";
      speak("High contrast light theme enabled.");
    }
  });

  document.getElementById('btn-sound-toggle')?.addEventListener('click', () => {
    state.isAudioEnabled = !state.isAudioEnabled;
    const soundIcon = document.getElementById('sound-icon');
    const soundBtn = document.getElementById('btn-sound-toggle');
    if (state.isAudioEnabled) {
      if (soundBtn) soundBtn.innerHTML = '<span id="sound-icon">🔊</span> Voice: ON';
      speak("Audio voice feedback enabled.");
    } else {
      if (soundBtn) soundBtn.innerHTML = '<span id="sound-icon">🔇</span> Voice: OFF';
      window.speechSynthesis.cancel();
    }
  });

  // 4. Voice Hero Button & Spacebar Shortcut
  document.getElementById('btn-user-voice')?.addEventListener('click', toggleVoiceListening);
  window.addEventListener('keydown', (e) => {
    if (e.code === 'Space' && e.target.tagName !== 'INPUT' && e.target.tagName !== 'BUTTON') {
      e.preventDefault();
      toggleVoiceListening();
    }
  });

  // 5. User Feature Tiles
  document.querySelectorAll('.feat-tile').forEach(tile => {
    tile.addEventListener('click', () => {
      const panelId = tile.getAttribute('data-panel');
      switchUserSubpanel(panelId);
      const name = tile.querySelector('.feat-name')?.textContent || '';
      speak(`Opening ${name}.`);
    });
  });

  // 6. Navigation Controls
  document.getElementById('btn-where-am-i')?.addEventListener('click', () => {
    speak(`You are near 450 Market Street, San Francisco, facing ${getHeadingName(state.userLocation.heading)}.`);
  });

  document.getElementById('btn-whats-direction')?.addEventListener('click', () => {
    speak(`Facing ${getHeadingName(state.userLocation.heading)}, ${Math.round(state.userLocation.heading)} degrees.`);
  });

  document.getElementById('btn-repeat-instruction')?.addEventListener('click', () => {
    speak(state.activeDestination.instruction);
  });

  document.getElementById('btn-turn-left')?.addEventListener('click', () => turnCompass(-30));
  document.getElementById('btn-turn-right')?.addEventListener('click', () => turnCompass(30));

  // 7. Camera Controls
  document.getElementById('btn-describe-scene')?.addEventListener('click', describeCurrentScene);
  document.getElementById('btn-find-bottle')?.addEventListener('click', findBottleTarget);
  document.getElementById('btn-camera-toggle')?.addEventListener('click', toggleCameraStream);
  document.getElementById('btn-stair-check')?.addEventListener('click', checkStairs);

  // 8. OCR Reader Controls
  document.getElementById('btn-read-aloud')?.addEventListener('click', readCurrentDocument);
  document.getElementById('btn-pause-ocr')?.addEventListener('click', pauseReader);
  document.getElementById('btn-repeat-ocr')?.addEventListener('click', readCurrentDocument);
  document.getElementById('btn-stop-ocr')?.addEventListener('click', stopReader);

  document.querySelectorAll('.sample-pill').forEach(pill => {
    pill.addEventListener('click', () => {
      const sample = pill.getAttribute('data-sample');
      loadDocumentSample(sample);
    });
  });

  // 9. Nearby Amenities Filter Chips
  document.querySelectorAll('.am-chip').forEach(chip => {
    chip.addEventListener('click', () => {
      document.querySelectorAll('.am-chip').forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
      const cat = chip.getAttribute('data-cat');
      renderAmenities(cat);
      speak(`Filtering nearby amenities: ${cat}`);
    });
  });

  // 10. Safety Controls
  document.getElementById('btn-user-trigger-sos')?.addEventListener('click', startSosCountdown);
  document.getElementById('btn-user-cancel-sos')?.addEventListener('click', cancelSosCountdown);
  document.getElementById('btn-user-call-primary')?.addEventListener('click', () => {
    speak("Calling primary emergency caregiver Jane Doe at +1 555-234-5678.");
  });
  document.getElementById('btn-user-toggle-sharing')?.addEventListener('click', toggleLocationSharing);

  // 11. Voice Memos Controls
  document.getElementById('btn-record-memo')?.addEventListener('click', recordNewMemo);

  // 12. Settings Controls
  const speedSlider = document.getElementById('slider-speed');
  const speedLabel = document.getElementById('speed-label');
  speedSlider?.addEventListener('input', (e) => {
    state.speechRate = parseFloat(e.target.value);
    if (speedLabel) speedLabel.textContent = `${state.speechRate.toFixed(1)}x`;
  });

  const pitchSlider = document.getElementById('slider-pitch');
  const pitchLabel = document.getElementById('pitch-label');
  pitchSlider?.addEventListener('input', (e) => {
    state.speechPitch = parseFloat(e.target.value);
    if (pitchLabel) pitchLabel.textContent = state.speechPitch.toFixed(1);
  });

  document.getElementById('btn-sample-voice')?.addEventListener('click', () => {
    speak("This is SIGHTGUIDE speaking with your customized speed and pitch settings.");
  });

  // 13. Family Dashboard Remote Actions
  document.getElementById('btn-send-checkin')?.addEventListener('click', sendCheckInPing);
  document.getElementById('btn-fam-call')?.addEventListener('click', callUserPhone);
  document.getElementById('btn-family-call-user')?.addEventListener('click', callUserPhone);
  document.getElementById('btn-family-ack-sos')?.addEventListener('click', acknowledgeFamilySos);
  document.getElementById('btn-send-spoken-msg')?.addEventListener('click', sendSpokenMessageToUser);
  document.getElementById('btn-clear-feed')?.addEventListener('click', () => {
    const feed = document.getElementById('notif-feed-list');
    if (feed) feed.innerHTML = '<div class="notif-item notif-info"><span class="notif-icon">ℹ️</span><div class="notif-body"><div class="notif-title">Feed Cleared</div><div class="notif-detail">New live events will appear here.</div></div></div>';
    speak("Family activity notifications cleared.");
  });

  // Enter key on Family Voice Message input
  document.getElementById('input-family-msg')?.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      sendSpokenMessageToUser();
    }
  });

  // 14. Initialize Components
  setupSpeechRecognition();
  updateCompassUI();
  initCamera();
  renderAmenities('all');
  renderMemosList();
  startLiveMapSimulation();
});
