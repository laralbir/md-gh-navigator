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
