# Contexto del Proyecto

## Proyecto: MD GitHub Navigator

**Tipo:** Aplicación nativa Android
**Lenguaje:** Kotlin
**UI Framework:** Jetpack Compose + Material 3 (Material You)
**Arquitectura:** Clean Architecture + MVI (Model-View-Intent)
**Min SDK:** 24 (Android 7.0 Nougat)
**Target SDK:** 36 (Android 16)
**Build System:** Gradle con Kotlin DSL
**Internacionalización (i18n):** Multi-idioma (idioma del sistema por defecto, fallback a inglés 'en'; soporte inicial Español 'es' e Inglés 'en' planificado para v0.4.0)

## Dispositivo Principal de Pruebas

- **Modelo:** TCL NXTPAPER 11 Plus
- **Pantalla:** 11.5" 2200x1440 (ratio 3:2, 229 ppi), NXTPAPER 4.0 anti-reflejo, 120Hz
- **SO:** Android 15 (actualizable a 16)
- **Chipset:** MediaTek Helio G100 (6nm)
- **RAM:** 8/12 GB
- **Almacenamiento:** 256 GB

## Propósito

Transformar repositorios de GitHub en una experiencia de lectura navegable tipo eReader para contenido Markdown en tablets y smartphones Android.

## Restricciones Clave

- Toda configuración debe persistirse (cifrada cuando sea sensible)
- Arquitectura event-driven para logs y cambios del sistema de ficheros
- No hacer commits ni pushes sin consentimiento explícito del usuario
- Git Flow branching + Conventional Commits
- Limpieza obligatoria de ramas tras fusión: una vez integrada cualquier rama (`feature/*`, `bugfix/*`, `release/*`) en `develop` o `main`, debe eliminarse localmente (`git branch -d`) y en remoto (`git push origin --delete`). Las únicas ramas protegidas y permanentes son `main` y `develop`.
- Mantenimiento continuo del Changelog: el archivo `docs/CHANGELOG.md` (bajo estándar Keep a Changelog y SemVer) debe actualizarse progresivamente con cada cambio relevante, nueva característica o corrección en la sección `[Unreleased]`.
- Promoción a main por fase implementada mediante Pull Request (PR): Cada fase completada en `develop` (con todos sus hitos verdes, tests unitarios/UI al 100%, changelog actualizado y documentación completada) debe integrarse en la rama `main` mediante un Pull Request (PR) formal con sus correspondientes notas de versión y tag semántico SemVer (`vMAJOR.MINOR.PATCH`).
- Desarrollo TDD-first
- Seguridad-first para almacenamiento de credenciales (Tink + DataStore)
- Soporte de renderizado de diagramas: Mermaid, D2, Graphviz, Excalidraw
- Documentación bilingüe (ES/EN), español por defecto
- Guía de usuario con ejemplos gráficos: la documentación para el usuario (`/docs/es/user-guide/` y `/docs/en/user-guide/`) debe incorporar capturas de pantalla, mockups de interfaz con marcos de dispositivo o diagramas visuales paso a paso para tablets (TCL NXTPAPER) y smartphones
- Marcadores (Bookmarks), anotaciones y resaltados con panel unificado: guardar marcadores en posiciones de lectura y listar conjuntamente marcadores, notas y fragmentos destacados para navegación rápida con un solo toque.
- Soporte multi-idioma de la aplicación (i18n, Fase 4 / v0.4.0): idioma del sistema por defecto, fallback automático a inglés ('values/strings.xml'), traducción completa inicial en Español ('values-es') e Inglés ('values'), y selector in-app en Ajustes
- Preparación para Google Play Store: Android App Bundle (.aab), firma segura desacoplada, Data Safety form y optimización para tablets (TCL NXTPAPER) y móviles
- Canal alternativo GitHub Releases: APKs standalone universales firmados para sideloading directo sin Google Play
- Estrategia de versionado semántico estricto (SemVer `vMAJOR.MINOR.PATCH`) sincronizado con Git tags y cálculo determinista de `versionCode`
- Concurrencia agéntica sin deadlocks: Uso intensivo y paralelo de subagentes para maximizar el aprovechamiento de CPU y GPU (compilación paralela, particionado modular, análisis concurrente), con workspaces aislados (`branch`/`share`), jerarquía acíclica estricta (DAG), timeouts defensivos y reactividad basada en eventos sin polling.

## Repositorio y Distribución

- **Remote:** git@github.com:laralbir/md-gh-navigator.git
- **Canales de Distribución:**
  1. **Google Play Store:** Android App Bundle (`.aab`) mediante Play App Signing
  2. **GitHub Releases:** Standalone universal APK (`.apk`) firmado con hash SHA-256
- **Versionado:** SemVer 2.0 (`vX.Y.Z`) con tags de Git y generación de changelogs
- **Estrategia de ramas:** Git Flow (desarrollo en `feature/*`, integración en `develop`; PR obligatoria de `develop` a `main` al completar cada fase con tag de versión y release)
- **Convención de commits:** Conventional Commits v1.0.0

---

## Desarrollo Agéntico

### Metodología: SSD (Specification-Driven Development)

Este proyecto se desarrolla guiado por agentes IA con el siguiente ciclo:

1. **Análisis** → Investigación, requisitos, decisiones de arquitectura
2. **Plan** → Plan de implementación detallado con hitos
3. **Tareas** → Tareas granulares y testables con criterios de aceptación
4. **Implementación** → Ejecución TDD con tests primero

### Agentes IA

| Agente | Rol | Fichero Config |
|--------|-----|---------------|
| **Antigravity (Google)** | Agente principal. Orquesta subagentes en paralelo. | `.ai/`, workspace skills |
| **Claude Code (Anthropic)** | Agente alternativo/complementario | `CLAUDE.md` |
| **GitHub Copilot** | Completado de código en IDE | `.github/copilot-instructions.md` |
| **Cursor** | IDE con IA integrada | `.cursorrules` |

### Ejecución con Subagentes en Paralelo

Los agentes IA (especialmente Antigravity) pueden y deben ejecutar múltiples tareas en paralelo usando subagentes para exprimir al máximo la capacidad de cómputo del sistema sin comprometer su estabilidad ni generar interbloqueos:

- **Subagentes de investigación** (`research`): Explorar el codebase, buscar documentación y analizar dependencias sin bloquear el hilo principal.
- **Subagentes de implementación** (`self`): Escribir código, refactorizar y ejecutar suites de tests en ramas o workspaces aislados.
- **Subagentes especializados** (custom): Tareas pesadas como generación de recursos gráficos, benchmarks o auditorías de accesibilidad.

#### Aprovechamiento Óptimo de Hardware (CPU & GPU)

1. **Saturación Eficiente de CPU**:
   - Compilación y testing paralelo: Aprovechar los daemons de Gradle con `--parallel` y `--max-workers` adaptados al número de núcleos lógicos del sistema.
   - Particionado modular ortogonal: Distribuir tareas concurrentes entre módulos desacoplados (e.g. un subagente en `:feature:reader` y otro en `:feature:library`), minimizando la contención de I/O en disco.
   - Límite de concurrencia: Evitar lanzar más subagentes activos que hilos de CPU disponibles ($N_{workers} \le \text{CPU Cores} - 1$) para prevenir degradación por context switching excesivo.

2. **Aceleración por GPU**:
   - Renderizado y emulación: Priorizar la aceleración hardware de GPU para emuladores Android, previsualizaciones de Compose y procesamiento de imágenes y assets gráficos.
   - Inferencia local: Si se utilizan modelos locales para tareas de soporte, asegurar la descarga a Metal/Vulkan/OpenCL liberando los núcleos de la CPU para compilación.

#### Prevención Estricta de Deadlocks (Anti-Interbloqueo)

Para garantizar que múltiples agentes y subagentes coexistan en paralelo sin llegar jamás a un *deadlock*, se aplican las siguientes 4 reglas fundamentales basadas en las condiciones de Coffman:

1. **Aislamiento de Workspaces (Exclusión Mutua Sin Conflicto)**:
   - Los subagentes que realizan escrituras de código NUNCA deben modificar simultáneamente los mismos ficheros en el mismo workspace.
   - Deben trabajar en workspaces aislados (`Workspace: 'branch'` o `'share'`) o en módulos ortogonales independientes.
2. **Eliminación de "Hold and Wait" (No Retención Activa)**:
   - Ningún subagente debe retener bloqueos exclusivos (como locks de base de datos, puertos o el daemon de Gradle) mientras espera la respuesta o finalización de otro subagente.
3. **Jerarquía Acíclica Estricta (Grafo Dirigido Sin Ciclos - DAG)**:
   - La orquestación sigue una estructura estrictamente jerárquica: Agente Principal $\rightarrow$ Subagente $\rightarrow$ Tarea.
   - Quedan prohibidas las dependencias circulares (Subagente A esperando a Subagente B mientras B espera a A).
4. **Timeouts Defensivos y Despertar Reactivo (Sin Polling Activo)**:
   - Toda comunicación inter-agente y ejecución asíncrona debe usar temporizadores defensivos con `TimerCondition` y el sistema de mensajería reactiva (`send_message`).
   - Se prohíbe el sondeo activo en bucle (`busy-waiting` o `sleep`), permitiendo que el sistema despierte reactivamente al agente ante eventos reales.

#### Desde Antigravity CLI (`agy`)

```bash
# Iniciar sesión interactiva
agy

# Slash commands clave para este proyecto:
/goal       # Tareas largas autónomas (no para hasta completar)
/teamwork-preview  # Proyecto grande con múltiples subagentes en paralelo
/plan       # Planificación detallada paso a paso
/boost      # Tareas complejas con análisis profundo
/compact    # Compactar contexto cuando la conversación es larga
```

#### Desde Antigravity 2.0 (Desktop App)

- Panel lateral con gestión de subagentes y tareas en background
- Monitorización de tareas programadas
- Vista de ficheros modificados por los agentes

### Reglas para los Agentes

1. **Leer siempre** `.ai/CONTEXT.md`, `.ai/CONVENTIONS.md` y `.ai/TASKS.md` antes de empezar
2. **Consultar** `.ai/DECISIONS.md` para decisiones de arquitectura ya tomadas
3. **No hacer commit/push** sin aprobación explícita del usuario
4. **TDD**: escribir tests antes de la implementación
5. **Documentar** cambios significativos en el CHANGELOG
6. **Conventional Commits** para todos los mensajes de commit
7. **Español** como idioma por defecto en documentación y comentarios de usuario
8. **Gestión de Issues y RCA**: Usar `./scripts/review-issues.sh` y la skill `.agents/skills/github-issues-resolver/` para revisar issues del repo, analizar su causa raíz (RCA), redactar soluciones de código, contestar al issue y actualizar/cerrar su estado.
9. **Paralelismo Seguro y Anti-Deadlock**: Lanzar subagentes en paralelo particionando el trabajo por módulos independientes y workspaces aislados, sin bucles de polling y con dependencias en DAG para aprovechar al máximo la CPU/GPU sin bloquear el sistema.
10. **PR a Main por Fase Implementada**: Al culminar todos los hitos y suites de prueba de una fase de desarrollo en `develop`, preparar y someter un Pull Request formal de `develop` hacia `main`, asegurando que el changelog, la versión SemVer y las pruebas estén completamente validadas.
