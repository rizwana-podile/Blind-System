# SIGHTGUIDE Security Policy

## 1. Local Encryption & Storage Security

1. **Encrypted Preferences**: Sensitive configurations (such as caregiver device pairing tokens, emergency contact details, and SOS authorization settings) are stored using `EncryptedSharedPreferences` backed by the Android Keystore.
2. **Room Database Integrity**: Audit logs for emergency triggers and location sharing events are signed using SHA-256 HMAC tokens to ensure audit records cannot be tampered with.
3. **No Hardcoded Secrets**: No API keys, server tokens, or credentials are hardcoded into the source code repository. Configuration is passed via environment variables or secure Gradle properties.

---

## 2. Intent Security & Exported Components

- All internal Activities, BroadcastReceivers, and Services set `android:exported="false"` unless explicitly designed for OS interaction (e.g. `MainActivity` with `ACTION_MAIN`).
- PendingIntents used in notifications explicitly specify `FLAG_IMMUTABLE` to prevent intent spoofing.

---

## 3. Vulnerability Reporting

If you identify a security vulnerability within SIGHTGUIDE, please submit an issue or email `security@sightguide.org`. Disclosures will be acknowledged within 24 hours and addressed with priority fixes.
