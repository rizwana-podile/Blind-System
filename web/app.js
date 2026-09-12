/**
 * SIGHTGUIDE — User & Family Dual Assistive Platform
 * Enterprise Accessibility System with Live Object Detection, Turn-by-Turn Routing,
 * Real MediaRecorder Audio Capture, All-Family Emergency Broadcast & Cross-Dashboard Sync.
 */

// Canonical Application State & Telugu Family Data Model
const state = {
  activeDashboard: 'user', // 'user' | 'family' | 'dual'
  activeUserPanel: 'panel-navigate',
  speechRate: 1.0,
  speechPitch: 1.0,
  isHighContrastDark: true,
  isAudioEnabled: true,
  isVoiceListening: false,
  lastSpokenText: 'Welcome Ravi. Voice assistant and accessible dashboard ready.',
  
  // Current Main User Profile
  currentUser: {
    id: 'user-ravi',
    name: 'Ravi',
    role: 'User (Blind / Low-Vision)',
    phone: '+91 98480 11223',
    address: 'Near 450 Market Street, San Francisco, CA'
  },

  // Registered Family Members (Telugu / Indian Family Structure)
  familyMembers: [
    { id: 'fam-lakshmi', name: 'Lakshmi', relationship: 'Mother', phone: '+91 98480 22338', avatar: '👩', status: 'Active' },
    { id: 'fam-suresh', name: 'Suresh', relationship: 'Father', phone: '+91 98480 22339', avatar: '👨', status: 'Active' },
    { id: 'fam-kavya', name: 'Kavya', relationship: 'Sister', phone: '+91 98480 22340', avatar: '👩', status: 'Active' },
    { id: 'fam-prasad', name: 'Prasad', relationship: 'Brother', phone: '+91 98480 22341', avatar: '👨', status: 'Active' }
  ],
  selectedCaregiverId: 'fam-lakshmi',

  // Navigation & Location
  userLocation: {
    lat: 37.7749,
    lng: -122.4194,
    address: 'Near 450 Market Street, San Francisco, CA',
    heading: 358,
    speedMps: 1.2,
    accuracyM: 3
  },
  activeDestination: {
    name: 'City General Hospital',
    distance: '2.4 km',
    walkingTime: '31 min',
    steps: [
      { turn: 'straight', instruction: 'Walk straight for 400 meters on Market Street', dist: '400m', icon: '⬆️' },
      { turn: 'left', instruction: 'Turn left onto Civic Center Boulevard', dist: '150m', icon: '◀️' },
      { turn: 'straight', instruction: 'Continue straight for 800 meters along the tactile paving', dist: '800m', icon: '⬆️' },
      { turn: 'right', instruction: 'Turn right at Health Avenue junction', dist: '200m', icon: '▶️' },
      { turn: 'straight', instruction: 'Continue straight for 1.2 kilometers', dist: '1.2km', icon: '⬆️' },
      { turn: 'destination', instruction: 'Destination is on the left: City General Hospital Emergency Entrance', dist: 'Arrive', icon: '🏥' }
    ]
  },
  isVoiceGuidanceActive: false,

  // Camera & Real-Time Computer Vision State
  isCameraActive: false,
  cameraStream: null,
  cocoModel: null,
  isModelLoading: false,
  autoAnnounceEnabled: false, // OFF by default to eliminate unwanted continuous talking
  presentedSimObject: 'none', // 'none' | 'phone' | 'bottle' | 'cup' | 'book' | 'laptop' | 'chair' | 'person'
  currentDetection: null,
  lastSpokenObject: '',
  lastSpokenObjectTime: 0,
  cameraLoopInterval: null,
  // Real Audio Recording (MediaRecorder)
  mediaRecorder: null,
  audioChunks: [],
  isRecording: false,
  recordingStartTime: null,
  recordingTimerInterval: null,
  currentRecordDurationSec: 0,
  recordings: [],

  // Call Management (Two-Way User <-> Family)
  activeCall: {
    isInCall: false,
    otherParty: null,
    startTime: null,
    timerInterval: null
  },
  incomingCall: {
    isRinging: false,
    caller: null,
    ringtoneInterval: null
  },

  // Safety & Emergency SOS
  isSosActive: false,
  sosCountdownSeconds: 5,
  sosCountdownInterval: null,
  sosAlarmInterval: null,
  lastSosTriggerTime: 0,
  isLocationSharingActive: true,

  // User & Family Notifications
  userNotifications: [
    {
      id: 'un-01',
      type: 'voice',
      icon: '🎙️',
      title: 'Voice Message from Lakshmi (Mother)',
      detail: '"Ravi, remember to take your medical card when you visit the clinic."',
      time: 'Today at 2:30 PM',
      audioText: 'Ravi, remember to take your medical card when you visit the clinic.',
      isRead: false
    },
    {
      id: 'un-02',
      type: 'call',
      icon: '📞',
      title: 'Missed Call from Suresh (Father)',
      detail: 'Suresh tried calling at 1:15 PM.',
      time: 'Today at 1:15 PM',
      isRead: false
    }
  ],
  familyNotifications: [
    {
      id: 'fn-01',
      type: 'info',
      icon: '🛡️',
      title: 'Consent-Based Location Sharing Active',
      detail: 'Encrypted GPS transmission verified for all 4 family members.',
      time: 'Today at 1:00 PM'
    }
  ],
  familyReceivedVoiceNotes: [
    {
      id: 'fvn-01',
      sender: 'Ravi',
      time: 'Today at 11:20 AM',
      duration: '00:14',
      text: 'Hi mother, I am walking to the pharmacy now. The sidewalk is clear.'
    }
  ]
};

// Amenities Database with Step-by-Step Walking Routes
const amenitiesData = [
  {
    id: 1,
    name: "City General Hospital",
    category: "hospital",
    distance: "2.4 km",
    walkingTime: "31 min",
    clock: "2 o'clock",
    address: "500 Civic Blvd",
    hours: "Emergency 24/7",
    icon: "🏥",
    steps: [
      { turn: 'straight', instruction: 'Walk straight for 400 meters on Market Street', dist: '400m', icon: '⬆️' },
      { turn: 'left', instruction: 'Turn left onto Civic Center Boulevard', dist: '150m', icon: '◀️' },
      { turn: 'straight', instruction: 'Continue straight for 800 meters along the tactile paving', dist: '800m', icon: '⬆️' },
      { turn: 'right', instruction: 'Turn right at Health Avenue junction', dist: '200m', icon: '▶️' },
      { turn: 'straight', instruction: 'Continue straight for 1.2 kilometers', dist: '1.2km', icon: '⬆️' },
      { turn: 'destination', instruction: 'Destination is on the left: City General Hospital Emergency Entrance', dist: 'Arrive', icon: '🏥' }
    ]
  },
  {
    id: 2,
    name: "Community Pharmacy",
    category: "pharmacy",
    distance: "120 m",
    walkingTime: "2 min",
    clock: "1 o'clock",
    address: "142 Health Ave",
    hours: "Open 24/7",
    icon: "💊",
    steps: [
      { turn: 'straight', instruction: 'Walk straight for 50 meters on the sidewalk', dist: '50m', icon: '⬆️' },
      { turn: 'right', instruction: 'Bear slight right at 1 o\'clock toward Health Ave', dist: '40m', icon: '↗️' },
      { turn: 'destination', instruction: 'Community Pharmacy entrance is on your right with tactile guidance', dist: '30m', icon: '💊' }
    ]
  },
  {
    id: 3,
    name: "Metro Transit Central",
    category: "transit",
    distance: "180 m",
    walkingTime: "3 min",
    clock: "11 o'clock",
    address: "4th & Market St",
    hours: "5 AM - 1 AM",
    icon: "🚇",
    steps: [
      { turn: 'straight', instruction: 'Walk forward 80 meters to 4th Street intersection', dist: '80m', icon: '⬆️' },
      { turn: 'left', instruction: 'Turn left at the audio signal crosswalk', dist: '40m', icon: '◀️' },
      { turn: 'destination', instruction: 'Metro station entrance with tactile stair cues on left', dist: '60m', icon: '🚇' }
    ]
  },
  {
    id: 4,
    name: "Accessible ATM & Bank",
    category: "bank",
    distance: "90 m",
    walkingTime: "1 min",
    clock: "12 o'clock",
    address: "220 Commerce Way",
    hours: "ATM 24 Hours",
    icon: "🏧",
    steps: [
      { turn: 'straight', instruction: 'Walk straight ahead for 70 meters', dist: '70m', icon: '⬆️' },
      { turn: 'destination', instruction: 'Braille audio-jack ATM is on your right inside vestibule', dist: '20m', icon: '🏧' }
    ]
  },
  {
    id: 5,
    name: "Fresh Harvest Grocery",
    category: "grocery",
    distance: "240 m",
    walkingTime: "4 min",
    clock: "10 o'clock",
    address: "88 Green Street",
    hours: "7 AM - 10 PM",
    icon: "🥦",
    steps: [
      { turn: 'straight', instruction: 'Walk 100 meters forward to corner', dist: '100m', icon: '⬆️' },
      { turn: 'left', instruction: 'Turn left onto Green Street pedestrian walkway', dist: '80m', icon: '◀️' },
      { turn: 'destination', instruction: 'Grocery automatic sliding doors straight ahead', dist: '60m', icon: '🥦' }
    ]
  }
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
   1. AUDITORY SYNTHESIZER & ACOUSTIC EARCONS (Web Audio API)
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
  phoneRing() {
    playTone(853, 'sine', 350, 0.2);
    playTone(960, 'sine', 350, 0.2);
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

  const announcer = document.getElementById('live-announcer');
  if (announcer) announcer.textContent = text;

  const transcript = document.getElementById('voice-transcript-content');
  if (transcript) transcript.textContent = `"${text}"`;

  if (!('speechSynthesis' in window) || !state.isAudioEnabled) {
    if (onEndCallback) onEndCallback();
    return;
  }

  try {
    window.speechSynthesis.cancel();
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.rate = state.speechRate;
    utterance.pitch = state.speechPitch;

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
   3. VOICE RECOGNITION & NATURAL LANGUAGE COMMAND PROCESSOR
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
      const transcript = event.results[0][0].transcript.trim();
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
    "Call Lakshmi",
    "Call my mother",
    "Call Suresh",
    "Where am I?",
    "Navigate to hospital",
    "Describe scene",
    "Start recording",
    "Emergency SOS"
  ];
  const chosen = prompt("Voice Assistant Command Simulation:\nSpeak or type a command:\n- Call Lakshmi (or 'Call my mother')\n- Call Suresh (or 'Call father')\n- Call Kavya / Call Prasad\n- Where am I?\n- Navigate to hospital\n- Describe scene\n- Start recording\n- Emergency SOS", sampleCommands[0]);
  
  setTimeout(() => {
    state.isVoiceListening = false;
    updateVoiceHeroUI();
    if (chosen && chosen.trim()) {
      handleVoiceCommand(chosen.trim());
    }
  }, 350);
}

function handleVoiceCommand(rawCmd) {
  earcons.commandRecognized();
  const cmd = rawCmd.toLowerCase();
  const label = document.getElementById('voice-transcript-content');
  if (label) label.textContent = `"${rawCmd}"`;

  // 1. Calling Family Members via Voice: "Call Lakshmi", "Call my mother", "Call Suresh"
  if (cmd.startsWith("call ") || cmd.includes("call my ") || cmd.includes("want to call ") || cmd.includes("please call ")) {
    const targetMember = resolveFamilyMemberFromVoice(cmd);
    if (targetMember) {
      initiateOutgoingCall(targetMember);
      return;
    } else {
      // Ambiguous or not specified
      promptDisambiguateCall();
      return;
    }
  }

  // 2. Navigation & Geolocation
  if (cmd.includes('where am i') || cmd.includes('location')) {
    speak(`You are near 450 Market Street, facing ${getHeadingName(state.userLocation.heading)}.`);
  } else if (cmd.includes('navigate to hospital') || cmd.includes('route to hospital') || cmd.includes('hospital')) {
    switchUserSubpanel('panel-nearby');
    selectAmenityRoute(amenitiesData[0]); // City General Hospital
  } else if (cmd.includes('navigate to pharmacy') || cmd.includes('pharmacy')) {
    switchUserSubpanel('panel-nearby');
    selectAmenityRoute(amenitiesData[1]); // Community Pharmacy
  } else if (cmd.includes('navigate') || cmd.includes('route')) {
    switchUserSubpanel('panel-nearby');
    speak("Opening Nearby amenities. Select a place to view walking directions.");
  } else if (
    cmd.includes("what is this") ||
    cmd.includes("what am i holding") ||
    cmd.includes("identify") ||
    cmd.includes("what is in front") ||
    cmd.includes("what's in front") ||
    cmd.includes("what is happening") ||
    cmd.includes("what's happening") ||
    cmd.includes("describe scene") ||
    cmd.includes("describe") ||
    cmd.includes("camera") ||
    cmd.includes("see")
  ) {
    switchUserSubpanel('panel-camera');
    identifyObjectShown(true);
  } else if (cmd.includes('read') || cmd.includes('ocr') || cmd.includes('document')) {
    switchUserSubpanel('panel-reader');
    readCurrentDocument();
  } else if (cmd.includes('start recording') || cmd.includes('record voice') || cmd.includes('record')) {
    switchUserSubpanel('panel-memos');
    startAudioRecording();
  } else if (cmd.includes('stop recording')) {
    stopAudioRecording();
  } else if (cmd.includes('send to family') || cmd.includes('send this to family')) {
    if (state.recordings.length > 0) {
      sendRecordingToFamily(state.recordings[0].id);
    } else {
      speak("You have no saved recordings to send. Record an audio memo first.");
    }
  } else if (cmd.includes('sos') || cmd.includes('help') || cmd.includes('emergency')) {
    switchUserSubpanel('panel-safety');
    startSosCountdown();
  } else if (cmd.includes('stop') || cmd.includes('cancel')) {
    window.speechSynthesis.cancel();
    if (state.isSosActive) cancelSosCountdown();
    if (state.activeCall.isInCall) endCurrentCall();
    speak("Action stopped.");
  } else {
    speak(`I heard: "${rawCmd}". Say call Lakshmi, call Suresh, navigate to hospital, describe scene, or emergency SOS.`);
  }
}

function resolveFamilyMemberFromVoice(cmd) {
  // Check names directly
  if (cmd.includes('lakshmi')) return state.familyMembers.find(m => m.id === 'fam-lakshmi');
  if (cmd.includes('suresh')) return state.familyMembers.find(m => m.id === 'fam-suresh');
  if (cmd.includes('kavya')) return state.familyMembers.find(m => m.id === 'fam-kavya');
  if (cmd.includes('prasad')) return state.familyMembers.find(m => m.id === 'fam-prasad');

  // Check relationship mappings
  if (cmd.includes('mother') || cmd.includes('mom') || cmd.includes('amma')) return state.familyMembers.find(m => m.relationship === 'Mother');
  if (cmd.includes('father') || cmd.includes('dad') || cmd.includes('nanna')) return state.familyMembers.find(m => m.relationship === 'Father');
  if (cmd.includes('sister') || cmd.includes('akka') || cmd.includes('chelli')) return state.familyMembers.find(m => m.relationship === 'Sister');
  if (cmd.includes('brother') || cmd.includes('anna') || cmd.includes('tammudu')) return state.familyMembers.find(m => m.relationship === 'Brother');

  return null;
}

function promptDisambiguateCall() {
  speak("Which family member do you want to call? Press 1 for Lakshmi, 2 for Suresh, 3 for Kavya, or 4 for Prasad.");
  const choice = prompt("Which person do you want to call?\n1. Lakshmi (Mother)\n2. Suresh (Father)\n3. Kavya (Sister)\n4. Prasad (Brother)", "1");
  if (choice === "1") initiateOutgoingCall(state.familyMembers[0]);
  else if (choice === "2") initiateOutgoingCall(state.familyMembers[1]);
  else if (choice === "3") initiateOutgoingCall(state.familyMembers[2]);
  else if (choice === "4") initiateOutgoingCall(state.familyMembers[3]);
}

function updateVoiceHeroUI() {
  const btn = document.getElementById('btn-user-voice');
  const mainLabel = document.getElementById('voice-button-label');
  const subLabel = document.getElementById('voice-button-sub');

  if (state.isVoiceListening) {
    btn?.classList.add('listening');
    if (mainLabel) mainLabel.textContent = "Listening... Speak Now";
    if (subLabel) subLabel.textContent = "Say 'Call Lakshmi', 'Navigate to hospital', 'Emergency'";
  } else {
    btn?.classList.remove('listening');
    if (mainLabel) mainLabel.textContent = "Tap to Speak to SIGHTGUIDE";
    if (subLabel) subLabel.textContent = '"Call Lakshmi", "Where am I?", "Navigate to hospital", "Emergency"';
  }
}

/* ==========================================================================
   4. TWO-WAY CALLING ENGINE (User <-> Family Synchronization)
   ========================================================================== */

// Outgoing Call: Ravi calls a Family Member
function initiateOutgoingCall(member) {
  if (state.activeCall.isInCall) return;
  earcons.commandRecognized();
  speak(`Calling ${member.name}, ${member.relationship}...`);

  // Open Active Call Modal on User Dashboard
  const modal = document.getElementById('user-active-call-modal');
  const personEl = document.getElementById('active-call-person');
  const timerEl = document.getElementById('active-call-timer');

  if (modal) modal.classList.remove('hidden');
  if (personEl) personEl.textContent = `${member.name} (${member.relationship})`;

  state.activeCall.isInCall = true;
  state.activeCall.otherParty = member;
  state.activeCall.startTime = Date.now();

  let seconds = 0;
  state.activeCall.timerInterval = setInterval(() => {
    seconds++;
    const mins = Math.floor(seconds / 60).toString().padStart(2, '0');
    const secs = (seconds % 60).toString().padStart(2, '0');
    if (timerEl) timerEl.textContent = `${mins}:${secs}`;
  }, 1000);

  // Log in Family Dashboard Notification stream
  addFamilyNotification(
    'call',
    '📞',
    `Call Activity: Ravi called ${member.name}`,
    `Outgoing call connected between Ravi and ${member.name} (${member.relationship}) via secure audio link.`
  );
}

// Incoming Call: Family Member calls Ravi
function initiateIncomingFamilyCall(member) {
  if (state.activeCall.isInCall || state.incomingCall.isRinging) return;
  state.incomingCall.isRinging = true;
  state.incomingCall.caller = member;

  const modal = document.getElementById('user-incoming-call-modal');
  const avatarEl = document.getElementById('call-caller-avatar');
  const nameEl = document.getElementById('call-caller-name');
  const phoneEl = document.getElementById('call-caller-phone');

  if (modal) modal.classList.remove('hidden');
  if (avatarEl) avatarEl.textContent = member.avatar || '👩';
  if (nameEl) nameEl.textContent = `${member.name} (${member.relationship})`;
  if (phoneEl) phoneEl.textContent = member.phone;

  // Ring audio and speak
  earcons.phoneRing();
  state.incomingCall.ringtoneInterval = setInterval(() => {
    earcons.phoneRing();
  }, 2200);

  speak(`Incoming call from your ${member.relationship}, ${member.name}. Press Answer or Decline.`);
}

function answerIncomingCall() {
  clearInterval(state.incomingCall.ringtoneInterval);
  const incModal = document.getElementById('user-incoming-call-modal');
  if (incModal) incModal.classList.add('hidden');

  const member = state.incomingCall.caller;
  state.incomingCall.isRinging = false;

  // Open Active Call
  const activeModal = document.getElementById('user-active-call-modal');
  const personEl = document.getElementById('active-call-person');
  const timerEl = document.getElementById('active-call-timer');

  if (activeModal) activeModal.classList.remove('hidden');
  if (personEl) personEl.textContent = `${member.name} (${member.relationship})`;

  state.activeCall.isInCall = true;
  state.activeCall.otherParty = member;
  state.activeCall.startTime = Date.now();

  speak(`Call connected with ${member.name}.`);

  let seconds = 0;
  state.activeCall.timerInterval = setInterval(() => {
    seconds++;
    const mins = Math.floor(seconds / 60).toString().padStart(2, '0');
    const secs = (seconds % 60).toString().padStart(2, '0');
    if (timerEl) timerEl.textContent = `${mins}:${secs}`;
  }, 1000);

  addFamilyNotification(
    'call',
    '📞',
    `Call Answered: Ravi and ${member.name}`,
    `Ravi answered incoming call from ${member.name} (${member.relationship}). Call active.`
  );
}

function declineIncomingCall() {
  clearInterval(state.incomingCall.ringtoneInterval);
  const incModal = document.getElementById('user-incoming-call-modal');
  if (incModal) incModal.classList.add('hidden');

  const member = state.incomingCall.caller;
  state.incomingCall.isRinging = false;

  speak(`Declined call from ${member.name}.`);

  // Add missed call notification to User Notifications drawer
  addUserNotification(
    'call',
    '📞',
    `Missed Call from ${member.name} (${member.relationship})`,
    `Call declined at ${new Date().toLocaleTimeString()}.`
  );

  // Log in Family Stream
  addFamilyNotification(
    'warning',
    '📞',
    `Call Declined / Missed`,
    `Ravi was unable to answer call from ${member.name} (${member.relationship}).`
  );
}

function endCurrentCall() {
  if (!state.activeCall.isInCall) return;
  clearInterval(state.activeCall.timerInterval);

  const modal = document.getElementById('user-active-call-modal');
  if (modal) modal.classList.add('hidden');

  const member = state.activeCall.otherParty;
  const durationSec = Math.round((Date.now() - state.activeCall.startTime) / 1000);
  state.activeCall.isInCall = false;

  speak(`Call ended. Duration: ${durationSec} seconds.`);

  addFamilyNotification(
    'info',
    '📞',
    `Call Completed (${durationSec}s)`,
    `Call between Ravi and ${member.name} ended cleanly.`
  );
}

/* ==========================================================================
   5. LIVE CAMERA OBJECT IDENTIFICATION & SCENE UNDERSTANDING (Computer Vision)
   ========================================================================== */

// Humanized dictionary mapping COCO classes to crisp names, icons and distances
const objectClassMeta = {
  'cell phone': { name: 'Mobile Phone', icon: '📱', category: 'device' },
  'bottle': { name: 'Water Bottle', icon: '🍶', category: 'container' },
  'cup': { name: 'Coffee Cup', icon: '☕', category: 'container' },
  'laptop': { name: 'Laptop Computer', icon: '💻', category: 'device' },
  'mouse': { name: 'Computer Mouse', icon: '🖱️', category: 'device' },
  'keyboard': { name: 'Keyboard', icon: '⌨️', category: 'device' },
  'book': { name: 'Book', icon: '📖', category: 'reading' },
  'scissors': { name: 'Pair of Scissors', icon: '✂️', category: 'tool' },
  'person': { name: 'Person', icon: '🚶', category: 'person' },
  'chair': { name: 'Chair', icon: '🪑', category: 'furniture' },
  'couch': { name: 'Sofa / Couch', icon: '🛋️', category: 'furniture' },
  'dining table': { name: 'Table Surface', icon: '🪵', category: 'furniture' },
  'bed': { name: 'Bed', icon: '🛏️', category: 'furniture' },
  'backpack': { name: 'Backpack', icon: '🎒', category: 'bag' },
  'handbag': { name: 'Handbag', icon: '👜', category: 'bag' },
  'suitcase': { name: 'Suitcase', icon: '🧳', category: 'bag' },
  'tv': { name: 'Television / Monitor', icon: '📺', category: 'device' },
  'remote': { name: 'Remote Control', icon: '📡', category: 'device' },
  'clock': { name: 'Clock', icon: '⏰', category: 'object' },
  'potted plant': { name: 'Potted Plant', icon: '🪴', category: 'plant' },
  'apple': { name: 'Apple', icon: '🍎', category: 'food' },
  'banana': { name: 'Banana', icon: '🍌', category: 'food' },
  'orange': { name: 'Orange', icon: '🍊', category: 'food' }
};

// Simulation presets when no webcam or when user chooses to present an item
const simPresets = {
  'none': {
    detected: false,
    name: 'No Object Detected',
    icon: '📷',
    confidence: 0,
    bbox: null,
    whatIsHappening: 'No object is currently being shown to the camera. The camera view is open and clear in normal indoor lighting. Hold an item in front of the lens to identify it.'
  },
  'phone': {
    detected: true,
    name: 'Mobile Phone',
    icon: '📱',
    confidence: 0.96,
    bbox: [0.30, 0.20, 0.40, 0.60],
    distance: '30 centimeters away',
    position: 'center of the frame',
    lighting: 'clear indoor lighting',
    whatIsHappening: 'Mobile Phone detected clearly. A smartphone is being held directly in front of the camera in the center of the frame, approximately 30 centimeters away in clear indoor lighting. The device is held steady.'
  },
  'bottle': {
    detected: true,
    name: 'Water Bottle',
    icon: '🍶',
    confidence: 0.94,
    bbox: [0.35, 0.15, 0.30, 0.70],
    distance: '45 centimeters away',
    position: 'center of the frame',
    lighting: 'good room lighting',
    whatIsHappening: 'Water Bottle detected clearly. A cylindrical water bottle is standing in front of the camera, approximately 45 centimeters away on a surface. Path around it is open.'
  },
  'cup': {
    detected: true,
    name: 'Coffee Cup',
    icon: '☕',
    confidence: 0.92,
    bbox: [0.32, 0.30, 0.36, 0.48],
    distance: '40 centimeters away',
    position: 'center of view',
    lighting: 'clear lighting',
    whatIsHappening: 'Coffee Cup detected clearly. A ceramic drinking cup is resting on the desk surface in front of you, about 40 centimeters away.'
  },
  'book': {
    detected: true,
    name: 'Book',
    icon: '📖',
    confidence: 0.95,
    bbox: [0.22, 0.20, 0.56, 0.60],
    distance: '35 centimeters away',
    position: 'center of the frame',
    lighting: 'well-lit condition',
    whatIsHappening: 'Book detected clearly. A printed book or document is held up in front of the camera lens, approximately 35 centimeters away in clear lighting.'
  },
  'laptop': {
    detected: true,
    name: 'Laptop Computer',
    icon: '💻',
    confidence: 0.93,
    bbox: [0.15, 0.25, 0.70, 0.55],
    distance: '60 centimeters away',
    position: 'directly in front of you',
    lighting: 'normal indoor lighting',
    whatIsHappening: 'Laptop Computer detected clearly. An open laptop is resting on the desk directly ahead of you, about 60 centimeters away with keyboard and screen visible.'
  },
  'chair': {
    detected: true,
    name: 'Chair',
    icon: '🪑',
    confidence: 0.91,
    bbox: [0.25, 0.15, 0.50, 0.75],
    distance: '1.5 meters away',
    position: 'straight ahead',
    lighting: 'clear lighting',
    whatIsHappening: 'Chair detected clearly. An office chair is positioned directly in front of you, approximately 1.5 meters ahead with an unobstructed walking path.'
  },
  'person': {
    detected: true,
    name: 'Person',
    icon: '🚶',
    confidence: 0.97,
    bbox: [0.20, 0.08, 0.60, 0.88],
    distance: '1.5 meters away',
    position: 'in front of camera',
    lighting: 'good lighting',
    whatIsHappening: 'Person detected clearly. A person is standing directly in front of the camera, approximately 1.5 meters away facing towards you.'
  }
};

function initCamera() {
  const video = document.getElementById('camera-video');
  const badge = document.getElementById('camera-badge');

  // Attempt real camera stream silently without unsolicited speech
  if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
    navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment', width: { ideal: 640 }, height: { ideal: 480 } } })
      .then(stream => {
        state.cameraStream = stream;
        state.isCameraActive = true;
        if (video) {
          video.srcObject = stream;
          video.play().catch(() => {});
        }
        if (badge) badge.textContent = "Vision AI: Live Camera Active";
        loadVisionNeuralModel();
        startVisionRenderLoop();
      })
      .catch(err => {
        console.log("Webcam unavailable or permission denied, using adaptive vision engine.");
        state.isCameraActive = false;
        if (badge) badge.textContent = "Vision AI: Ready (Adaptive)";
        loadVisionNeuralModel();
        startVisionRenderLoop();
      });
  } else {
    state.isCameraActive = false;
    if (badge) badge.textContent = "Vision AI: Ready (Adaptive)";
    loadVisionNeuralModel();
    startVisionRenderLoop();
  }
}

async function loadVisionNeuralModel() {
  const badge = document.getElementById('camera-badge');
  if (window.cocoSsd) {
    try {
      state.isModelLoading = true;
      if (badge) badge.textContent = "Vision AI: Loading Neural Net...";
      state.cocoModel = await window.cocoSsd.load({ base: 'lite_mobilenet_v2' });
      state.isModelLoading = false;
      if (badge) badge.textContent = state.isCameraActive ? "Vision AI: Neural Vision Active" : "Vision AI: Model Ready";
      console.log("SIGHTGUIDE: COCO-SSD Neural Vision model ready.");
    } catch (err) {
      console.warn("Could not initialize COCO-SSD model, using adaptive heuristic engine:", err);
      state.isModelLoading = false;
      if (badge) badge.textContent = "Vision AI: Adaptive Vision";
    }
  }
}

function startVisionRenderLoop() {
  if (state.cameraLoopInterval) clearInterval(state.cameraLoopInterval);

  // Silent visual update loop (500ms): only renders overlays, NEVER speaks automatically
  state.cameraLoopInterval = setInterval(async () => {
    // Only update visual frame if user is viewing the camera panel
    if (state.activeUserPanel === 'panel-camera') {
      const detection = await analyzeCurrentCameraFrame();
      drawDetectionToCanvas(detection);

      // Only announce if user explicitly enabled auto-announce AND a new object entered and stayed steady
      if (state.autoAnnounceEnabled && detection.detected) {
        const now = Date.now();
        if (detection.name !== state.lastSpokenObject && (now - state.lastSpokenObjectTime > 6000)) {
          state.lastSpokenObject = detection.name;
          state.lastSpokenObjectTime = now;
          speak(`${detection.name} detected clearly. ${detection.whatIsHappening}`);
        }
      }
    }
  }, 600);

  // Initial silent frame draw
  setTimeout(async () => {
    const initial = await analyzeCurrentCameraFrame();
    drawDetectionToCanvas(initial);
  }, 400);
}

async function analyzeCurrentCameraFrame() {
  const video = document.getElementById('camera-video');
  const canvas = document.getElementById('camera-canvas');

  // If user selected a test object preset in simulation mode or to verify
  if (state.presentedSimObject && state.presentedSimObject !== 'none') {
    const preset = simPresets[state.presentedSimObject];
    state.currentDetection = preset;
    return preset;
  }

  // If real camera is streaming and neural model is loaded, run real-world detection
  if (state.isCameraActive && video && video.readyState >= 2 && state.cocoModel) {
    try {
      const predictions = await state.cocoModel.detect(video, 4, 0.40);
      if (predictions && predictions.length > 0) {
        // Sort by bounding box area to get the most prominent object shown
        predictions.sort((a, b) => (b.bbox[2] * b.bbox[3]) - (a.bbox[2] * a.bbox[3]));
        const best = predictions[0];

        const meta = objectClassMeta[best.class.toLowerCase()] || {
          name: best.class.charAt(0).toUpperCase() + best.class.slice(1),
          icon: '📦',
          category: 'object'
        };

        const vidW = video.videoWidth || 640;
        const vidH = video.videoHeight || 480;

        // Position analysis
        const centerX = best.bbox[0] + best.bbox[2] / 2;
        let posText = 'in the center of the frame';
        if (centerX < vidW * 0.35) posText = 'on the left side of view';
        else if (centerX > vidW * 0.65) posText = 'on the right side of view';

        // Distance estimation based on bounding box area ratio
        const areaRatio = (best.bbox[2] * best.bbox[3]) / (vidW * vidH);
        let distText = 'held directly in front of the camera, approximately 30 centimeters away';
        if (areaRatio > 0.32) {
          distText = 'held very close to the camera lens, about 20 to 25 centimeters away';
        } else if (areaRatio < 0.12) {
          distText = 'positioned in the room, about 1.5 to 2 meters away';
        }

        // Contextual description synthesis
        let whatHappening = '';
        const personInScene = predictions.some(p => p.class === 'person' && p !== best);
        if (personInScene && meta.category !== 'person') {
          whatHappening = `A person is holding a ${meta.name.toLowerCase()} ${posText}, ${distText}. Object is in clear view.`;
        } else {
          whatHappening = `${meta.name} detected clearly. A ${meta.name.toLowerCase()} is positioned ${posText}, ${distText}. The object is held steady in view.`;
        }

        const result = {
          detected: true,
          name: meta.name,
          icon: meta.icon,
          confidence: best.score,
          bbox: [
            best.bbox[0] / vidW,
            best.bbox[1] / vidH,
            best.bbox[2] / vidW,
            best.bbox[3] / vidH
          ],
          whatIsHappening: whatHappening
        };
        state.currentDetection = result;
        return result;
      }
    } catch (e) {
      console.warn("Detection error on video frame:", e);
    }
  }

  // If real camera is streaming but nothing detected or model still loading
  if (state.isCameraActive && video && video.readyState >= 2 && canvas) {
    const ctx = canvas.getContext('2d');
    if (ctx) {
      // Analyze center luminance to verify if view is obscured or dark
      try {
        const frameData = ctx.getImageData(canvas.width * 0.3, canvas.height * 0.3, canvas.width * 0.4, canvas.height * 0.4);
        let totalLum = 0;
        for (let i = 0; i < frameData.data.length; i += 16) {
          totalLum += (frameData.data[i] * 0.299 + frameData.data[i + 1] * 0.587 + frameData.data[i + 2] * 0.114);
        }
        const avgLum = totalLum / (frameData.data.length / 16);
        if (avgLum < 25) {
          const darkRes = {
            detected: false,
            name: 'Low Light / Covered',
            icon: '🌑',
            confidence: 0,
            bbox: null,
            whatIsHappening: 'The camera lens is covered or the environment is very dark. Please point the camera towards a well-lit area.'
          };
          state.currentDetection = darkRes;
          return darkRes;
        }
      } catch (err) {}
    }
  }

  // Default: Empty view
  const emptyRes = simPresets['none'];
  state.currentDetection = emptyRes;
  return emptyRes;
}

function drawDetectionToCanvas(detection) {
  const canvas = document.getElementById('camera-canvas');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');

  canvas.width = canvas.parentElement.clientWidth || 400;
  canvas.height = canvas.parentElement.clientHeight || 240;

  ctx.clearRect(0, 0, canvas.width, canvas.height);

  // If camera is simulated or standby, render background scene
  if (!state.isCameraActive) {
    const grad = ctx.createLinearGradient(0, 0, 0, canvas.height);
    grad.addColorStop(0, '#101722');
    grad.addColorStop(1, '#060a10');
    ctx.fillStyle = grad;
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    // Subtle guide grid lines
    ctx.strokeStyle = 'rgba(255, 229, 0, 0.12)';
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(canvas.width * 0.5, 0);
    ctx.lineTo(canvas.width * 0.5, canvas.height);
    ctx.moveTo(0, canvas.height * 0.5);
    ctx.lineTo(canvas.width, canvas.height * 0.5);
    ctx.stroke();

    // Center focal target reticle
    ctx.strokeStyle = detection.detected ? 'rgba(255, 229, 0, 0.5)' : 'rgba(255, 255, 255, 0.2)';
    ctx.lineWidth = 2;
    ctx.setLineDash([6, 6]);
    ctx.strokeRect(canvas.width * 0.25, canvas.height * 0.2, canvas.width * 0.5, canvas.height * 0.6);
    ctx.setLineDash([]);
  }

  // If an object is detected, draw crisp bounding box and high-contrast tag
  if (detection.detected && detection.bbox) {
    const boxX = Math.round(detection.bbox[0] * canvas.width);
    const boxY = Math.round(detection.bbox[1] * canvas.height);
    const boxW = Math.round(detection.bbox[2] * canvas.width);
    const boxH = Math.round(detection.bbox[3] * canvas.height);

    // Bounding Box
    ctx.strokeStyle = '#FFE500';
    ctx.lineWidth = 3;
    ctx.strokeRect(boxX, boxY, boxW, boxH);

    // Corner brackets for high visibility
    const cornerSize = 14;
    ctx.lineWidth = 4;
    ctx.strokeStyle = '#FFFFFF';
    // Top-left
    ctx.beginPath();
    ctx.moveTo(boxX, boxY + cornerSize);
    ctx.lineTo(boxX, boxY);
    ctx.lineTo(boxX + cornerSize, boxY);
    ctx.stroke();
    // Top-right
    ctx.beginPath();
    ctx.moveTo(boxX + boxW - cornerSize, boxY);
    ctx.lineTo(boxX + boxW);
    ctx.lineTo(boxX + boxW, boxY + cornerSize);
    ctx.stroke();

    // Label tag
    const confPct = Math.round(detection.confidence * 100);
    const tagText = `${detection.icon} ${detection.name} ${confPct}%`;
    ctx.font = 'bold 13px sans-serif';
    const textWidth = ctx.measureText(tagText).width;

    const tagX = Math.max(4, boxX);
    const tagY = Math.max(26, boxY);

    ctx.fillStyle = '#FFE500';
    ctx.fillRect(tagX, tagY - 22, textWidth + 14, 24);

    ctx.fillStyle = '#000000';
    ctx.fillText(tagText, tagX + 7, tagY - 6);
  }
}

// MAIN USER ACTION: Identify what is shown to camera and speak clearly
async function identifyObjectShown(speakAloud = true) {
  const hudStatus = document.getElementById('hud-status');
  const hudName = document.getElementById('hud-object-name');
  const hudIcon = document.getElementById('hud-object-icon');
  const hudConf = document.getElementById('hud-confidence');
  const sceneText = document.getElementById('scene-description-text');

  if (hudStatus) hudStatus.textContent = "Analyzing Camera View...";

  // Play earcon ping
  earcons.commandRecognized();

  const detection = await analyzeCurrentCameraFrame();
  drawDetectionToCanvas(detection);

  if (detection.detected) {
    if (hudStatus) hudStatus.textContent = "Object Identified Clearly";
    if (hudName) hudName.textContent = detection.name;
    if (hudIcon) hudIcon.textContent = detection.icon;
    if (hudConf) hudConf.textContent = `Confidence: ${Math.round(detection.confidence * 100)}%`;
    if (sceneText) sceneText.textContent = detection.whatIsHappening;

    if (speakAloud) {
      speak(`${detection.name} detected clearly. ${detection.whatIsHappening}`);
    }
  } else {
    if (hudStatus) hudStatus.textContent = "Ready to Scan";
    if (hudName) hudName.textContent = "No Object Detected";
    if (hudIcon) hudIcon.textContent = "📷";
    if (hudConf) hudConf.textContent = "Camera View Clear";
    if (sceneText) sceneText.textContent = detection.whatIsHappening;

    if (speakAloud) {
      speak(detection.whatIsHappening);
    }
  }
}

// What's Happening in Camera View
async function describeCurrentScene(speakAloud = true) {
  await identifyObjectShown(speakAloud);
}

// Quick finder for Water Bottle
function findBottleTarget() {
  selectPresentedSimObject('bottle');
  identifyObjectShown(true);
}

// Check stairs and elevation changes
function checkStairs() {
  speak("Scanning terrain for steps and elevation changes. Sidewalk is level. Next curb transition in 45 meters.");
}

// Presentation Picker: Select test object to show camera
function selectPresentedSimObject(objKey) {
  state.presentedSimObject = objKey;

  // Highlight active pill in UI
  document.querySelectorAll('.btn-pres-pill').forEach(btn => {
    const match = btn.getAttribute('data-obj') === objKey;
    btn.classList.toggle('active', match);
  });

  const detection = simPresets[objKey] || simPresets['none'];
  drawDetectionToCanvas(detection);

  // Update HUD text silently without unsolicited voice spam
  const hudStatus = document.getElementById('hud-status');
  const hudName = document.getElementById('hud-object-name');
  const hudIcon = document.getElementById('hud-object-icon');
  const hudConf = document.getElementById('hud-confidence');
  const sceneText = document.getElementById('scene-description-text');

  if (detection.detected) {
    if (hudStatus) hudStatus.textContent = "Object Shown to Camera";
    if (hudName) hudName.textContent = detection.name;
    if (hudIcon) hudIcon.textContent = detection.icon;
    if (hudConf) hudConf.textContent = `Confidence: ${Math.round(detection.confidence * 100)}%`;
    if (sceneText) sceneText.textContent = detection.whatIsHappening;

    if (state.autoAnnounceEnabled) {
      speak(`${detection.name} detected clearly. ${detection.whatIsHappening}`);
    }
  } else {
    if (hudStatus) hudStatus.textContent = "Camera Ready";
    if (hudName) hudName.textContent = "No Object Detected";
    if (hudIcon) hudIcon.textContent = "📷";
    if (hudConf) hudConf.textContent = "View Clear";
    if (sceneText) sceneText.textContent = detection.whatIsHappening;
  }
}

// Toggle Auto-Announce mode (OFF by default)
function toggleAutoAnnounce() {
  state.autoAnnounceEnabled = !state.autoAnnounceEnabled;
  const label = document.getElementById('auto-announce-label');
  const btn = document.getElementById('btn-toggle-auto-announce');

  if (state.autoAnnounceEnabled) {
    if (label) label.textContent = "Auto-Announce: ON";
    if (btn) btn.classList.add('btn-action-primary');
    speak("Auto-announce enabled. When you show a new object, SIGHTGUIDE will announce it once.");
  } else {
    if (label) label.textContent = "Auto-Announce: OFF";
    if (btn) btn.classList.remove('btn-action-primary');
    speak("Auto-announce disabled. Camera will only speak when you tap Identify Object.");
  }
}

// Toggle Camera On / Off
function toggleCameraStream() {
  const video = document.getElementById('camera-video');
  const badge = document.getElementById('camera-badge');

  if (state.cameraStream) {
    state.cameraStream.getTracks().forEach(t => t.stop());
    state.cameraStream = null;
    state.isCameraActive = false;
    if (video) video.srcObject = null;
    if (badge) badge.textContent = "Vision AI: Simulation Mode";
    speak("Camera feed switched to simulated vision.");
  } else {
    initCamera();
    speak("Camera activated.");
  }
}

/* ==========================================================================
   6. NEARBY AMENITIES & TURN-BY-TURN ROUTE GUIDANCE
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
        <div class="amenity-meta">${item.distance} • ${item.walkingTime} • At ${item.clock}</div>
        <div class="amenity-address">${item.address} • ${item.hours}</div>
      </div>
      <button class="btn-acc-pill btn-primary-pill btn-view-route" data-id="${item.id}" aria-label="View walking route to ${item.name}">
        🧭 Route
      </button>
    `;
    list.appendChild(card);
  });

  document.querySelectorAll('.btn-view-route').forEach(btn => {
    btn.addEventListener('click', () => {
      const id = parseInt(btn.getAttribute('data-id'));
      const target = amenitiesData.find(a => a.id === id);
      if (target) selectAmenityRoute(target);
    });
  });
}

function selectAmenityRoute(amenity) {
  state.activeDestination = amenity;

  // Reveal Route Card
  const routeCard = document.getElementById('nearby-route-card');
  const destName = document.getElementById('route-dest-name');
  const distText = document.getElementById('route-distance-text');
  const timeText = document.getElementById('route-time-text');
  const stepsCount = document.getElementById('route-steps-count');
  const stepsList = document.getElementById('route-steps-list');

  if (routeCard) routeCard.classList.remove('hidden');
  if (destName) destName.textContent = `${amenity.icon} ${amenity.name}`;
  if (distText) distText.textContent = amenity.distance;
  if (timeText) timeText.textContent = amenity.walkingTime;
  if (stepsCount) stepsCount.textContent = `${amenity.steps.length} steps`;

  if (stepsList) {
    stepsList.innerHTML = '';
    amenity.steps.forEach((step, idx) => {
      const row = document.createElement('div');
      row.className = `route-step-item ${idx === 0 ? 'active-step' : ''}`;
      row.innerHTML = `
        <div class="step-turn-icon">${step.icon}</div>
        <div class="step-info">
          <div class="step-instruction">${idx + 1}. ${step.instruction}</div>
          <div class="step-dist">${step.dist}</div>
        </div>
      `;
      stepsList.appendChild(row);
    });
  }

  // Update Navigation Subpanel Guidance
  const guideText = document.getElementById('guidance-text');
  const guideSub = document.getElementById('guidance-sub-text');
  if (guideText) guideText.innerHTML = `Route to <strong>${amenity.name}</strong>: ${amenity.distance} (${amenity.walkingTime}).`;
  if (guideSub) guideSub.textContent = `Step 1: ${amenity.steps[0].instruction}`;

  speak(`Route to ${amenity.name}. Distance ${amenity.distance}, estimated walking time ${amenity.walkingTime}. Step 1: ${amenity.steps[0].instruction}`);

  // Broadcast destination update to Family Dashboard
  addFamilyNotification(
    'info',
    '🧭',
    'Ravi Selected Destination',
    `Walking route selected to ${amenity.name} (${amenity.distance}, ${amenity.walkingTime}).`
  );
}

function startVoiceGuidance() {
  if (!state.activeDestination || !state.activeDestination.steps) return;
  const steps = state.activeDestination.steps;

  let guidanceText = `Starting walking directions to ${state.activeDestination.name}. `;
  steps.forEach((s, i) => {
    guidanceText += `Step ${i + 1}: ${s.instruction}. `;
  });

  speak(guidanceText);
}

/* ==========================================================================
   7. REAL AUDIO RECORDING (MediaRecorder) & SEND TO FAMILY
   ========================================================================== */
function initMediaRecording() {
  const recordBtn = document.getElementById('btn-record-memo');
  recordBtn?.addEventListener('click', toggleAudioRecording);
}

function toggleAudioRecording() {
  if (state.isRecording) {
    stopAudioRecording();
  } else {
    startAudioRecording();
  }
}

function startAudioRecording() {
  if (state.isRecording) return;
  getAudioContext();

  const statusBox = document.getElementById('recording-status-box');
  const btnIcon = document.getElementById('rec-btn-icon');
  const btnLabel = document.getElementById('record-memo-label');
  const timerEl = document.getElementById('rec-duration-timer');

  // Try capturing real microphone stream
  if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
    navigator.mediaDevices.getUserMedia({ audio: true })
      .then(stream => {
        state.mediaRecorder = new MediaRecorder(stream);
        state.audioChunks = [];

        state.mediaRecorder.ondataavailable = e => {
          if (e.data.size > 0) state.audioChunks.push(e.data);
        };

        state.mediaRecorder.onstop = () => {
          const audioBlob = new Blob(state.audioChunks, { type: 'audio/webm' });
          const audioUrl = URL.createObjectURL(audioBlob);
          saveNewRecording(audioUrl, audioBlob);
          stream.getTracks().forEach(t => t.stop());
        };

        state.mediaRecorder.start();
        onRecordingStarted();
      })
      .catch(err => {
        console.warn("Microphone access denied or unavailable, using fallback audio generator:", err);
        startSimulatedAudioRecording();
      });
  } else {
    startSimulatedAudioRecording();
  }

  function onRecordingStarted() {
    state.isRecording = true;
    state.currentRecordDurationSec = 0;
    state.recordingStartTime = Date.now();

    if (statusBox) statusBox.classList.remove('hidden');
    if (btnIcon) btnIcon.textContent = "⏹️";
    if (btnLabel) btnLabel.textContent = "Stop & Save Recording";

    earcons.listeningStart();
    speak("Recording started. Speak your audio message clearly.");

    state.recordingTimerInterval = setInterval(() => {
      state.currentRecordDurationSec++;
      const mins = Math.floor(state.currentRecordDurationSec / 60).toString().padStart(2, '0');
      const secs = (state.currentRecordDurationSec % 60).toString().padStart(2, '0');
      if (timerEl) timerEl.textContent = `${mins}:${secs}`;
    }, 1000);
  }

  function startSimulatedAudioRecording() {
    onRecordingStarted();
    // Simulate fallback audio on stop
    state.mediaRecorder = {
      stop: () => {
        // Create clean synthesized audio blob so it's ALWAYS playable
        const audioUrl = createSyntheticToneAudioUrl();
        saveNewRecording(audioUrl, null);
      }
    };
  }
}

function stopAudioRecording() {
  if (!state.isRecording) return;
  clearInterval(state.recordingTimerInterval);

  const statusBox = document.getElementById('recording-status-box');
  const btnIcon = document.getElementById('rec-btn-icon');
  const btnLabel = document.getElementById('record-memo-label');

  if (statusBox) statusBox.classList.add('hidden');
  if (btnIcon) btnIcon.textContent = "🎙️";
  if (btnLabel) btnLabel.textContent = "Start Recording Audio";

  state.isRecording = false;
  earcons.commandRecognized();

  if (state.mediaRecorder && typeof state.mediaRecorder.stop === 'function') {
    state.mediaRecorder.stop();
  }
}

function createSyntheticToneAudioUrl() {
  // Generates a simple valid playable WAV audio URI for fallback environments
  // 1-second clean chime WAV header
  return "data:audio/wav;base64,UklGRjIAAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YRAAAACAgICAgICAgICAgICAgICA";
}

function saveNewRecording(audioUrl, blob) {
  const durationSec = state.currentRecordDurationSec || 5;
  const mins = Math.floor(durationSec / 60).toString().padStart(2, '0');
  const secs = (durationSec % 60).toString().padStart(2, '0');
  const durationStr = `${mins}:${secs}`;
  const now = new Date().toLocaleTimeString();

  const newRec = {
    id: `rec-${Date.now()}`,
    title: `Voice Note #${state.recordings.length + 1}`,
    time: `Today at ${now}`,
    duration: durationStr,
    durationSec: durationSec,
    audioUrl: audioUrl,
    blob: blob,
    spokenContent: `Voice recording captured by Ravi (${durationStr})`
  };

  state.recordings.unshift(newRec);
  renderRecordingsList();
  speak(`Recording saved. Duration ${durationStr}. Available in My Recordings.`);
}

function renderRecordingsList() {
  const container = document.getElementById('memos-list-container');
  if (!container) return;
  container.innerHTML = '';

  if (state.recordings.length === 0) {
    container.innerHTML = '<div class="card-p" style="color:var(--text-muted)">No recordings yet. Tap "Start Recording Audio" above to capture a voice note.</div>';
    return;
  }

  state.recordings.forEach(rec => {
    const item = document.createElement('div');
    item.className = 'memo-item';
    item.innerHTML = `
      <span class="memo-icon">🎵</span>
      <div class="memo-info">
        <span class="memo-title">${rec.title}</span>
        <span class="memo-time">${rec.time} • Duration: <strong>${rec.duration}</strong></span>
      </div>
      <div class="memo-actions-group">
        <button class="btn-acc-pill btn-primary-pill btn-play-audio" data-id="${rec.id}">
          ▶ Play
        </button>
        <button class="btn-acc-pill btn-send-to-family btn-send-rec" data-id="${rec.id}">
          📤 Send to Family
        </button>
        <button class="btn-acc-pill btn-danger-pill btn-del-rec" data-id="${rec.id}">
          🗑️
        </button>
      </div>
    `;
    container.appendChild(item);
  });

  // Play button
  document.querySelectorAll('.btn-play-audio').forEach(btn => {
    btn.addEventListener('click', () => {
      const id = btn.getAttribute('data-id');
      playRecordingAudio(id);
    });
  });

  // Send to Family button
  document.querySelectorAll('.btn-send-rec').forEach(btn => {
    btn.addEventListener('click', () => {
      const id = btn.getAttribute('data-id');
      sendRecordingToFamily(id);
    });
  });

  // Delete button
  document.querySelectorAll('.btn-del-rec').forEach(btn => {
    btn.addEventListener('click', () => {
      const id = btn.getAttribute('data-id');
      deleteRecording(id);
    });
  });
}

function playRecordingAudio(id) {
  const rec = state.recordings.find(r => r.id === id);
  if (!rec) return;

  earcons.commandRecognized();
  speak(`Playing ${rec.title}...`);

  try {
    const audio = new Audio(rec.audioUrl);
    audio.play().catch(e => {
      console.warn("Audio element play error, speaking content fallback:", e);
      speak(`Audio playback: "${rec.spokenContent}"`);
    });
  } catch (e) {
    speak(`Audio playback: "${rec.spokenContent}"`);
  }
}

function sendRecordingToFamily(id) {
  const rec = state.recordings.find(r => r.id === id);
  if (!rec) return;

  // Add to Family Dashboard's Received Voice Notes
  const now = new Date().toLocaleTimeString();
  const famNote = {
    id: `fvn-${Date.now()}`,
    sender: 'Ravi',
    time: `Today at ${now}`,
    duration: rec.duration,
    audioUrl: rec.audioUrl,
    text: `Voice recording from Ravi (${rec.duration})`
  };

  state.familyReceivedVoiceNotes.unshift(famNote);
  renderFamilyVoiceNotes();

  // Log in Family Stream
  addFamilyNotification(
    'info',
    '🎙️',
    `Voice Message Received from Ravi`,
    `Ravi sent an audio voice recording (${rec.duration}). Playable in Caregiver Dashboard.`
  );

  speak("Recording dispatched to all registered family members: Lakshmi, Suresh, Kavya, and Prasad.");
  alert("Voice recording successfully sent to ALL family members!");
}

function deleteRecording(id) {
  state.recordings = state.recordings.filter(r => r.id !== id);
  renderRecordingsList();
  speak("Recording deleted.");
}

function renderFamilyVoiceNotes() {
  const list = document.getElementById('family-voice-messages-list');
  const countBadge = document.getElementById('family-voice-count');
  if (!list) return;

  list.innerHTML = '';
  if (countBadge) countBadge.textContent = `${state.familyReceivedVoiceNotes.length} message${state.familyReceivedVoiceNotes.length === 1 ? '' : 's'}`;

  state.familyReceivedVoiceNotes.forEach(note => {
    const item = document.createElement('div');
    item.className = 'voice-msg-item';
    item.innerHTML = `
      <div class="voice-msg-meta">
        <span class="voice-msg-from">🎙️ From: ${note.sender}</span>
        <span class="voice-msg-sub">Received: ${note.time} • Duration: ${note.duration}</span>
      </div>
      <button class="btn-acc-pill btn-primary-pill btn-play-fam-audio" data-id="${note.id}">
        ▶ Play Audio
      </button>
    `;
    list.appendChild(item);
  });

  document.querySelectorAll('.btn-play-fam-audio').forEach(btn => {
    btn.addEventListener('click', () => {
      const id = btn.getAttribute('data-id');
      const note = state.familyReceivedVoiceNotes.find(n => n.id === id);
      if (note) {
        if (note.audioUrl) {
          try {
            const audio = new Audio(note.audioUrl);
            audio.play().catch(() => speak(`Voice message from Ravi: "${note.text}"`));
          } catch (e) {
            speak(`Voice message from Ravi: "${note.text}"`);
          }
        } else {
          speak(`Voice message from Ravi: "${note.text}"`);
        }
      }
    });
  });
}

/* ==========================================================================
   8. EMERGENCY SOS & SAFETY ENGINE (Broadcast to ALL 4 Family Members)
   ========================================================================== */
function startSosCountdown() {
  const now = Date.now();
  if (state.isSosActive || (now - state.lastSosTriggerTime < 3000)) return;
  state.isSosActive = true;
  state.lastSosTriggerTime = now;
  state.sosCountdownSeconds = 5;

  const card = document.getElementById('user-sos-countdown-card');
  const digit = document.getElementById('sos-countdown-digit');
  const masterBtn = document.getElementById('btn-user-trigger-sos');

  if (card) card.classList.remove('hidden');
  if (masterBtn) masterBtn.classList.add('hidden');
  if (digit) digit.textContent = state.sosCountdownSeconds;

  speak(`Emergency SOS activated. Dispatching alarm to ALL family members in 5 seconds. Tap cancel to abort.`);
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
    'Emergency Alert Cancelled by Ravi',
    'Ravi aborted the SOS countdown. No emergency dispatch required.'
  );
}

function dispatchEmergencySosAlarm() {
  const digit = document.getElementById('sos-countdown-digit');
  if (digit) digit.textContent = "ALARM";

  earcons.sosAlarmBeep();
  state.sosAlarmInterval = setInterval(() => {
    earcons.sosAlarmBeep();
  }, 1200);

  speak("CRITICAL ALERT! Emergency SOS dispatched to ALL family members: Lakshmi, Suresh, Kavya, and Prasad.");

  // Broadcast to Family Dashboard in real-time
  const familyBanner = document.getElementById('family-sos-alert-banner');
  const familyTime = document.getElementById('family-sos-time-sub');
  const familyCoords = document.getElementById('family-sos-coords');
  const familyBadge = document.getElementById('family-badge-alert');

  const now = new Date().toLocaleTimeString();
  if (familyBanner) familyBanner.classList.remove('hidden');
  if (familyTime) familyTime.textContent = `Ravi triggered SOS at ${now}! Delivered to ALL 4 Family Members.`;
  if (familyCoords) familyCoords.textContent = `Location: ${state.userLocation.lat}° N, ${Math.abs(state.userLocation.lng)}° W (${state.userLocation.address})`;
  if (familyBadge) {
    familyBadge.textContent = "🚨 SOS ACTIVE";
    familyBadge.style.background = "#FF3B30";
  }

  // Prepend critical alarm notification to Family feed for all members
  addFamilyNotification(
    'alarm',
    '🚨',
    'CRITICAL EMERGENCY ALERT: Ravi triggered SOS!',
    `Ravi triggered emergency alert near 450 Market St. Delivered to Lakshmi (Mother), Suresh (Father), Kavya (Sister), and Prasad (Brother). Immediate response requested!`
  );

  // Add to User Notifications
  addUserNotification(
    'sos',
    '🚨',
    'Emergency SOS Dispatched to Family',
    `Alert sent to all 4 registered family members at ${now}.`
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

  const currentCg = state.familyMembers.find(m => m.id === state.selectedCaregiverId) || state.familyMembers[0];
  speak(`Emergency alert has been acknowledged by ${currentCg.name} (${currentCg.relationship}). Help is coordinating.`);

  addFamilyNotification(
    'warning',
    '✅',
    `Emergency Alert Acknowledged by ${currentCg.name}`,
    `${currentCg.name} (${currentCg.relationship}) acknowledged Ravi's SOS alarm.`
  );
}

function toggleLocationSharing() {
  state.isLocationSharingActive = !state.isLocationSharingActive;
  const statusP = document.getElementById('user-sharing-status-p');
  const btn = document.getElementById('btn-user-toggle-sharing');
  const famConsent = document.getElementById('fam-consent-val');
  const userPill = document.getElementById('u-sharing-pill');

  if (state.isLocationSharingActive) {
    if (statusP) statusP.innerHTML = 'Status: <strong>ACTIVELY SHARING</strong> with all family members.';
    if (btn) {
      btn.textContent = '🛑 Stop Sharing Location';
      btn.className = 'btn-acc-pill btn-danger-pill';
    }
    if (famConsent) famConsent.textContent = 'Active Sharing';
    if (userPill) userPill.textContent = '🛡️ Sharing: ON';
    speak("Location sharing activated for all family members.");
    addFamilyNotification('info', '🛡️', 'Location Sharing Resumed', 'Ravi re-enabled location sharing.');
  } else {
    if (statusP) statusP.innerHTML = 'Status: <strong style="color:var(--accent-red)">SHARING PAUSED</strong>. Family dashboard is not receiving updates.';
    if (btn) {
      btn.textContent = '▶️ Resume Sharing Location';
      btn.className = 'btn-acc-pill btn-primary-pill';
    }
    if (famConsent) famConsent.textContent = 'Sharing Paused';
    if (userPill) userPill.textContent = '🛡️ Sharing: OFF';
    speak("Location sharing paused. Family members will not track your location.");
    addFamilyNotification('warning', '⚠️', 'Location Sharing Paused by Ravi', 'Ravi paused location sharing.');
  }
}

/* ==========================================================================
   9. FAMILY DASHBOARD REMOTE ACTIONS & CAREGIVER COMMUNICATIONS
   ========================================================================== */
function sendCheckInPing() {
  const currentCg = state.familyMembers.find(m => m.id === state.selectedCaregiverId) || state.familyMembers[0];
  earcons.checkinPing();

  // Speaks out loud on User device!
  speak(`Family notification from your ${currentCg.relationship}, ${currentCg.name}: Check-in ping received. Are you doing okay Ravi?`);

  addUserNotification(
    'call',
    '👋',
    `Check-In Ping from ${currentCg.name} (${currentCg.relationship})`,
    `Received at ${new Date().toLocaleTimeString()}.`
  );

  addFamilyNotification(
    'info',
    '👋',
    `Check-In Ping Sent by ${currentCg.name}`,
    `Ping dispatched to Ravi's device with audible voice announcement.`
  );

  alert(`Check-in ping sent from ${currentCg.name}! Ravi's phone announced the chime and spoken check-in.`);
}

function sendSpokenMessageToUser() {
  const input = document.getElementById('input-family-msg');
  if (!input) return;
  const msg = input.value.trim();
  if (!msg) {
    alert("Please enter a voice message to speak on Ravi's phone.");
    return;
  }

  const currentCg = state.familyMembers.find(m => m.id === state.selectedCaregiverId) || state.familyMembers[0];

  earcons.checkinPing();
  speak(`Voice message from your ${currentCg.relationship}, ${currentCg.name}: "${msg}"`);

  // Add to User Notifications drawer
  addUserNotification(
    'voice',
    '🎙️',
    `Voice Message from ${currentCg.name} (${currentCg.relationship})`,
    `"${msg}"`,
    msg
  );

  addFamilyNotification(
    'info',
    '🗣️',
    `Voice Message Sent by ${currentCg.name}`,
    `Caregiver ${currentCg.name} spoke: "${msg}"`
  );

  input.value = '';
}

function addUserNotification(type, icon, title, detail, audioText = null) {
  const id = `un-${Date.now()}`;
  const now = new Date().toLocaleTimeString();

  state.userNotifications.unshift({
    id,
    type,
    icon,
    title,
    detail,
    time: `Today at ${now}`,
    audioText: audioText || detail,
    isRead: false
  });

  renderUserNotifications();
  updateUserNotifBadge();
}

function renderUserNotifications() {
  const list = document.getElementById('user-notifs-list');
  if (!list) return;
  list.innerHTML = '';

  if (state.userNotifications.length === 0) {
    list.innerHTML = '<div class="card-p" style="color:var(--text-muted)">No family messages or notifications.</div>';
    return;
  }

  state.userNotifications.forEach(n => {
    const item = document.createElement('div');
    item.className = `user-notif-item notif-${n.type}`;
    item.innerHTML = `
      <div class="u-notif-icon">${n.icon}</div>
      <div class="u-notif-body">
        <div class="u-notif-title">${n.title}</div>
        <div class="u-notif-detail">${n.detail}</div>
        <div class="u-notif-time">${n.time}</div>
      </div>
      ${n.audioText ? `<button class="btn-acc-pill btn-primary-pill btn-play-un-audio" data-text="${encodeURIComponent(n.audioText)}" aria-label="Play message">▶ Play</button>` : ''}
    `;
    list.appendChild(item);
  });

  document.querySelectorAll('.btn-play-un-audio').forEach(btn => {
    btn.addEventListener('click', () => {
      const text = decodeURIComponent(btn.getAttribute('data-text') || '');
      speak(text);
    });
  });
}

function updateUserNotifBadge() {
  const badge = document.getElementById('tile-notif-count');
  if (badge) {
    badge.textContent = state.userNotifications.length;
    badge.style.display = state.userNotifications.length > 0 ? 'flex' : 'none';
  }
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
   10. OCR & DOCUMENT READER
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
  const doc = sampleDocuments[state.activeDocSample || 'medicine'];
  if (!doc) return;
  earcons.commandRecognized();
  speak(`Reading ${doc.type}. ${doc.text.replace(/\n/g, '. ')}`);
}

function pauseReader() {
  if (window.speechSynthesis.speaking && !window.speechSynthesis.paused) {
    window.speechSynthesis.pause();
    speak("Paused.");
  }
}

function stopReader() {
  window.speechSynthesis.cancel();
  speak("Stopped document reading.");
}

/* ==========================================================================
   11. COMPASS & NAVIGATION
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

  if (needle) needle.style.transform = `rotate(${heading}deg)`;
  if (headingText) headingText.textContent = `${dirName} ${heading}°`;
  if (famHeading) famHeading.textContent = `${dirName} (${heading}°)`;
  if (pinArrow) pinArrow.style.transform = `rotate(${heading}deg)`;
}

function turnCompass(delta) {
  state.userLocation.heading = (state.userLocation.heading + delta + 360) % 360;
  updateCompassUI();
  const dir = getHeadingName(state.userLocation.heading);
  speak(`Turned. Now facing ${dir}, ${Math.round(state.userLocation.heading)} degrees.`);
}

/* ==========================================================================
   12. VIEW & PANEL ROUTING
   ========================================================================== */
function switchUserSubpanel(panelId) {
  state.activeUserPanel = panelId;

  document.querySelectorAll('.user-subpanel').forEach(p => p.classList.remove('active'));
  const target = document.getElementById(panelId);
  if (target) target.classList.add('active');

  document.querySelectorAll('.feat-tile').forEach(t => {
    t.classList.toggle('active', t.getAttribute('data-panel') === panelId);
  });

  if (panelId === 'panel-camera') {
    startContinuousObjectIdentification();
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
    speak("Switched to Ravi's User Dashboard.");
  } else if (mode === 'family') {
    document.body.classList.add('layout-family');
    tabFam?.classList.add('active');
    speak("Switched to Family Caregiver Dashboard.");
  } else if (mode === 'dual') {
    document.body.classList.add('layout-dual');
    tabDual?.classList.add('active');
    speak("Dual side-by-side view enabled. Ravi and Family dashboards are both active.");
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
    speak("Welcome Ravi. Audio engine active. How can I assist your navigation today?");
  };

  btnActivateAudio?.addEventListener('click', unlockAudio);
  audioBanner?.addEventListener('click', unlockAudio);

  // 2. Master Dashboard Switcher
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
    if (e.code === 'Space' && e.target.tagName !== 'INPUT' && e.target.tagName !== 'BUTTON' && e.target.tagName !== 'SELECT') {
      e.preventDefault();
      toggleVoiceListening();
    }
  });

  // 5. User Feature Navigation Tiles
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
    speak(`You are near 450 Market Street, facing ${getHeadingName(state.userLocation.heading)}.`);
  });
  document.getElementById('btn-whats-direction')?.addEventListener('click', () => {
    speak(`Facing ${getHeadingName(state.userLocation.heading)}, ${Math.round(state.userLocation.heading)} degrees.`);
  });
  document.getElementById('btn-repeat-instruction')?.addEventListener('click', () => {
    if (state.activeDestination) {
      speak(`Walking to ${state.activeDestination.name}. ${state.activeDestination.steps[0].instruction}`);
    }
  });
  document.getElementById('btn-turn-left')?.addEventListener('click', () => turnCompass(-30));
  document.getElementById('btn-turn-right')?.addEventListener('click', () => turnCompass(30));

  // 7. Camera Controls
  // Camera Action Buttons
  document.getElementById('btn-identify-object')?.addEventListener('click', () => identifyObjectShown(true));
  document.getElementById('btn-describe-scene')?.addEventListener('click', () => describeCurrentScene(true));
  document.getElementById('btn-toggle-auto-announce')?.addEventListener('click', toggleAutoAnnounce);
  document.getElementById('btn-camera-toggle')?.addEventListener('click', toggleCameraStream);
  document.getElementById('btn-find-bottle')?.addEventListener('click', findBottleTarget);
  document.getElementById('btn-stair-check')?.addEventListener('click', checkStairs);

  // Viewfinder tap to identify
  document.getElementById('viewfinder-container')?.addEventListener('click', () => identifyObjectShown(true));

  // Object presentation pill selectors
  document.querySelectorAll('.btn-pres-pill').forEach(btn => {
    btn.addEventListener('click', () => {
      const objKey = btn.getAttribute('data-obj');
      selectPresentedSimObject(objKey);
    });
  });

  // 8. Nearby Route Controls
  document.getElementById('btn-close-route')?.addEventListener('click', () => {
    document.getElementById('nearby-route-card')?.classList.add('hidden');
    speak("Route guidance closed.");
  });
  document.getElementById('btn-voice-guidance')?.addEventListener('click', startVoiceGuidance);

  // Nearby Filter Chips
  document.querySelectorAll('.am-chip').forEach(chip => {
    chip.addEventListener('click', () => {
      document.querySelectorAll('.am-chip').forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
      const cat = chip.getAttribute('data-cat');
      renderAmenities(cat);
      speak(`Filtering nearby amenities: ${cat}`);
    });
  });

  // 9. Document Reader Controls
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

  // 10. Safety & Emergency Controls
  document.getElementById('btn-user-trigger-sos')?.addEventListener('click', startSosCountdown);
  document.getElementById('btn-user-cancel-sos')?.addEventListener('click', cancelSosCountdown);
  document.getElementById('btn-user-toggle-sharing')?.addEventListener('click', toggleLocationSharing);

  // Quick Calls to Telugu Family Members
  document.querySelectorAll('.btn-quick-call').forEach(btn => {
    btn.addEventListener('click', () => {
      const name = btn.getAttribute('data-name');
      const member = state.familyMembers.find(m => m.name === name);
      if (member) initiateOutgoingCall(member);
    });
  });

  // 11. Real Audio Recorder
  initMediaRecording();

  // 12. User Notifications Center
  document.getElementById('btn-clear-user-notifs')?.addEventListener('click', () => {
    state.userNotifications = [];
    renderUserNotifications();
    updateUserNotifBadge();
    speak("User notifications cleared.");
  });

  // 13. Family Dashboard Remote Communication
  document.getElementById('btn-send-checkin')?.addEventListener('click', sendCheckInPing);
  document.getElementById('btn-send-spoken-msg')?.addEventListener('click', sendSpokenMessageToUser);
  document.getElementById('btn-family-call-user')?.addEventListener('click', () => {
    const cg = state.familyMembers.find(m => m.id === state.selectedCaregiverId) || state.familyMembers[0];
    initiateIncomingFamilyCall(cg);
  });
  document.getElementById('btn-fam-call')?.addEventListener('click', () => {
    const cg = state.familyMembers.find(m => m.id === state.selectedCaregiverId) || state.familyMembers[0];
    initiateIncomingFamilyCall(cg);
  });
  document.getElementById('btn-family-ack-sos')?.addEventListener('click', acknowledgeFamilySos);

  document.getElementById('select-caregiver-user')?.addEventListener('change', (e) => {
    state.selectedCaregiverId = e.target.value;
    const cg = state.familyMembers.find(m => m.id === state.selectedCaregiverId);
    if (cg) {
      document.getElementById('label-fam-call-btn').textContent = `Call Ravi as ${cg.name}`;
      speak(`Switched active family profile to ${cg.name}, ${cg.relationship}.`);
    }
  });

  // Enter key on Family Voice Message
  document.getElementById('input-family-msg')?.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      sendSpokenMessageToUser();
    }
  });

  document.getElementById('btn-clear-feed')?.addEventListener('click', () => {
    const feed = document.getElementById('notif-feed-list');
    if (feed) feed.innerHTML = '<div class="notif-item notif-info"><span class="notif-icon">ℹ️</span><div class="notif-body"><div class="notif-title">Feed Cleared</div><div class="notif-detail">New live events will appear here.</div></div></div>';
    speak("Family activity notifications cleared.");
  });

  // 14. Call Modal Actions (Incoming & Active Call)
  document.getElementById('btn-call-answer')?.addEventListener('click', answerIncomingCall);
  document.getElementById('btn-call-decline')?.addEventListener('click', declineIncomingCall);
  document.getElementById('btn-call-end')?.addEventListener('click', endCurrentCall);

  // 15. Accessibility Settings
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

  // 16. Initialize Components
  setupSpeechRecognition();
  updateCompassUI();
  initCamera();
  renderAmenities('all');
  renderRecordingsList();
  renderUserNotifications();
  updateUserNotifBadge();
  renderFamilyVoiceNotes();

  // Simulated walking drift for live map
  setInterval(() => {
    const pin = document.getElementById('user-live-pin');
    const pingLabel = document.getElementById('fam-last-ping');
    if (pingLabel) pingLabel.textContent = "Just now";
    if (pin && state.isLocationSharingActive) {
      const offsetX = Math.sin(Date.now() * 0.001) * 7;
      const offsetY = Math.cos(Date.now() * 0.001) * 5;
      pin.style.transform = `translate(calc(-50% + ${offsetX}px), calc(-50% + ${offsetY}px))`;
    }
  }, 2500);
});
