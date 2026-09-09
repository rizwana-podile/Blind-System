/**
 * SIGHTGUIDE — Interactive Local Web Simulation & Production Companion
 * Integrates Web Speech API (Synthesis & Recognition), Web Audio Earcons,
 * WebRTC Camera Stream, and WCAG 2.2 AAA Accessibility.
 */

// Application State
const state = {
  activeView: 'dashboard',
  speechRate: 1.0,
  speechPitch: 1.0,
  isHighContrastDark: true,
  isAudioChimesEnabled: true,
  isLocationSharingActive: false,
  lastSpokenUtterance: '',
  isNavigating: false,
  navDestination: null,
  navDistanceMeters: 140,
  currentHeadingDegrees: 358,
  isCameraActive: false,
  isSosActive: false,
  sosCountdownTimer: null,
  sosSecondsRemaining: 5,
  pairingCode: 'H7-9K2',
  selectedAmenityCategory: 'all',
  readerDocType: 'receipt',
  readerIsPlaying: false,
  readerIsPaused: false,
  auditTrail: [
    { event: 'SYSTEM_BOOT', time: new Date().toLocaleTimeString(), details: 'SIGHTGUIDE core initialized in offline mode' }
  ]
};

// Offline Amenities Database
const offlineAmenities = [
  { id: 1, name: "Community Pharmacy", category: "pharmacy", distance: 110, clock: "1 o'clock", address: "142 Health Ave", hours: "Open 24/7" },
  { id: 2, name: "Metro Transit Central", category: "transit", distance: 180, clock: "11 o'clock", address: "4th & Market St", hours: "5 AM - 1 AM" },
  { id: 3, name: "City General Hospital", category: "hospital", distance: 320, clock: "2 o'clock", address: "500 Civic Blvd", hours: "Emergency 24/7" },
  { id: 4, name: "Accessible ATM & Bank", category: "bank", distance: 90, clock: "12 o'clock", address: "220 Commerce Way", hours: "ATM 24 Hours" },
  { id: 5, name: "Fresh Harvest Grocery", category: "grocery", distance: 240, clock: "10 o'clock", address: "88 Green Street", hours: "7 AM - 10 PM" }
];

// Sample Documents for Reader
const sampleDocuments = {
  receipt: {
    type: "Receipt",
    highlight: "Total: $14.64",
    text: "WALGREENS STORE #4829\n1 ASPIRIN 325MG     $8.99\n1 COTTON BANDAGES   $4.50\nSUBTOTAL           $13.49\nTAX                 $1.15\nTOTAL:             $14.64\nTHANK YOU FOR SHOPPING WITH US!"
  },
  menu: {
    type: "Restaurant Menu",
    highlight: "Entrees from $18",
    text: "DAILY DINNER SPECIALS\nAPPETIZERS:\n1. Vegetable Spring Rolls - $7.50\n2. Steamed Edamame - $6.00\nENTREES:\n1. Teriyaki Salmon Bowl - $19.50\n2. Tofu Veggie Stir-Fry - $16.00\nBEVERAGES:\nGreen Tea, Sparkling Water"
  },
  medicine: {
    type: "Medicine Label",
    highlight: "Exp: 11/2027",
    text: "ACETAMINOPHEN 500 MG\nPAIN RELIEVER / FEVER REDUCER\nDOSAGE: Take 1 tablet every 6 hours with full glass of water.\nDO NOT EXCEED 4 TABLETS IN 24 HOURS.\nEXPIRATION DATE: 11/2027\nBATCH: #AC-99214"
  },
  sign: {
    type: "Safety Sign",
    highlight: "Caution: Wet Floor",
    text: "CAUTION\nWET FLOOR AHEAD\nPLEASE USE HANDRAIL AND WATCH YOUR STEP"
  }
};

/* ==========================================================================
   1. AUDITORY EARCON ENGINE (Web Audio API Synthesizer)
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
  if (!state.isAudioChimesEnabled) return;
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
    console.warn("Audio earcon error", e);
  }
}

const earcons = {
  listeningStart() {
    playTone(440, 'sine', 100, 0.15);
    setTimeout(() => playTone(660, 'sine', 120, 0.18), 80);
  },
  commandRecognized() {
    playTone(523.25, 'sine', 90, 0.15);
    setTimeout(() => playTone(783.99, 'sine', 150, 0.2), 90);
  },
  waypointReached() {
    playTone(587.33, 'triangle', 120, 0.2);
    setTimeout(() => playTone(880, 'triangle', 220, 0.25), 110);
  },
  hazardAlert() {
    playTone(320, 'sawtooth', 140, 0.25);
    setTimeout(() => playTone(320, 'sawtooth', 140, 0.25), 160);
  },
  sosBeat() {
    playTone(220, 'sawtooth', 200, 0.35);
  },
  ocrSuccess() {
    playTone(659.25, 'sine', 100, 0.18);
  }
};

/* ==========================================================================
   2. HUMANIZED SPEECH SYNTHESIS ENGINE (Web Speech API)
   ========================================================================== */
function speak(text, interrupt = true) {
  if (!text) return;
  state.lastSpokenUtterance = text;

  // Update live announcer for screen readers
  const announcer = document.getElementById('live-announcer');
  if (announcer) {
    announcer.textContent = text;
  }

  // Update footer status bar
  const footerStatus = document.getElementById('footer-status-text');
  if (footerStatus) {
    footerStatus.textContent = text;
  }

  if (!('speechSynthesis' in window)) {
    console.warn("Web SpeechSynthesis unavailable");
    return;
  }

  if (interrupt) {
    window.speechSynthesis.cancel();
  }

  const utterance = new SpeechSynthesisUtterance(text);
  utterance.rate = state.speechRate;
  utterance.pitch = state.speechPitch;

  // Select humanized natural voice if available
  const voices = window.speechSynthesis.getVoices();
  const naturalVoice = voices.find(v =>
    v.lang.startsWith('en') &&
    (v.name.includes('Natural') || v.name.includes('Google') || v.name.includes('Samantha') || v.name.includes('Siri') || v.name.includes('David'))
  ) || voices.find(v => v.lang.startsWith('en'));

  if (naturalVoice) {
    utterance.voice = naturalVoice;
  }

  window.speechSynthesis.speak(utterance);
}

function repeatLastSpeech() {
  if (state.lastSpokenUtterance) {
    speak(state.lastSpokenUtterance, true);
  } else {
    speak("Nothing to repeat.", true);
  }
}

function triggerHaptic(pattern = [40, 60, 40]) {
  if ('vibrate' in navigator) {
    try {
      navigator.vibrate(pattern);
    } catch (_) {}
  }
}

/* ==========================================================================
   3. VOICE COMMAND INTENT PARSER & ROUTER
   ========================================================================== */
function handleParsedIntent(transcript) {
  const norm = transcript.toLowerCase().trim().replace(/[?.!,]/g, '');

  earcons.commandRecognized();
  triggerHaptic([50, 50, 50]);

  // 1. Where am I?
  if (norm.includes('where am i') || norm.includes('location') || norm.includes('tell me where')) {
    speak("You are near 450 Market Street, facing " + getCompassDirectionName(state.currentHeadingDegrees) + ".");
    return;
  }

  // 2. Compass & Direction
  if (norm.includes('direction') || norm.includes('heading') || norm.includes('facing') || norm.includes('compass')) {
    const dir = getCompassDirectionName(state.currentHeadingDegrees);
    speak("Facing " + dir + ", " + Math.round(state.currentHeadingDegrees) + " degrees.");
    return;
  }

  // 3. Navigation
  if (norm.startsWith('navigate to') || norm.startsWith('take me to') || norm.startsWith('go to') || norm === 'navigate') {
    let dest = norm.replace(/(navigate to|take me to|go to)/, '').trim();
    if (!dest) dest = "Community Pharmacy";
    navigateToView('navigation');
    startWalkingNavigation(dest);
    return;
  }

  if (norm.includes('stop navigation') || norm.includes('cancel navigation') || norm.includes('end route')) {
    stopWalkingNavigation();
    navigateToView('dashboard');
    speak("Navigation stopped. Returning to dashboard.");
    return;
  }

  // 4. Camera & Scene Description
  if (norm.includes('describe') || norm.includes('what is in front') || norm.includes('what do you see') || norm.includes('surroundings')) {
    navigateToView('camera');
    describeCurrentScene();
    return;
  }

  if (norm.includes('find my') || norm.startsWith('find ')) {
    const target = norm.replace(/(find my|find)/, '').trim();
    navigateToView('camera');
    startFindingTarget(target || 'bottle');
    return;
  }

  // 5. Reader & OCR
  if (norm.includes('read this') || norm.includes('read') || norm.includes('scan text') || norm.includes('read document')) {
    navigateToView('reader');
    readCurrentDocument();
    return;
  }

  if (norm.includes('stop reading') || norm.includes('silence reader')) {
    stopReader();
    return;
  }

  if (norm.includes('pause')) {
    pauseReader();
    return;
  }

  if (norm.includes('resume')) {
    resumeReader();
    return;
  }

  // 6. Nearby
  if (norm.includes('around me') || norm.includes('nearby') || norm.includes('what is near')) {
    navigateToView('nearby');
    speakNearbyAmenities('all');
    return;
  }

  if (norm.includes('pharmacy')) {
    navigateToView('nearby');
    speakNearbyAmenities('pharmacy');
    return;
  }

  if (norm.includes('hospital') || norm.includes('clinic')) {
    navigateToView('nearby');
    speakNearbyAmenities('hospital');
    return;
  }

  // 7. Safety & SOS
  if (norm.includes('emergency') || norm === 'sos' || norm.includes('help me')) {
    navigateToView('safety');
    triggerSosCountdown();
    return;
  }

  if (norm.includes('cancel sos') || norm.includes('cancel emergency') || norm.includes('safe')) {
    cancelSosCountdown();
    return;
  }

  if (norm.includes('call contact') || norm.includes('call emergency') || norm.includes('call sister') || norm.includes('call jane')) {
    callPrimaryContact();
    return;
  }

  // 8. Repeat
  if (norm.includes('repeat') || norm.includes('say that again')) {
    repeatLastSpeech();
    return;
  }

  // 9. Settings
  if (norm.includes('settings')) {
    navigateToView('settings');
    speak("Opening accessibility settings.");
    return;
  }

  // 10. Help
  if (norm.includes('help') || norm.includes('commands') || norm.includes('what can you do')) {
    speak("You can say: Where am I, What's my direction, Navigate to pharmacy, Describe scene, Find my bottle, Read this, What's around me, or Emergency SOS.");
    return;
  }

  // Fallback
  speak("I heard: " + transcript + ". Try saying: Help, Navigate, Read this, Where am I, or Describe scene.");
}

/* ==========================================================================
   4. VOICE ASSISTANT MICROPHONE LISTENER
   ========================================================================== */
let recognition = null;
let isListening = false;

function initSpeechRecognition() {
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
  if (!SpeechRecognition) {
    console.warn("SpeechRecognition not supported in this browser");
    return null;
  }

  const rec = new SpeechRecognition();
  rec.continuous = false;
  rec.interimResults = false;
  rec.lang = 'en-US';

  rec.onstart = () => {
    isListening = true;
    updateVoiceButtonUi(true);
    earcons.listeningStart();
    triggerHaptic([30, 40, 30]);
  };

  rec.onresult = (event) => {
    const transcript = event.results[0][0].transcript;
    document.getElementById('voice-status-text').textContent = 'Heard: "' + transcript + '"';
    handleParsedIntent(transcript);
  };

  rec.onerror = (event) => {
    isListening = false;
    updateVoiceButtonUi(false);
    if (event.error === 'no-speech') {
      speak("I didn't hear anything. Tap Voice Assistant and try again.");
    } else {
      console.warn("Speech recognition error:", event.error);
    }
  };

  rec.onend = () => {
    isListening = false;
    updateVoiceButtonUi(false);
  };

  return rec;
}

function updateVoiceButtonUi(active) {
  const btn = document.getElementById('btn-voice-trigger');
  const title = document.getElementById('voice-title-text');
  const sub = document.getElementById('voice-status-text');

  if (active) {
    btn.classList.add('listening');
    title.textContent = 'Listening...';
    sub.textContent = 'Speak your command clearly now';
  } else {
    btn.classList.remove('listening');
    title.textContent = 'Voice Assistant';
    sub.textContent = 'Tap or press Space to Speak';
  }
}

function toggleVoiceListening() {
  if (!recognition) {
    recognition = initSpeechRecognition();
  }

  if (!recognition) {
    // Simulated Voice Dialog Fallback for browsers without Web Speech API
    const simulated = prompt("Type a voice command (e.g., 'Where am I?', 'Navigate to pharmacy', 'Read this', 'Describe scene', 'Emergency'):");
    if (simulated) {
      handleParsedIntent(simulated);
    }
    return;
  }

  if (isListening) {
    recognition.stop();
  } else {
    try {
      recognition.start();
    } catch (e) {
      console.warn("Recognizer start error", e);
    }
  }
}

/* ==========================================================================
   5. NAVIGATION SUBSYSTEM
   ========================================================================== */
function getCompassDirectionName(degrees) {
  const norm = ((degrees % 360) + 360) % 360;
  if (norm >= 337.5 || norm < 22.5) return "North";
  if (norm < 67.5) return "North-East";
  if (norm < 112.5) return "East";
  if (norm < 157.5) return "South-East";
  if (norm < 202.5) return "South";
  if (norm < 247.5) return "South-West";
  if (norm < 292.5) return "West";
  return "North-West";
}

function updateCompassUI() {
  const dirName = getCompassDirectionName(state.currentHeadingDegrees);
  const reading = document.getElementById('compass-reading');
  const needle = document.getElementById('compass-needle');

  if (reading) reading.textContent = `${dirName.toUpperCase()} (${Math.round(state.currentHeadingDegrees)}°)`;
  if (needle) needle.style.transform = `rotate(${state.currentHeadingDegrees}deg)`;
}

function startWalkingNavigation(destName) {
  state.isNavigating = true;
  state.navDestination = destName;
  state.navDistanceMeters = 120;

  const text = `Starting walking route to ${destName}. Target is 120 meters ahead at 1 o'clock.`;
  document.getElementById('instruction-text').textContent = `Walking to ${destName}: 120m at 1 o'clock`;
  document.getElementById('distance-sub-text').textContent = `Remaining: 120 meters • Approx 2 min walk`;

  speak(text);
  triggerHaptic([80, 80, 140]);
}

function stopWalkingNavigation() {
  state.isNavigating = false;
  state.navDestination = null;
  document.getElementById('instruction-text').textContent = "Facing North. Select destination or tap 'Where am I?'";
  document.getElementById('distance-sub-text').textContent = "Accuracy: High (3 meters)";
}

/* ==========================================================================
   6. CAMERA & OBSTACLE DETECTION SUBSYSTEM
   ========================================================================== */
let videoElement = null;
let canvasElement = null;
let animationFrameId = null;

function initCameraFeed() {
  videoElement = document.getElementById('camera-video');
  canvasElement = document.getElementById('camera-canvas');

  if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
    navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } })
      .then(stream => {
        if (videoElement) {
          videoElement.srcObject = stream;
          state.isCameraActive = true;
          document.getElementById('camera-tag').textContent = "Vision AI: Live Camera Active";
          startCanvasDetectionLoop();
        }
      })
      .catch(err => {
        console.info("Webcam not opened (or permission declined), running accessible simulated vision feed", err);
        startCanvasDetectionLoop(true);
      });
  } else {
    startCanvasDetectionLoop(true);
  }
}

function startCanvasDetectionLoop(simulated = false) {
  if (!canvasElement) return;
  const ctx = canvasElement.getContext('2d');

  function renderFrame() {
    canvasElement.width = canvasElement.clientWidth || 400;
    canvasElement.height = canvasElement.clientHeight || 240;

    ctx.clearRect(0, 0, canvasElement.width, canvasElement.height);

    // Render realistic vision bounding boxes
    const t = Date.now() / 1500;
    const xPerson = canvasElement.width * 0.25 + Math.sin(t) * 15;
    const xBottle = canvasElement.width * 0.65;

    // 1. Person Box (Left)
    ctx.strokeStyle = '#FFE500';
    ctx.lineWidth = 3;
    ctx.strokeRect(xPerson, 30, 90, 150);
    ctx.fillStyle = 'rgba(255, 229, 0, 0.85)';
    ctx.fillRect(xPerson, 10, 80, 20);
    ctx.fillStyle = '#000000';
    ctx.font = 'bold 12px sans-serif';
    ctx.fillText('Person 2.2m', xPerson + 4, 25);

    // 2. Bottle Box (Right)
    ctx.strokeStyle = '#30D158';
    ctx.lineWidth = 3;
    ctx.strokeRect(xBottle, 100, 50, 75);
    ctx.fillStyle = 'rgba(48, 209, 88, 0.85)';
    ctx.fillRect(xBottle, 80, 70, 20);
    ctx.fillStyle = '#000000';
    ctx.font = 'bold 12px sans-serif';
    ctx.fillText('Bottle 1.2m', xBottle + 4, 95);

    animationFrameId = requestAnimationFrame(renderFrame);
  }

  renderFrame();
}

function describeCurrentScene() {
  earcons.commandRecognized();
  const summary = "A person is standing 2 meters ahead on your left. A water bottle is located 1.2 meters ahead to your right. Center walkway is clear.";
  document.getElementById('scene-summary-text').textContent = summary;
  speak(summary);
}

function startFindingTarget(target) {
  earcons.commandRecognized();
  speak(`Searching for ${target}. Pan camera slowly to the right...`);
  setTimeout(() => {
    earcons.ocrSuccess();
    triggerHaptic([60, 40, 60]);
    const foundMsg = `Found ${target}! Located 1.2 meters ahead slightly to your right.`;
    document.getElementById('scene-summary-text').textContent = foundMsg;
    speak(foundMsg);
  }, 2200);
}

/* ==========================================================================
   7. DOCUMENT OCR READER SUBSYSTEM
   ========================================================================== */
function loadSampleDocument(type) {
  const doc = sampleDocuments[type] || sampleDocuments.receipt;
  state.readerDocType = type;

  document.getElementById('doc-type-badge').textContent = "Document: " + doc.type;
  document.getElementById('ocr-display-text').textContent = doc.text;
  document.getElementById('ocr-highlight-box').textContent = "💰 " + doc.highlight;

  speak(`${doc.type} loaded. ${doc.highlight}. Tap Read Aloud to listen.`);
}

function readCurrentDocument() {
  const doc = sampleDocuments[state.readerDocType] || sampleDocuments.receipt;
  state.readerIsPlaying = true;
  state.readerIsPaused = false;

  earcons.ocrSuccess();
  triggerHaptic([40, 50]);

  const speechContent = `${doc.type} recognized. ${doc.highlight}. Full text reads: ${doc.text.replace(/\n/g, '. ')}`;
  speak(speechContent);
}

function pauseReader() {
  if ('speechSynthesis' in window) {
    window.speechSynthesis.pause();
    state.readerIsPaused = true;
    state.readerIsPlaying = false;
  }
}

function resumeReader() {
  if ('speechSynthesis' in window) {
    window.speechSynthesis.resume();
    state.readerIsPaused = false;
    state.readerIsPlaying = true;
  }
}

function stopReader() {
  if ('speechSynthesis' in window) {
    window.speechSynthesis.cancel();
    state.readerIsPaused = false;
    state.readerIsPlaying = false;
    speak("Reading stopped.");
  }
}

/* ==========================================================================
   8. NEARBY AMENITIES SUBSYSTEM
   ========================================================================== */
function renderNearbyAmenities(category = 'all') {
  const container = document.getElementById('places-container');
  if (!container) return;
  container.innerHTML = '';

  const filtered = (category === 'all')
    ? offlineAmenities
    : offlineAmenities.filter(a => a.category === category);

  document.getElementById('nearby-summary-text').textContent =
    `Found ${filtered.length} offline amenities nearby.`;

  filtered.forEach(place => {
    const card = document.createElement('div');
    card.className = 'place-item-card';
    card.setAttribute('tabindex', '0');
    card.setAttribute('role', 'region');
    card.setAttribute('aria-label', `${place.name}, ${place.distance} meters away at ${place.clock}. Double tap to navigate.`);

    card.innerHTML = `
      <div class="place-name">${place.name}</div>
      <div class="place-meta">${place.address} • ${place.hours}</div>
      <div class="place-metrics">${place.distance}m • ${place.clock}</div>
      <button class="btn-accessible btn-primary btn-navigate-place" data-name="${place.name}" aria-label="Navigate to ${place.name}">
        🚶 Navigate Here
      </button>
    `;

    card.querySelector('.btn-navigate-place').addEventListener('click', (e) => {
      e.stopPropagation();
      navigateToView('navigation');
      startWalkingNavigation(place.name);
    });

    container.appendChild(card);
  });
}

function speakNearbyAmenities(category = 'all') {
  const filtered = (category === 'all')
    ? offlineAmenities
    : offlineAmenities.filter(a => a.category === category);

  if (filtered.length === 0) {
    speak("No places found for this category nearby in offline database.");
    return;
  }

  const nearest = filtered[0];
  speak(`Found ${filtered.length} nearby places. Nearest is ${nearest.name}, ${nearest.distance} meters away at ${nearest.clock}.`);
}

/* ==========================================================================
   9. SAFETY & SOS SUBSYSTEM
   ========================================================================== */
function triggerSosCountdown() {
  state.isSosActive = true;
  state.sosSecondsRemaining = 5;

  const banner = document.getElementById('sos-countdown-banner');
  const timerNum = document.getElementById('sos-timer-number');
  if (banner) banner.classList.remove('hidden');
  if (timerNum) timerNum.textContent = '5';

  earcons.hazardAlert();
  triggerHaptic([100, 50, 100, 50, 100]);
  speak("SOS emergency triggered! Dispatching alert in 5 seconds. Tap Cancel SOS to abort.");

  clearInterval(state.sosCountdownTimer);
  state.sosCountdownTimer = setInterval(() => {
    state.sosSecondsRemaining -= 1;
    if (timerNum) timerNum.textContent = state.sosSecondsRemaining;

    earcons.sosBeat();
    triggerHaptic([150, 80, 150]);

    if (state.sosSecondsRemaining > 0) {
      speak(String(state.sosSecondsRemaining), true);
    } else {
      clearInterval(state.sosCountdownTimer);
      dispatchSosAlert();
    }
  }, 1000);
}

function cancelSosCountdown() {
  if (state.sosCountdownTimer) {
    clearInterval(state.sosCountdownTimer);
    state.sosCountdownTimer = null;
  }
  state.isSosActive = false;

  const banner = document.getElementById('sos-countdown-banner');
  if (banner) banner.classList.add('hidden');

  earcons.commandRecognized();
  triggerHaptic([50, 50]);
  speak("Emergency SOS cancelled. You are safe.");

  state.auditTrail.unshift({
    event: 'SOS_CANCELLED',
    time: new Date().toLocaleTimeString(),
    details: 'User cancelled SOS countdown before dispatch'
  });
}

function dispatchSosAlert() {
  const banner = document.getElementById('sos-countdown-banner');
  if (banner) banner.classList.add('hidden');

  speak("Emergency SOS dispatched! Calling primary contact Jane Doe now.");

  state.auditTrail.unshift({
    event: 'SOS_DISPATCHED',
    time: new Date().toLocaleTimeString(),
    details: 'Emergency alert & GPS coordinates dispatched to Jane Doe'
  });

  setTimeout(() => {
    alert("📞 DIALING PRIMARY EMERGENCY CONTACT:\nJane Doe (+1-555-234-5678)\nSMS Alert: 'SIGHTGUIDE SOS Alert: I need assistance. My current location is https://maps.google.com/?q=37.7749,-122.4194'");
  }, 600);
}

function callPrimaryContact() {
  triggerHaptic([50, 50]);
  speak("Calling Jane Doe.");
  alert("📞 Dialing Jane Doe: +1-555-234-5678");
}

function toggleLocationSharing() {
  state.isLocationSharingActive = !state.isLocationSharingActive;
  const statusBadge = document.getElementById('sharing-status-text');
  const descText = document.getElementById('sharing-desc-text');
  const toggleBtn = document.getElementById('btn-toggle-sharing');

  if (state.isLocationSharingActive) {
    if (statusBadge) statusBadge.textContent = "Sharing ON";
    if (descText) descText.innerHTML = "Status: <strong>ACTIVELY SHARING</strong> with authorized caregiver Jane Doe.";
    if (toggleBtn) {
      toggleBtn.textContent = "🛑 Stop Sharing Location";
      toggleBtn.classList.add('btn-danger');
    }
    speak("Location sharing enabled with authorized caregiver.");
  } else {
    if (statusBadge) statusBadge.textContent = "Private";
    if (descText) descText.innerHTML = "Status: <strong>DISABLED</strong>. SIGHTGUIDE will never track or share your location without consent.";
    if (toggleBtn) {
      toggleBtn.textContent = "📡 Enable Location Sharing";
      toggleBtn.classList.remove('btn-danger');
    }
    speak("Location sharing stopped.");
  }
}

function generateNewPairingCode() {
  const chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
  let c1 = "", c2 = "";
  for (let i = 0; i < 3; i++) c1 += chars.charAt(Math.floor(Math.random() * chars.length));
  for (let i = 0; i < 3; i++) c2 += chars.charAt(Math.floor(Math.random() * chars.length));
  state.pairingCode = `${c1}-${c2}`;

  const disp = document.getElementById('pairing-code-display');
  if (disp) disp.textContent = state.pairingCode;

  const spoken = state.pairingCode.replace("-", " dash ").split('').join(' ');
  speak(`New pairing code: ${spoken}. Share this in person with your caregiver.`);
}

/* ==========================================================================
   10. VIEW NAVIGATION ROUTER
   ========================================================================== */
function navigateToView(viewId) {
  state.activeView = viewId;

  document.querySelectorAll('.view-panel').forEach(panel => {
    panel.classList.remove('active');
  });

  const target = document.getElementById(`view-${viewId}`);
  if (target) {
    target.classList.add('active');
    triggerHaptic([30, 40]);

    if (viewId === 'camera') {
      initCameraFeed();
    }
    if (viewId === 'nearby') {
      renderNearbyAmenities(state.selectedAmenityCategory);
    }
    if (viewId === 'navigation') {
      updateCompassUI();
    }
  }
}

/* ==========================================================================
   11. INITIALIZATION & EVENT LISTENERS
   ========================================================================== */
window.addEventListener('DOMContentLoaded', () => {
  // 1. Theme Toggle
  const themeToggle = document.getElementById('btn-theme-toggle');
  themeToggle?.addEventListener('click', () => {
    state.isHighContrastDark = !state.isHighContrastDark;
    document.body.className = state.isHighContrastDark ? 'theme-dark' : 'theme-light';
    const label = document.getElementById('theme-label');
    if (label) label.textContent = state.isHighContrastDark ? 'Dark Theme' : 'Light Theme';
    speak(state.isHighContrastDark ? "High contrast dark mode" : "High contrast light mode");
  });

  // 2. Master Voice Assistant Trigger
  const voiceTrigger = document.getElementById('btn-voice-trigger');
  voiceTrigger?.addEventListener('click', toggleVoiceListening);

  // Keyboard shortcut: Spacebar opens voice assistant when not focused in input
  window.addEventListener('keydown', (e) => {
    if (e.code === 'Space' && e.target.tagName !== 'INPUT' && e.target.tagName !== 'BUTTON') {
      e.preventDefault();
      toggleVoiceListening();
    }
  });

  // 3. Tile Clicks on Dashboard
  document.querySelectorAll('.action-tile').forEach(tile => {
    tile.addEventListener('click', () => {
      const target = tile.getAttribute('data-target');
      navigateToView(target);
      speak(`Opening ${target}`);
    });
  });

  // 4. Back Buttons
  document.querySelectorAll('.btn-back').forEach(btn => {
    btn.addEventListener('click', () => {
      const backTarget = btn.getAttribute('data-back') || 'dashboard';
      navigateToView(backTarget);
      speak("Back at main dashboard.");
    });
  });

  // 5. Navigation View Controls
  document.getElementById('btn-where-am-i')?.addEventListener('click', () => {
    speak("You are near 450 Market Street, facing " + getCompassDirectionName(state.currentHeadingDegrees) + ".");
  });

  document.getElementById('btn-whats-direction')?.addEventListener('click', () => {
    speak("Facing " + getCompassDirectionName(state.currentHeadingDegrees) + ", " + Math.round(state.currentHeadingDegrees) + " degrees.");
  });

  document.getElementById('btn-repeat-nav')?.addEventListener('click', repeatLastSpeech);

  document.getElementById('btn-open-maps')?.addEventListener('click', () => {
    window.open(`https://maps.google.com/?q=37.7749,-122.4194&mode=w`, '_blank');
  });

  document.getElementById('btn-stop-nav')?.addEventListener('click', () => {
    stopWalkingNavigation();
    speak("Navigation stopped.");
  });

  document.getElementById('btn-turn-left')?.addEventListener('click', () => {
    state.currentHeadingDegrees = (state.currentHeadingDegrees - 30 + 360) % 360;
    updateCompassUI();
    triggerHaptic([60, 40]);
    speak("Turned left. Facing " + getCompassDirectionName(state.currentHeadingDegrees));
  });

  document.getElementById('btn-turn-right')?.addEventListener('click', () => {
    state.currentHeadingDegrees = (state.currentHeadingDegrees + 30) % 360;
    updateCompassUI();
    triggerHaptic([40, 60]);
    speak("Turned right. Facing " + getCompassDirectionName(state.currentHeadingDegrees));
  });

  // 6. Camera View Controls
  document.getElementById('btn-describe-scene')?.addEventListener('click', describeCurrentScene);
  document.getElementById('btn-find-bottle')?.addEventListener('click', () => startFindingTarget('bottle'));
  document.getElementById('btn-toggle-camera-source')?.addEventListener('click', () => {
    if (state.isCameraActive) {
      if (videoElement && videoElement.srcObject) {
        videoElement.srcObject.getTracks().forEach(t => t.stop());
      }
      state.isCameraActive = false;
      document.getElementById('camera-tag').textContent = "Vision AI: Simulation Mode";
      speak("Live camera stream paused. Using simulated vision feed.");
    } else {
      initCameraFeed();
      speak("Live camera activated.");
    }
  });

  // 7. Reader View Controls
  document.getElementById('btn-read-now')?.addEventListener('click', readCurrentDocument);
  document.getElementById('btn-pause-reader')?.addEventListener('click', pauseReader);
  document.getElementById('btn-resume-reader')?.addEventListener('click', resumeReader);
  document.getElementById('btn-repeat-reader')?.addEventListener('click', repeatLastSpeech);
  document.getElementById('btn-stop-reader')?.addEventListener('click', stopReader);

  document.querySelectorAll('.sample-docs-bar .btn-chip').forEach(btn => {
    btn.addEventListener('click', () => {
      const sampleKey = btn.getAttribute('data-sample');
      loadSampleDocument(sampleKey);
    });
  });

  // 8. Nearby Amenities Filters
  document.querySelectorAll('.filter-chip').forEach(chip => {
    chip.addEventListener('click', () => {
      document.querySelectorAll('.filter-chip').forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
      const cat = chip.getAttribute('data-cat');
      state.selectedAmenityCategory = cat;
      renderNearbyAmenities(cat);
      speakNearbyAmenities(cat);
    });
  });

  // 9. Safety View Controls
  document.getElementById('btn-trigger-sos')?.addEventListener('click', triggerSosCountdown);
  document.getElementById('btn-cancel-sos')?.addEventListener('click', cancelSosCountdown);
  document.getElementById('btn-call-primary')?.addEventListener('click', callPrimaryContact);
  document.getElementById('btn-toggle-sharing')?.addEventListener('click', toggleLocationSharing);
  document.getElementById('btn-new-code')?.addEventListener('click', generateNewPairingCode);

  // 10. Settings View Controls
  const rateSlider = document.getElementById('slider-speech-rate');
  rateSlider?.addEventListener('input', (e) => {
    state.speechRate = parseFloat(e.target.value);
    document.getElementById('rate-value-text').textContent = state.speechRate.toFixed(1) + 'x';
  });

  const pitchSlider = document.getElementById('slider-speech-pitch');
  pitchSlider?.addEventListener('input', (e) => {
    state.speechPitch = parseFloat(e.target.value);
    document.getElementById('pitch-value-text').textContent = state.speechPitch.toFixed(1);
  });

  document.getElementById('btn-test-speech')?.addEventListener('click', () => {
    speak("This is SIGHTGUIDE speaking with your chosen speed and pitch.");
  });

  document.getElementById('btn-test-vibration')?.addEventListener('click', () => {
    triggerHaptic([80, 50, 80, 50, 150]);
    speak("Tactile vibration pulse tested.");
  });

  document.getElementById('btn-toggle-dark-mode')?.addEventListener('click', () => {
    state.isHighContrastDark = !state.isHighContrastDark;
    document.body.className = state.isHighContrastDark ? 'theme-dark' : 'theme-light';
    const btn = document.getElementById('btn-toggle-dark-mode');
    if (btn) btn.textContent = state.isHighContrastDark ? 'ON' : 'OFF';
    speak(state.isHighContrastDark ? "Dark high-contrast mode enabled" : "Light high-contrast mode enabled");
  });

  document.getElementById('btn-toggle-chimes')?.addEventListener('click', () => {
    state.isAudioChimesEnabled = !state.isAudioChimesEnabled;
    const btn = document.getElementById('btn-toggle-chimes');
    if (btn) btn.textContent = state.isAudioChimesEnabled ? 'ON' : 'OFF';
    if (state.isAudioChimesEnabled) earcons.commandRecognized();
    speak(state.isAudioChimesEnabled ? "Auditory earcons enabled" : "Auditory earcons silenced");
  });

  // Welcome Announcement
  setTimeout(() => {
    earcons.commandRecognized();
    speak("Welcome to SIGHTGUIDE. Voice assistant and accessible dashboard ready. What can I help with?");
  }, 400);
});
