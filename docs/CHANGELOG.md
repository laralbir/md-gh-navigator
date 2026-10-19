# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [v1.3.0] - 2026-10-19 (Beta / Pre-release)

> [!NOTE]
> Versión Beta v1.3.0 con soporte integral de enlaces ancla a encabezados y navegación fluida intra y entre documentos.

### Fixed
- **Navegación por enlaces ancla intra-documento y encabezados Markdown** ([#21](https://github.com/laralbir/md-gh-navigator/issues/21)): Inyección automática de identificadores slug compatibles con GitHub Flavored Markdown mediante `HeadingIdAttributeProvider` en CommonMark, resolución resiliente de anclas en `reader.js` con coincidencia de IDs, atributos `name`, texto de encabezados y plegado de diacríticos, y propagación de anclas en enlaces relativos al documento actual o inter-documento.

## [v1.2.0] - 2026-10-19 (Beta / Pre-release)

> [!NOTE]
> Versión Beta v1.2.0 con ampliación interactiva de diagramas Mermaid, navegación y selección de documentos en carpetas, y restauración precisa de la posición de lectura.

### Fixed
- **Visualización ampliada de diagramas Mermaid en modal interactivo** ([#18](https://github.com/laralbir/md-gh-navigator/issues/18)): Soporte para resolución de Data URIs SVG (`ImageDataUriParser`) decodificando Base64 y UTF-8 a `ByteArray` para Coil y `SvgDecoder`, incorporando un lienzo de alto contraste sobre fondo blanco con esquinas redondeadas para que los diagramas con líneas y textos oscuros sean legibles al 100%.
- **Preservación y restauración de la posición de scroll al volver atrás** ([#20](https://github.com/laralbir/md-gh-navigator/issues/20)): Pila de navegación (`NavigationBackStackEntry`) que almacena la tupla `(documentId, scrollProgress)`. Al retroceder, el motor web restaura de manera precisa la posición de lectura en `onPageFinished` con sincronización en `requestAnimationFrame`.

### Added
- **Selector de documentos Markdown para enlaces a directorios** ([#19](https://github.com/laralbir/md-gh-navigator/issues/19)): Detección de enlaces relativos a carpetas y URLs de árbol de GitHub (`/tree/<branch>/<dir>`), desplegando el diálogo interactivo `DirectoryDocumentPickerDialog` para elegir a qué fichero Markdown del directorio navegar, priorizando archivos `README.md` / `index.md` con insignia destacada.

## [v1.1.0] - 2026-10-19 (Beta / Pre-release)

> [!NOTE]
> Versión Beta v1.1.0 con resolución activa de incidencias comunitarias y mejoras significativas de navegación y accesibilidad.

### Fixed
- **Scroll fluido y eliminación de bloqueos** ([#4](https://github.com/laralbir/md-gh-navigator/issues/4)): Desactivados los gestos táctiles de apertura de paneles modales (`gesturesEnabled = false`) para evitar interferencias y bloqueos al deslizar verticalmente el contenido del lector.
- **Renderizado de diagramas Mermaid offline** ([#5](https://github.com/laralbir/md-gh-navigator/issues/5)): Inclusión de `mermaid.min.js` embebido localmente en los assets del motor web, inicialización dinámica con soporte para temas claro/oscuro y extracción de bloques `language-mermaid`.
- **Desplazamiento horizontal de tablas anchas** ([#8](https://github.com/laralbir/md-gh-navigator/issues/8)): Contenedor responsivo con `.table-wrapper { overflow-x: auto; -webkit-overflow-scrolling: touch; }` y `white-space: nowrap` para permitir desplazamiento táctil suave en tablas de datos extensas.
- **Navegación fluida entre documentos del repositorio** ([#9](https://github.com/laralbir/md-gh-navigator/issues/9)): Detección e intercepción de enlaces Markdown internos relativos (`.md`) para navegar directamente entre ficheros del mismo repositorio o carpeta local con pila de historial (`backstack`) preservada.

### Added
- **Selector de idioma y voz para Text-to-Speech (TTS)** ([#6](https://github.com/laralbir/md-gh-navigator/issues/6)): Diálogo de configuración `TtsVoiceSettingsDialog` integrado en la barra de reproducción del lector para inspeccionar motores TTS instalados, listar y cambiar idiomas disponibles (Español, Inglés, Francés, Alemán, etc.) y seleccionar variantes de voz.
- **Alias personalizados para repositorios y carpetas** ([#7](https://github.com/laralbir/md-gh-navigator/issues/7)): Soporte para asignar, editar y eliminar nombres amigables (alias) para colecciones locales y repositorios de GitHub mediante `EditAliasDialog` y persistencia en Room (`RepositoryEntity.alias`).

## [v1.0.0-beta] - 2026-10-18 (Beta / Pre-release)

> [!WARNING]
> Versión Beta preliminar para pruebas. Contiene bugs y comportamientos pendientes de resolución activa.
> Known Issues:
> - [#4: Scroll se frena al desplazarse por documentos](https://github.com/laralbir/md-gh-navigator/issues/4)
> - [#5: Mermaid no renderiza](https://github.com/laralbir/md-gh-navigator/issues/5)
> - [#6: No se puede elegir idioma y voz para el text-to-speech](https://github.com/laralbir/md-gh-navigator/issues/6)
> - [#7: Alias repositorio o carpeta configurada](https://github.com/laralbir/md-gh-navigator/issues/7)

### Added
- Custom adaptive launcher icons (`ic_launcher.xml`, `ic_launcher_round.xml`, `ic_launcher_foreground.xml`) uniting open book and git branching iconography with e-paper aesthetics.
- Native splash screen integration using AndroidX Core Splashscreen API (`installSplashScreen()`, `Theme.App.Starting`) with smooth transition into the application.
- High-resolution marketing graphics for Google Play Store: 512x512 app icon (`icon_play_512x512.png`) and 1024x500 feature graphic banner (`feature_graphic_1024x500.png`) in `docs/assets/store/`.
- High-resolution visual diagrams and UI mockups in `docs/assets/images/user-guide/` covering offline sync, TCL NXTPAPER dual-pane layout, annotations flow, TTS architecture, and themes.
- Diagram generator script `scripts/generate_guide_diagrams.py` producing 7 graphical user guide assets.
- Complete R8 and ProGuard rules (`app/proguard-rules.pro`) ensuring reflection safety for Room database DAOs, Google Tink Keystore encryption, WebView JavaScript interfaces (`ReaderBridge`), AndroidSVG, and KotlinX serialization with log stripping in release builds.
- Automated Gradle task `generateReleaseChecksums` computing and persisting SHA-256 verification checksums for release APKs and Android App Bundles (AAB).
- Automated GitHub Actions CI workflow (`.github/workflows/ci.yml`) validating all PRs and pushes to `develop` and `main` with automated tests and debug assembly.
- Automated GitHub Actions CD Release pipeline (`.github/workflows/release.yml`) triggered on `v*` tags to compile signed release AABs and universal APKs, extract release notes from CHANGELOG, and publish GitHub Releases with SHA-256 checksums.
- End-to-end reading flow integration test (`ReaderFlowIntegrationTest`) validating bookmark creation, highlight tracking, architectural note attachment, Markdown export, and theme preference binding.

## [v0.4.0] - 2026-10-18

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
- Interactive Zoomable Image & Diagram Viewer (`ZoomableImageViewer`, `ZoomState`) with pinch-to-zoom (1x to 5x), double-tap zoom toggle, pan dragging, translucent status backdrop, top controls bar with zoom level badge, and automatic WebView click interception for images and SVG diagrams.
- Application settings screen (`SettingsScreen`, `SettingsViewModel`) with live preview, dynamic theme switching (Scholar, Paper, Neon), font scaling (0.85x to 1.35x), font family selector (Literata, Inter, System), in-app language switching (Spanish, English, System default via `AppLocaleManager`), and private cache storage usage calculator and cleaner (`StorageManager`).
- Persistent user preferences repository (`UserPreferencesRepository`, `DataStoreUserPreferencesRepository`) powered by AndroidX DataStore and Hilt dependency injection.

### Changed
- Upgraded Room `AppDatabase` to version 2 supporting unified annotations, bookmarks, and highlights with scroll progress.
