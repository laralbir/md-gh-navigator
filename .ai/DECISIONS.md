# Architecture Decision Records (ADRs)

Este documento registra formalmente las decisiones de arquitectura del proyecto **MD GitHub Navigator**, aprobadas tras el proceso de análisis de la Fase 1.

---

## ADR-001: Lenguaje de Programación — Kotlin

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
Elegir entre Java y Kotlin para la aplicación nativa Android.

### Decisión
**Kotlin 2.x**.

### Justificación
- Lenguaje oficial recomendado por Google para Android.
- Jetpack Compose exige Kotlin (no hay soporte Java para Compose UI).
- Null-safety estricta en tiempo de compilación.
- Soporte nativo para Coroutines y Flow para concurrencia y arquitecturas reactivas/event-driven.

---

## ADR-002: UI Framework — Jetpack Compose + Material 3

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
Elegir entre el sistema tradicional de Views XML y Jetpack Compose.

### Decisión
**Jetpack Compose con Material 3 (Material You)**.

### Justificación
- Google ha colocado el sistema de Views XML en modo mantenimiento.
- Renderizado declarativo con recomposición optimizada (Strong Skipping Mode).
- Integración nativa de temas dinámicos, adaptive layouts (WindowSizeClass para tablets y smartphones).
- Gestos fluidos, animaciones y soporte edge-to-edge nativo.

---

## ADR-003: Arquitectura — Clean Architecture + MVI + Event-Driven

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
Establecer una arquitectura desacoplada, orientada a eventos para logs y cambios de ficheros, y altamente testeable mediante TDD.

### Decisión
**Clean Architecture estructurada en MVI (Model-View-Intent) con Event Bus reactivo (SharedFlow)**.

### Justificación
- **Flujo Unidireccional (UDF):** La UI emite Intents, el ViewModel procesa UseCases y produce un único `State` inmutable y `Effects` puntuales.
- **Event-Driven:** Canales reactivos (`SharedFlow`/`Channel`) para propagar cambios en el sistema de ficheros local/remoto y eventos de telemetría/logs estructurados.
- Desacoplamiento total para tests unitarios sin dependencias de Android framework en la capa de dominio.

---

## ADR-004: Motor de Renderizado — Híbrido (Compose + WebView aislado)

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
Renderizar Markdown con soporte avanzado para diagramas complejos: Mermaid, Graphviz (DOT), D2 y Excalidraw, además de fórmulas KaTeX/LaTeX.

### Decisión
**Arquitectura Híbrida**: WebView local aislado embebido en un composable `AndroidView` para el renderizado del lienzo Markdown y diagramas vía motores JS/WASM locales (sin CDN externo en ejecución offline), complementado por UI nativa en Compose para la navegación, índice TOC, anotaciones, configuración y barras de progreso.

### Justificación
- Los motores de Mermaid, Graphviz (Viz.js), D2 (WASM) y Excalidraw operan nativamente en entornos JS/WASM.
- Cargar librerías empaquetadas en `assets/` garantiza funcionamiento 100% offline y seguro.
- Comunicación bidireccional mediante `@JavascriptInterface` seguro para scroll tracking, selección de texto para anotaciones y clicks en enlaces.

---

## ADR-005: Seguridad y Gestión de Credenciales — Tink + DataStore + Keystore

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
Almacenar tokens de GitHub (OAuth / PATs) y preferencias de manera segura frente a extracciones y copias de seguridad no autorizadas.

### Decisión
**Google Tink + Jetpack DataStore + Android Keystore (TEE/StrongBox)** con configuración estricta de red (`network_security_config.xml` con Certificate Pinning y bloqueo de tráfico cleartext).

### Justificación
- `EncryptedSharedPreferences` está deprecado por Google por bloqueos de I/O síncronos y fallos de corrupción de keysets en ciertos fabricantes.
- Tink cifra streams con envelope encryption delegando la clave maestra en el hardware seguro del dispositivo.
- DataStore gestiona la persistencia de forma asíncrona mediante Coroutines y Flow.
- Desactivación de auto-backup de datos sensibles (`android:allowBackup="false"` para los stores cifrados).

---

## ADR-006: Tema Visual por Defecto — "Scholar" (Académico/Lectura Relajada)

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
Definir la paleta y tipografía principal de la app, optimizada para tablets e-paper como la TCL NXTPAPER 11 Plus.

### Decisión
**Tema "Scholar" como predeterminado**, manteniendo alternadores a "Paper" y "Neon Dev" y selector de Modo Claro / Modo Oscuro.

### Justificación
- Paleta en tonos cálidos (sepia tenue, ámbar apagado, fondo pergamino/papel mate) que maximiza el confort visual en pantallas antirreflejo (NXTPAPER 4.0).
- Tipografía Serif con excelente espaciado y altura de x (Literata / Merriweather) para el cuerpo, y sans-serif limpia para la interfaz de control.

---

## ADR-007: Inyección de Dependencias — Hilt (Dagger)

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
Elegir el framework de inyección de dependencias para el proyecto.

### Decisión
**Hilt**.

### Justificación
- Solución oficial de Google para Android.
- Validación de grafos de inyección en tiempo de compilación (evita fallos en runtime).
- Integración de primer nivel con Jetpack Compose (`hiltViewModel()`) y Navigation.

---

## ADR-008: Cliente de Red — Retrofit 2 + OkHttp 4

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
Elegir el stack de comunicación HTTP para la API de GitHub.

### Decisión
**Retrofit 2 con OkHttp 4 y Kotlinx Serialization**.

### Justificación
- Madurez de la librería, soporte robusto para interceptores de seguridad (eliminación de cabeceras Authorization en logs, inyección de tokens dinámicos).
- Soporte nativo para Certificate Pinning y control fino de caché HTTP (ETags de GitHub).

---

## ADR-009: Autenticación con GitHub — Estrategia Dual (OAuth Device Flow + PAT)

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
Permitir acceso tanto a repositorios propios como a repositorios privados de compañeros u organizaciones empresariales.

### Decisión
**Soporte dual**:
1. **GitHub OAuth Device Flow:** Ideal para login rápido en dispositivos móviles sin requerir servidores intermedios de redirección.
2. **Personal Access Token (PAT / Fine-Grained Token):** Permite introducir tokens manuales con scopes limitados para colaborar en repositorios de terceros o entornos corporativos.

---

## ADR-010: Estrategia de Caché Offline Selectivo

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
Optimizar el espacio en el almacenamiento interno de la tablet o teléfono al clonar o sincronizar repositorios que puedan contener artefactos pesados ajenos a la lectura.

### Decisión
**Caché selectivo de contenido Markdown y Assets vinculados**:
- Se almacenan localmente de forma persistente únicamente los archivos `.md` / documentación y sus recursos referenciados (imágenes, diagramas incrustados, estilos locales).
- Binarios, archivos ejecutables, dependencias o artefactos de compilación presentes en el repositorio no se descargan a la caché local y, si son referenciados, se accede a ellos vía enlace remoto bajo demanda.

---

## ADR-011: Formatos de Documentación Extensibles

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
Requisito de admitir Markdown y ampliar al máximo de formatos abiertos posibles.

### Decisión
Definir un pipeline de renderizado extensible:
- **Core v1.0:** Markdown GFM (CommonMark + tablas, tasklists, footnotes) + Mermaid + Graphviz (Viz.js) + D2 (WASM) + Excalidraw + KaTeX/LaTeX.
- **Extensiones modulares previstas:** Interfaz `DocumentRenderer` que permita acoplar procesadores para AsciiDoc y reStructuredText en iteraciones sucesivas.

---

## ADR-012: Licencia y Nomenclatura del Paquete

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Decisión
- **Licencia:** Apache License 2.0 (Open Source permisiva con protección de patentes).
- **Package Name:** `com.laralbir.mdghnavigator`.


---

## ADR-013: Preparación y Pipeline de Publicación en Google Play Store

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
El producto final se publicará y distribuirá a través de Google Play Store para tablets y smartphones, requiriendo cumplimiento de políticas de la tienda, generación de Android App Bundles (.aab), firmas criptográficas seguras y automatización del despliegue.

### Decisión
1. **Formato de Distribución:** Generación obligatoria de **Android App Bundle (.aab)** mediante Gradle (`bundleRelease`), delegando en Play App Signing la optimización y generación de APKs derivados.
2. **Gestión de Firma Segura (Signing Config):**
   - Configuración en Gradle de `signingConfigs` para `release` desacoplada del código fuente.
   - Las variables sensibles (`KEYSTORE_PATH`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`) se leen exclusivamente de variables de entorno del sistema o de un archivo `secrets.properties` ignorado en `.gitignore`.
   - Prohibido commitear archivos `.jks` / `.keystore` de producción.
3. **Ofuscación y Optimización:**
   - Activación de R8 (`isMinifyEnabled = true`, `isShrinkResources = true`) con subida automática de archivos `mapping.txt` a Play Console para desofuscación de trazas de crash.
4. **Automatización del Despliegue:**
   - Soporte para **Gradle Play Publisher (GPP)** o **Fastlane** para automatizar la subida de bundles a las pistas Interna (Internal Testing), Cerrada (Closed/Alpha) y Producción.
5. **Requisitos de Ficha y Cumplimiento:**
   - Capturas de pantalla adaptativas para teléfonos y tabletas (mínimo tabletas de 7" y 10", optimizadas para la relación 3:2 de la TCL NXTPAPER 11 Plus).
   - Ficha de Seguridad de Datos (Data Safety) declarando el uso no compartido de tokens ni datos personales.
   - Política de Privacidad alojada en GitHub Pages / documentación pública.


---

## ADR-014: Distribución Alternativa vía GitHub Releases y Estrategia de Versionado

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
Además del canal de Google Play Store, los usuarios deben poder descargar e instalar directamente la aplicación (sideloading) desde las **Releases de GitHub**, sin dependencia de los servicios de Google Play, lo que resulta crítico para dispositivos con ROMs desgooglizadas, e-readers especializados o usuarios que prefieren software libre distribuido de forma directa.

### Decisión
1. **Canal Dual de Distribución:**
   - **Google Play:** Android App Bundle (`.aab`) optimizado por dispositivo.
   - **GitHub Releases:** Compilación de APKs universales (`app-universal-release.apk`) y/o divididos por arquitectura (`arm64-v8a`, `armeabi-v7a`, `x86_64`) firmados con la clave de lanzamiento oficial, acompañados de sus hashes criptográficos `SHA-256`.
2. **Estrategia de Versionado Semántico (SemVer):**
   - **Git Tags:** Formato estricto `vMAJOR.MINOR.PATCH` (ejemplo: `v1.0.0`, `v1.1.0-beta.1`).
   - **VersionName:** Derivado directamente del tag de Git (ejemplo: `1.0.0`).
   - **VersionCode:** Número entero incremental generado de forma determinista para garantizar que actualizaciones tanto de Google Play como de GitHub Releases sean instalables sobre versiones anteriores sin conflicto (`versionCode = major * 10000 + minor * 100 + patch`).
3. **Automatización en CI/CD (GitHub Actions):**
   - Al crear y pushear un tag que coincida con `v*`, el pipeline ejecuta los tests, compila el `.aab` para Play Store y el `.apk` universal standalone para GitHub Releases, adjuntando el APK y las notas de lanzamiento generadas a partir de Conventional Commits al release correspondiente.
4. **Comprobación de Actualizaciones en la App (In-App Update Checker):**
   - Para las instalaciones desde GitHub (detectadas por el instalador `null` o sin firma de Google Play), la app podrá consultar el endpoint público de GitHub Releases (`api.github.com/repos/laralbir/md-gh-navigator/releases/latest`) para notificar al usuario de nuevas versiones disponibles para descarga directa.

---

## ADR-015: Estrategia de Concurrencia y Paralelismo Agéntico Seguro (CPU/GPU y Prevención de Deadlocks)

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
El desarrollo agéntico intensivo con múltiples agentes y subagentes concurrentes debe aprovechar de forma óptima los recursos computacionales de la máquina host (CPU multi-núcleo y GPU), pero corre el riesgo de agotar recursos del sistema, provocar contención de I/O en disco o generar bloqueos mutuos permanentes (*deadlocks*) si no se aplican restricciones formales de concurrencia.

### Decisión
1. **Aprovechamiento Eficiente de CPU & GPU:**
   - La compilación y ejecución de tests se distribuye en paralelo entre módulos ortogonales aprovechando Gradle Workers (`org.gradle.parallel=true`).
   - El número de subagentes activos concurrentes se acota dinámicamente a $N \le \text{núcleos de CPU} - 1$ para evitar el *thrashing* del planificador del sistema operativo.
   - Las cargas gráficas (emuladores de Android, renderizado Canvas/Compose) y de inferencia local se delegan a aceleración por GPU (Metal/Vulkan/OpenCL), dejando los núcleos de la CPU disponibles para análisis y compilación.
2. **Eliminación de las Cuatro Condiciones de Coffman (Anti-Deadlock):**
   - **Exclusión Mutua Sin Conflicto (Workspaces Aislados):** Todo subagente que realiza escrituras de código trabaja en su propio branch aislado (`Workspace: 'branch'` o `'share'`) o en módulos desacoplados sin intersección de ficheros.
   - **Eliminación de "Hold and Wait" (No Retención Activa):** Ningún agente retiene bloqueos exclusivos compartidos (como el daemon de Gradle, conexiones a base de datos o puertos de red) mientras espera una respuesta asíncrona de otro agente.
   - **Prevención de Inanición e Imposibilidad de Desapropiación:** Se fijan timeouts defensivos en todas las tareas asíncronas con temporizadores basados en condiciones (`TimerCondition`).
   - **Eliminación de Espera Circular (Jerarquía Estricta DAG):** La topología de orquestación es un grafo acíclico dirigido unidireccional (Agente Orquestador $\rightarrow$ Subagente Worker). Los subagentes nunca esperan mutuamente entre sí de forma cíclica.
3. **Comunicación Reactiva sin Polling:**
   - Prohibido el sondeo activo en bucle (`sleep` o bucles `while`). La comunicación inter-agente se realiza exclusivamente mediante mensajería reactiva y eventos del sistema.

---

## ADR-016: Sistema de Localización Multi-idioma (i18n), Marcadores y Panel Unificado de Lectura

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
Para ofrecer una experiencia de eReader completa, accesible internacionalmente y adaptada a la tableta TCL NXTPAPER 11 Plus, la aplicación requiere soporte nativo multi-idioma (i18n) y un sistema unificado de marcadores, notas y fragmentos destacados para navegación rápida con un solo toque y exportación a Markdown.

### Decisión
1. **Localización Multi-idioma (i18n):**
   - **Preferencia Per-App:** Soporte Android 13+ con `AppLocaleManager` y `AppCompatDelegate.setApplicationLocales`.
   - **Fallback determinista:** Cadenas base en inglés (`values/strings.xml`) y catálogo en español (`values-es/strings.xml`).
   - **Test de paridad automatizado:** `StringResourcesParityTest` en `:app` verifica en cada compilación la correspondencia exacta de claves entre catálogos de recursos.
2. **Entidad y Persistencia Unificada (Room v2):**
   - Migración de `AppDatabase` a versión 2 introduciendo `AnnotationType` (`BOOKMARK`, `HIGHLIGHT`, `NOTE`), `scrollProgress` y `scrollY` en `AnnotationEntity`.
   - Una única tabla permite consultar y ordenar cronológica o posicionalmente todos los hitos de lectura en una sola consulta reactiva de Room.
3. **Panel Unificado de Navegación Rápida (`UnifiedAnnotationsDrawer`):**
   - Panel lateral acoplado en split-pane para tablets NXTPAPER y modal drawer para smartphones.
   - Pestañas con contadores en tiempo real (*Todos*, *Marcadores*, *Resaltados*, *Notas*).
   - Salto instantáneo mediante `window.ReaderApi.scrollToProgress()` al pulsar cualquier elemento.
   - Exportador nativo `MarkdownAnnotationExporter` y compartición del resumen generado vía Android Share sheet (`ACTION_SEND`).

---

## ADR-017: Mantenimiento Continuo de Changelog y Política de Limpieza de Ramas

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
A medida que el proyecto avanza a gran velocidad con múltiples ramas de características e integración continua, se requiere mantener la trazabilidad exacta de los cambios para los usuarios y asegurar que el repositorio remoto no acumule ramas huérfanas tras sus respectivas fusiones.

### Decisión
1. **Mantenimiento Continuo del Changelog:**
   - Todo cambio relevante debe documentarse de forma incremental e inmediata en `docs/CHANGELOG.md` en la sección `[Unreleased]`, categorizado estrictamente bajo el estándar [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) (`Added`, `Changed`, `Deprecated`, `Removed`, `Fixed`, `Security`).
2. **Limpieza Automática e Inmediata de Ramas:**
   - Tan pronto como una rama (`feature/*`, `bugfix/*`, `release/*`) sea fusionada con éxito en `develop` o `main`, debe eliminarse tanto localmente (`git branch -d`) como en el repositorio remoto (`git push origin --delete`).
   - Las únicas ramas protegidas y permanentes son `main` y `develop`.

---

## ADR-018: Promoción a Main por Fases Implementadas mediante Pull Request (PR)

**Estado:** Aprobado
**Fecha:** 2026-10-18

### Contexto
Para garantizar que la rama `main` actúe como un reflejo fidedigno, inmutable y estable de cada versión entregable del producto, y evitar discrepancias o mezclas caóticas en producción, se requiere formalizar el procedimiento de integración del trabajo completado en `develop`.

### Decisión
1. **Un Pull Request a Main por Cada Fase Implementada:**
   - Cada vez que se completen e implementen todos los hitos y especificaciones de una fase de desarrollo en la rama `develop`, se debe crear y fusionar un Pull Request formal desde `develop` hacia `main`.
   - Se prohíbe el merge directo a `main` sin Pull Request o sin pasar por la validación de integración continua.
2. **Criterios de Aceptación para la Fusión del PR:**
   - **Pruebas Automatizadas:** 100% de la suite de pruebas unitarias e instrumentadas pasando en verde (`./gradlew test`).
   - **Changelog Consolidado:** Las entradas acumuladas en la sección `[Unreleased]` de `docs/CHANGELOG.md` se consolidan y fechan bajo el encabezado de versión correspondiente (`## [vX.Y.Z] - YYYY-MM-DD`).
   - **Etiquetado SemVer:** Creación y push del tag Git semántico (`vMAJOR.MINOR.PATCH`).
   - **Disparo de Releases:** El tag dispara la generación automatizada de los artefactos de distribución (Android App Bundle para Google Play y APK universal firmado para GitHub Releases).
3. **Preservación y Continuidad:**
   - Las ramas `main` y `develop` nunca se eliminan (ramas protegidas permanentes).
   - Tras la fusión, `develop` continúa abierta para la planificación e implementación de la siguiente fase del proyecto.


