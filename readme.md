# Retro Flap 8-Bit — Arcade Edition 🕹️🦅

Welcome to **Retro Flap 8-Bit**, a feature-rich, authentic arcade experience built for Android. Combining nostalgic 8-bit visuals, synthesized chiptune audio, and modern online social features, this game delivers the classic challenging gameplay loop enhanced with hearts, difficulty multipliers, global leaderboards, a chat terminal, and a cyberpunk event theme!

## 📱 App Layout & Recommended Image Placements

When showcasing your app repository, use the following layout to organize your screenshots and promotional graphics:

```
/docs/images/
│
├── 1_title_screen.png       # 📍 Place near the "Hero / Overview" section
├── 2_gameplay_classic.png   # 📍 Place in the "Core Gameplay & Mechanics" section
├── 3_cyber_event.png        # 📍 Place in the "Themes & Cyber Event" section
├── 4_leaderboard_chat.png   # 📍 Place in the "Online Social & Leaderboards" section
└── 5_shop_customization.png # 📍 Place in the "Character Customization Shop" section

```

### Markdown Image Insertion Guide:

```
<!-- Hero Shot -->
<p align="center">
  <img src="docs/images/1_title_screen.png" width="300" alt="Title Screen">
</p>

<!-- Gameplay Shot -->
<p align="center">
  <img src="docs/images/2_gameplay_classic.png" width="300" alt="Classic Gameplay">
  <img src="docs/images/3_cyber_event.png" width="300" alt="Cyber Event Theme">
</p>

```

## 🌟 Key Features

### 1. 🕹️ Retro 8-Bit Canvas Engine & Responsive Controls

* **Pixel-Perfect Physics:** Smooth 60 FPS update loop featuring realistic gravity, velocity curves, and dynamic pitch rotation based on vertical speed.

* **Customizable Flap Sensitivity:** Tailor touch responsiveness with selectable control profiles (*Gentle*, *Standard*, *Snappy*).

* **Animated Sprites:** 3-frame flapping animations, eye sparkles, beaks, and glowing auras for all character skins.

* **CRT Scanline Overlay:** Optional retro monitor scanline effect for authentic arcade nostalgia.

### 2. ❤️ Pixelated Hearts & Difficulty Multipliers

* **Heart Health System:** Start with 3 Pixel Hearts (4 on Easy). Colliding with a pipe costs 1 heart and grants 1.3 seconds of flashing invulnerability.

* **In-Flight Pickups:** Collect floating red hearts to heal and yellow coins between gaps.

* **Difficulty Modes & Multipliers:**

  * **Easy (1.0x):** Wider pipe gaps and slower flight speed.

  * **Normal (1.5x):** Balanced arcade rhythm with 1.5x score/coin bonus.

  * **Hard (2.0x):** Narrower gaps and faster flight speed.

  * **Cyber Insane (3.0x):** Rapid velocity, tightest gaps, and 3.0x score multipliers!

### 3. 🎨 Immersive Themes & Cyber Event

* **Classic 8-Bit Day:** Traditional bright arcade sky, stepped green pipes, and parallax scrolling hills.

* **Dark Night Mode:** Midnight blue canvas, twinkling retro stars, and dark pipes for night sessions.

* **Violet & Cyan Cyber Event:** Vibrant synthwave sky, neon cyan accents, deep black ground, and pulsing cyber aesthetic.

### 4. 🎵 Synthesized 8-Bit Audio & Haptics

* Real-time procedural tone generation via Android `AudioTrack` producing classic square and sine wave chiptunes:

  * Upward chirp for wing flaps.

  * Two-tone chimes for clearing pipes.

  * High-pitch chime for coin pickups.

  * 8-bit crunch tone on collision/damage.

  * Ascending arpeggios for health pickups & descending melodies for Game Over.

* Synchronized haptic feedback with in-game mute toggles.

### 5. 🏆 Online Social, Leaderboards & Chat

* **Global Leaderboards:** Filter by Global, Friends, or Weekly Tournaments across all difficulty tiers.

* **Head-to-Head VS Dialog:** Directly compare scores with rival pilots, check point deltas, and issue chat challenges.

* **Global 8-Bit Chat Terminal:** Real-time community chat with quick-chat phrases and verified high-score scorecard broadcasts.

### 6. 🛍️ Character Customization Shop & Upgrades

* Unlock and equip 6 pixel bird skins: *Classic Yellow, Cyber Falcon, Golden Phoenix, Pixel Bat, Astro Robo, & Fire Drake*.

* Permanent arcade upgrades: *Extra Starting Hearts (+1)* and *Coin Shield Magnet* (widened coin attraction radius).

### 7. 🎁 Additional Highlights

* **14 Unlockable Trophies:** Earn achievements and coin bounties for persistent play.

* **Daily Login Bonus:** 7-day progressive reward calendar and streak tracker.

* **Cloud Save & Offline Play:** Local Room database persistence with unique backup codes for seamless multi-device restoration.

* **Interactive Flight Academy:** Comprehensive tutorial guiding new pilots through mechanics, hearts, and controls.

## 🛠️ Tech Stack & Architecture

* **Language:** Kotlin

* **UI Framework:** Jetpack Compose (Material 3 with custom 8-bit color palette and typography)

* **Local Database:** Room Persistence Library (Entities, Daos, TypeConverters)

* **Concurrency:** Kotlin Coroutines & Flow

* **Audio:** Android `AudioTrack` synthesizer (Zero external media asset dependencies)

## 🚀 Getting Started

1. Clone the repository:

   ```
   git clone https://github.com/your-username/retro-flap-8bit.git
   
   ```

2. Open the project in Android Studio (Giraffe or newer recommended).

3. Sync Gradle and run the app on an emulator or physical Android device (API 24+).

## 