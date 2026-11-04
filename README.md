# FLUX · Content Production Command Centre (Android)

FLUX is a native Android application built with **Kotlin** and **Jetpack Compose** for social media teams and solo creators. Plan content, manage stage-by-stage pipelines, track daily tasks, brainstorm ideas, and log publishing analytics.

---

## 📥 Direct APK Download for Pixel 7 & Android Devices

You can download and install the app on your phone directly from this repository:

1. **Go to Releases**:
   - Navigate to the **[Releases](https://github.com/efter-dash/flux-social/releases)** section of this GitHub repository (or click **Releases** on the right sidebar).
2. **Download APK**:
   - Under **Assets**, tap **`flux-mobile.apk`** to download it directly to your Pixel 7.
3. **Install on your Pixel 7**:
   - Open your Pixel's **Files by Google** app and open **Downloads**.
   - Tap `flux-mobile.apk`.
   - If prompted with *"For your security, your phone is not allowed to install unknown apps from this source"*, tap **Settings** and toggle on **Allow from this source**.
   - Tap **Install**, then **Open**.

> 💡 **Automated Builds**: A GitHub Actions workflow (`.github/workflows/build-apk.yml`) runs on every push. GitHub Actions automatically compiles the Android app and publishes an updated `flux-mobile.apk` under **Releases** as well as under the **Actions** tab as a downloadable artifact.

---

## 📱 Features

- **Dashboard**: Monthly metrics, active production velocity, attention & blocker alerts, and stage breakdown.
- **Pipeline Kanban**: Stage-by-stage board tracking Script & Brief, Production & Shoot, Editing & Design, Review & Signoff, and Publish & Promote.
- **Content Plan**: Filter by platform (Instagram, YouTube, TikTok, LinkedIn, X, Threads) and format (Video, Static Design, Written Content, Strategic Brief).
- **Content Inspector & Editor**: Comprehensive item management with stage checklists, state dropdowns, creative captions, hashtags, and performance metrics.
- **Monthly Content Calendar**: Visual schedule grid with date selection and day-specific post agendas.
- **Tasks Checklist**: Interactive task tracking with status chips and role assignments.
- **Idea Backlog**: Concept bank with 1-click promotion into the active content pipeline.
- **Publishing & Analytics**: Live post management and performance metrics (views, reach, likes, saves, engagement rate).
- **Team Directory**: Team members, roles, responsibilities, and active workload tracking.
- **Workspace Settings**: Custom content/task/idea prefixes and pipeline configuration.

---

## 🛠️ Architecture & Tech Stack

- **Platform**: Android (minSdk 26, targetSdk 35)
- **Language**: Kotlin 2.0
- **UI Framework**: Jetpack Compose with Material Design 3 (Obsidian Flux theme)
- **Build System**: Gradle Kotlin DSL (`settings.gradle.kts`, `build.gradle.kts`, `app/build.gradle.kts`) with Version Catalog (`gradle/libs.versions.toml`)
- **State Management**: Reactive `StateFlow` and Repository pattern (`FluxRepository`)
- **Design System**: Obsidian Flux palette (Void, BaseCanvas, PanelCard, RaisedCard, FluxAccent, FluxEmerald, FluxAmber, FluxDanger)

---

## 🚀 Building Locally

```bash
# Clone the repository
git clone https://github.com/efter-dash/flux-social.git
cd flux-social

# Build debug APK
./gradlew :app:assembleDebug

# Output APK path
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 License

MIT © 2026.
