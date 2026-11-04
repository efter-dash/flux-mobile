# FLUX · Content Production Command Centre (Android)

FLUX is a native Android application built with **Kotlin** and **Jetpack Compose** for social media teams and solo creators. Plan content, manage stage-by-stage pipelines, track daily tasks, brainstorm ideas, and log publishing analytics.

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

## 🚀 Building the App

```bash
# Build debug APK
gradle assembleDebug

# Output APK path
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 License

MIT © 2026.
