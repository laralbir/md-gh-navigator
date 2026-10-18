# Backlog de Tareas — MD GitHub Navigator

Estado actual: **Fase 4 Completada ✅** → Promoción de versión `v0.4.0` vía PR hacia `main` y arranque de **Fase 5: Pulido, Recursos Gráficos y Distribución**.

---

## Fase 0: Inicialización del Repositorio ✅
- [x] Estructura base de carpetas y ficheros de configuración (.gitignore, .editorconfig, .gitattributes)
- [x] Configuración multi-agente (.ai/CONTEXT, .ai/CONVENTIONS, .ai/README, CLAUDE.md, .cursorrules, .github/copilot-instructions.md)
- [x] Marco de documentación bilingüe (docs/es/ y docs/en/)
- [x] Configuración de variables de entorno Android (`ANDROID_HOME` en `.zshrc` y `local.properties`)
- [x] Inclusión de Licencia Apache 2.0

---

## Fase 1: Análisis y Decisiones de Arquitectura ✅ ([Plan Detallado](plans/phase-1-analysis.md))
- [x] Aprobación de ADR-001 (Kotlin 2.x) y ADR-002 (Jetpack Compose + Material 3)
- [x] Aprobación de ADR-003 (Clean Architecture + MVI + Event-Driven)
- [x] Aprobación de ADR-004 (Motor de renderizado híbrido Compose + WebView con JS/WASM local)
- [x] Aprobación de ADR-005 (Seguridad: Tink + DataStore + Keystore)
- [x] Aprobación de ADR-006 (Tema por defecto: Scholar; soporte para Paper y Neon Dev)
- [x] Aprobación de ADR-007 (Inyección de dependencias con Hilt)
- [x] Aprobación de ADR-008 (Cliente HTTP con Retrofit + OkHttp)
- [x] Aprobación de ADR-009 (Autenticación Dual: OAuth Device Flow + PAT manual)
- [x] Aprobación de ADR-010 (Caché offline selectivo: solo Markdown y sus assets)
- [x] Aprobación de ADR-011 (Soporte de diagramas y formatos extensibles)
- [x] Aprobación de ADR-012 (Licencia Apache 2.0 y package `com.laralbir.mdghnavigator`)
- [x] Aprobación de ADR-013 (Preparación y Pipeline de Publicación en Google Play Store)
- [x] Aprobación de ADR-014 (Distribución Alternativa vía GitHub Releases y SemVer)

---

## Fase 2: Plan & Arquitectura Base (Foundation) ✅ ([Plan Detallado](plans/phase-2-plan.md))

### 2.1 Estructura Gradle y Módulos ✅
- [x] Configurar Gradle con Kotlin DSL y Version Catalog (`gradle/libs.versions.toml`)
- [x] Configurar módulo `:app` con Android SDK 36, minSdk 24
- [x] Modularización del proyecto implementada y verificada (13 módulos):
  - `:core:common` (utilidades base, despachadores, EventBus)
  - `:core:designsystem` (tema Scholar, tipografías Literata/Inter, paleta M3)
  - `:core:network` (Retrofit, OkHttp, Certificate Pinning, interceptores de auth)
  - `:core:database` (Room para repos, documentos, marcadores, anotaciones)
  - `:core:datastore` (DataStore cifrado con Tink para tokens y configuración)
  - `:core:renderer` (Motor HTML/JS/WASM para Markdown, Mermaid, D2, Graphviz, Excalidraw)
  - `:feature:library` (Gestión y listado de repositorios y directorios locales)
  - `:feature:reader` (Pantalla de lectura, zoom, control de scroll, progreso)
  - `:feature:navigator` (Índice de documentos, tabla de contenidos desplegable/TOC)
  - `:feature:annotations` (Notas, subrayados y exportador)
  - `:feature:settings` (Preferencias, temas, tipografía, gestión de caché)
  - `:feature:tts` (Text-to-Speech con voces descargables)

### 2.2 Infraestructura de Testing (TDD) ✅
- [x] Configuración de JUnit 5 + MockK + Turbine para tests unitarios
- [x] Configuración de Compose Testing + Espresso para tests instrumentados
- [x] Implementación TDD del `DefaultEventBus` en `:core:common` con tests en verde
- [x] Implementación TDD de `EncryptedTokenStorage` en `:core:datastore` con Google Tink
- [x] Implementación TDD de interceptores de red en `:core:network` (`AuthInterceptorTest`, `RateLimitInterceptorTest`) con MockWebServer
- [x] Implementación TDD de contratos de contraste WCAG y tipografía en `:core:designsystem`
- [x] Implementación TDD de clasificador de links y puente JS en `:core:renderer`
- [x] Implementación TDD de TypeConverters en `:core:database`
- [x] Smoke test de cálculo de SemVer y version code en `:app`

### 2.3 Seguridad y Red Base ✅
- [x] Configurar `network_security_config.xml` con TLS 1.3 y Certificate Pinning para `api.github.com`
- [x] Módulo de cifrado seguro con Google Tink para almacenamiento de tokens de GitHub
- [x] Configuración Gradle de firma para Release (desacoplada con keystore vía variables de entorno o `secrets.properties`, soporte AAB y APK standalone)
- [x] Lógica de versionado semántico en Gradle (cálculo de `versionName` y `versionCode` a partir de Git tags / SemVer)
- [x] Verificación de construcción limpia: APK (`app-debug.apk`, 19MB) y AAB (`app-debug.aab`, 16MB) generados con éxito.


---

## Fase 3: Funcionalidades Core (Lectura y Repositorios) ✅ ([Plan Detallado](plans/phase-3-plan.md))
- [x] Implementación de `GitHubRemoteDataSource` (API GitHub v3) con soporte ETag para descargas incrementales
- [x] Implementación de `LocalDirectoryRepository` (Storage Access Framework de Android)
- [x] Selector de archivo principal de inicio (por defecto `README.md` de la raíz)
- [x] Sistema de caché selectivo: descarga y persistencia exclusiva de `.md` y assets vinculados (`SelectiveCacheManager`)
- [x] Integración del motor de renderizado Markdown con soporte para diagramas locales (`ComposeMarkdownReader`)
- [x] Pantalla de Lector con control de scroll sincronizado y persistencia automática de progreso en Room (`ReaderViewModel`, `ReaderScreen`)
- [x] Panel TOC (Tabla de Contenidos) en panel colapsable/drawer y split-pane para tablet TCL NXTPAPER 11 Plus (`TocSidebar`, `NavigatorViewModel`, `computeReaderWindowLayout`)
- [x] Navegación completa end-to-end con Navigation Compose entre `LibraryScreen` y `ReaderScreen` en `MainActivity`

---

## Fase 4: Experiencia eReader & Herramientas Avanzadas ✅ ([Plan Detallado](plans/phase-4-plan.md))
- [x] Soporte Multi-idioma (i18n): idioma del sistema por defecto, fallback automático a inglés (`res/values/strings.xml`), traducción inicial completa en Español (`res/values-es/strings.xml`) e Inglés (`res/values/strings.xml`), y selector in-app en Ajustes
- [x] Motor Text-to-Speech (TTS) nativo con selección de idioma y voces de Android
- [x] Sistema de marcadores (bookmarks), resaltados y anotaciones con panel unificado para navegación rápida, persistencia en Room y exportación a Markdown
- [x] Visor de imágenes con pinch-to-zoom y doble tap integrado en Compose (`ZoomableImageViewer`, `ZoomState`)
- [x] Configuración de tipografía, tamaño de fuente, interlineado y selector de temas (Scholar / Paper / Neon)
- [x] Gestor de almacenamiento con limpieza de caché selectiva y medidor de uso de disco (`StorageManager`)

---

## Fase 5: Pulido, Recursos Gráficos y Distribución (Google Play & GitHub Releases) ⏳
- [ ] Generación de icono de la aplicación (Adaptive Icons para Android) y pantallas splash/launch
- [ ] Tests E2E de flujos críticos de usuario (añadir repo -> sincronizar -> leer -> anotar -> reanudar)
- [ ] Auditoría de seguridad final y optimización de rendimiento en tablet TCL NXTPAPER 11 Plus
- [ ] Generación y verificación final de la documentación navegable completa (`/docs`), incorporando capturas de pantalla, diagramas y ejemplos gráficos paso a paso en la guía de usuario (`/docs/es/user-guide/` y `/docs/en/user-guide/`) para tablets (TCL NXTPAPER 11 Plus) y smartphones
- [ ] Preparación de build release (`isMinifyEnabled = true`, R8/ProGuard, subida de `mapping.txt`)
- [ ] Generación de Android App Bundle (`.aab`) firmado para Google Play Store
- [ ] Generación de APKs universales (`app-universal-release.apk`) firmados con hash SHA-256 para GitHub Releases
- [ ] Pipeline CI/CD en GitHub Actions activado por Git tags `v*` (construcción de AAB + APK y publicación automática del Release en GitHub)
- [ ] Preparación de ficha de Google Play Store:
  - Icono de alta resolución (512x512)
  - Gráfico de funciones (1024x500)
  - Capturas de pantalla para móviles y tabletas de 7" y 10" (NXTPAPER 11 Plus)
  - Formulario de Seguridad de Datos (Data Safety) y clasificación de contenido
  - Política de Privacidad pública
- [ ] Configuración de despliegue automatizado con Gradle Play Publisher (GPP) o Fastlane (pistas Interna, Cerrada y Producción)
