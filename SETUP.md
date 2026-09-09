# Local Developer Setup for SIGHTGUIDE

## Prerequisites
- **JDK 17 or 21**: Recommended Temurin or Eclipse Adoptium JDK.
- **Android Studio Jellyfish / Koala (or newer)** with Android SDK 34 / 35.
- **Git**: For version control.

## Initial Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/sightguide/sightguide-android.git
   cd sightguide-android
   ```
2. Set up `local.properties`:
   Point to your Android SDK installation directory:
   ```properties
   sdk.dir=/Users/your-user/Library/Android/sdk # macOS
   # or sdk.dir=C\:\\Users\\your-user\\AppData\\Local\\Android\\Sdk # Windows
   ```
3. Build the debug application:
   ```bash
   ./gradlew assembleDebug
   ```
4. Run tests:
   ```bash
   ./gradlew test
   ```
