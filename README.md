# 🏃‍♂️ RunningApp – Open Source Run Tracking App

A modern, lightweight, open-source run-tracking app built with Android's latest
tooling. Track runs with live GPS, review your history and weekly progress, and
dig into your stats — all wrapped in the **"Momentum"** visual design.

---

## 🎨 Design — "Momentum"

A teal/mint identity with a lime energy accent and a dark, neon-route treatment
for the in-run moment. All design tokens (colours, type scale, radii, spacing)
are centralised in `ui/theme/` — no hard-coded values per screen.

- **Display + numerals:** Space Grotesk (tabular figures)
- **Body:** Manrope
- **Palette:** Ink `#08312C` · Teal `#0F8C7E` · Mint `#54D6BA`/`#5FE0C0` · Lime `#D6F24E` (reserved for the primary run CTA & records) · Paper `#F4F7F5`

Screens: **Onboarding** · **Home & History** · **Live Run** · **Stats** · **Settings**.

---

## 🔧 Tech Stack & Architecture

- **Min SDK:** 24 · **Target/Compile SDK:** 34
- **Language:** 100% Kotlin
- **Toolchain:** Gradle 8.7 · AGP 8.5.2 · Kotlin 1.9.24 · JDK 17
- **Architecture:** MVVM with clean separation of concerns
- **Async:** Kotlin Coroutines · LiveData
- **UI Toolkit:** Jetpack Compose + Material 3

The UI is Jetpack Compose, hosted inside Fragments via `ComposeView` and wired
with the AndroidX **Navigation Component** (`nav_graph`). Each screen owns its
Compose surface; a Compose bottom bar drives the shared `NavController`.

---

## 🧩 Core Components & Libraries

### 📱 UI & Navigation
- **Jetpack Compose** (BOM) + **Material 3** – declarative UI & theming
- **Navigation Component** – fragment-hosted navigation graph
- **Space Grotesk + Manrope** – bundled Google Fonts wired into the type scale

### ⚙️ Architecture & DI
- **ViewModel + LiveData** – lifecycle-aware state
- **Hilt** – dependency injection ([Dagger Hilt](https://dagger.dev/hilt/))

### 🗄️ Data Layer
- **Room** – local database (runs + timestamped GPS traces) over SQLite
- **SharedPreferences** – profile & settings (name, weight, units, weekly goal, toggles)

### 🗺️ Location & Maps
- **Google Maps SDK** – live map with a dark style + glowing mint route on the run screen
- **FusedLocationProvider** – GPS stream via a foreground location service

### 🪵 Logs
- **Timber** – lightweight logging

---

## 🚀 Build & Run

1. Add a git-ignored `local.properties` with your `sdk.dir`.
2. (Optional) Provide a real Maps key as `GMP_KEY` in a git-ignored
   `secrets.properties` to load map tiles — a placeholder in
   `local.defaults.properties` lets the project build without one.
3. `./gradlew :app:assembleDebug`

---

## 🧭 Roadmap

- Live **ETA / time-to-finish** feature (distance goals → planned routes)
- Real API integration (auth, run sync, `/eta`) — thin client against a
  contract-first backend

---

## 📌 Contribution

Feel free to star 🌟 or fork 🍴 this project if you find it helpful!
Contributions and feedback are always welcome.
