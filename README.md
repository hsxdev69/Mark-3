# MovieVortex 3.0 🎬

<p align="center">
  <img src="https://raw.githubusercontent.com/hsxdev69/Mark-3/master/app/src/main/res/drawable/app_icon.png" alt="MovieVortex Logo" width="120" height="120" />
</p>

<p align="center">
  <strong>The Ultimate Next-Generation Streaming &amp; Offline Entertainment Experience for Android (Mark-3 Release)</strong>
</p>

<p align="center">
  <a href="https://github.com/hsxdev69/Mark-3/releases/latest"><img src="https://img.shields.io/github/v/release/hsxdev69/Mark-3?style=for-the-badge&amp;color=white&amp;labelColor=101014&amp;label=Latest%20Release" alt="Latest Release" /></a>
  <a href="https://github.com/hsxdev69/Mark-3/actions"><img src="https://img.shields.io/github/actions/workflow/status/hsxdev69/Mark-3/build-movievortex.yml?style=for-the-badge&amp;color=white&amp;labelColor=101014&amp;label=Build" alt="Build Status" /></a>
  <a href="https://github.com/hsxdev69/Mark-3/releases"><img src="https://img.shields.io/github/downloads/hsxdev69/Mark-3/total?style=for-the-badge&amp;color=white&amp;labelColor=101014&amp;label=Downloads" alt="Total Downloads" /></a>
</p>

<p align="center">
  <a href="https://github.com/hsxdev69/Mark-3/releases/download/v3.0-latest/MovieVortex.apk">
    <img src="https://img.shields.io/badge/Direct_Download-MovieVortex.apk-white?style=for-the-badge&amp;logo=android&amp;logoColor=black&amp;labelColor=white" alt="Download MovieVortex APK" />
  </a>
</p>

---

## ⚡ Overview

**MovieVortex** is a premium, ultra-fast streaming application engineered with high-efficiency native playback, a minimalist pure monochrome OLED design, and automatic multi-server failover. Built from the ground up for movie lovers, binge-watchers, and anime fans who value speed, privacy, and zero interruptions.

---

## ✨ Key Features

### 🎬 Seamless Inline Video Player
- **Direct Playback**: Tap any movie or series to start streaming instantly with no intermediate loading screens or clunky overlays.
- **Dynamic Orientation**: 16:9 portrait player inline with content details, seamlessly transitioning into full-screen cinema mode upon rotating the device.
- **Single Loading State**: Zero double spinners or skeleton flicker—only crisp, internal video buffering indicators.

### 🛡️ Multi-Server Failover (Mark 1 - Mark 4)
- **Instant Server Tabs**: Switch instantly between **Mark 1**, **Mark 2**, **Mark 3**, and **Mark 4** directly below the video player.
- **Auto-Server Switching**: If a server experiences issues or slow speeds, MovieVortex automatically falls back to the next available server in real-time without disrupting playback.

### 📦 In-App Offline Vault & Multi-Quality Downloads
- **Private In-App Storage**: Downloaded media is stored securely inside the app’s private vault—isolated from external device folders and hidden from public gallery scanners.
- **Quality Selector**: Choose your preferred download resolution (`1080p Full HD`, `720p HD`, `480p SD`, `360p`) before downloading.
- **Downloads Section Sync**: Real-time download progress, offline playback, and storage management accessible from the dedicated Downloads & Offline Vault tab.
- **Clean Privacy Notifications**: Clean notifications with no internal provider leakage.

### 🌟 Rich Metadata & Intuitive Navigation
- **Top Cast Details**: Explore actor portraits, real names, and character roles.
- **Clean Seasons & Episode Lists**: Episode cards with high-definition thumbnails and quick season selectors.
- **More Like This**: Discover personalized movie and TV show recommendations directly below playback.

### 🖤 Pure Black & White Monochrome Aesthetic
- Crafted for OLED and AMOLED displays with pure black backgrounds (`#0A0A0C`), crisp white accents (`#FFFFFF`), and refined gray highlights.
- Zero harsh red tints or visual noise.

---

## 📲 Download & Installation

1. Click **[Direct Download MovieVortex.apk](https://github.com/hsxdev69/Mark-2/releases/download/v2.0-latest/MovieVortex.apk)** to directly download the raw `.apk` file (no zip, no extraction needed).
2. Open the downloaded `MovieVortex.apk` file on your Android device (Android 7.0+ supported).
3. If prompted, allow "Install from Unknown Sources" in your device settings.
4. Launch **MovieVortex** and enjoy instant streaming!

> You can also browse previous release notes and updates in the [Releases](https://github.com/hsxdev69/Mark-2/releases) section.

---

## 🛠️ Tech Stack & Architecture

- **UI & Layout**: Jetpack Compose, Android Jetpack, ConstraintLayout, Material 3
- **Language**: Kotlin 2.0+ (Coroutines, Flow, StateFlow)
- **Engine**: Hardware Decoding Core with Adaptive Streaming
- **Image Loading**: Coil 3 with disk caching
- **Build System**: Gradle with GitHub Actions automated CI/CD pipeline

---

## 📄 License & Disclaimer

MovieVortex is developed for educational and personal media consumption purposes. The application does not host or store any media content on its servers. All media data is fetched dynamically through decentralized streaming protocols and third-party extractors.
