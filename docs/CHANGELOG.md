# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Multi-language localization (i18n): system default with English fallback, full Spanish (`values-es`) and English (`values`) resources, and automated parity testing (`StringResourcesParityTest`).
- Native Text-to-Speech (TTS) engine (`MarkdownSpeechCleaner`, `AndroidTtsSpeechEngine`, `TtsController`, `TtsPlayerBar`) with speed adjustment and clean Markdown sanitization.
- Unified bookmarks, highlights, and annotations system (`UnifiedAnnotationsDrawer`, `AddBookmarkDialog`, `MarkdownAnnotationExporter`) with one-tap fast reader navigation and Markdown export via Android Share sheet.
- Clean Architecture modular Android project (13 modules) targeting SDK 36 (minSdk 24) using Kotlin 2.x, Jetpack Compose, Material 3, and Version Catalogs.
- Design system optimized for TCL NXTPAPER 11 Plus tablets and smartphones with Scholar, Paper, and Neon Dev themes.
- Encrypted storage with Google Tink and Android Keystore (`EncryptedTokenStorage`) for GitHub PAT and OAuth credentials.
- Local Markdown reader canvas (`ComposeMarkdownReader`) with asset isolation, JavaScript bridge, and dynamic anchor/progress scrolling.
- Selective offline cache manager (`SelectiveCacheManager`) downloading only `.md` files and associated assets.
- GitHub v3 remote data source with ETag support and Storage Access Framework (SAF) local folder repository provider.
- Dual distribution pipelines: Google Play Store (AAB with Play App Signing) and GitHub Releases (standalone signed universal APK with SemVer and SHA-256).
- Architectural specification for parallel agent/subagent orchestration maximizing CPU and GPU hardware utilization while preventing deadlocks (Coffman conditions, isolated workspaces, strict DAG hierarchy, event-driven reactive messaging).
- Persisted detailed implementation plans and TDD roadmaps for all development phases (Phase 1 Analysis, Phase 2 Foundation, Phase 3 Core, Phase 4 Enhanced) under `.ai/plans/`.
- Specification and ADR-018 requiring a formal Pull Request (PR) from `develop` to `main` upon completing each development phase, including SemVer release tagging, automated CI/CD validation, and branch synchronization.

### Changed
- Upgraded Room `AppDatabase` to version 2 supporting unified annotations, bookmarks, and highlights with scroll progress.
