# Contexto del Proyecto

## Proyecto: MD GitHub Navigator

**Tipo:** Aplicación nativa Android
**Lenguaje:** Kotlin
**UI Framework:** Jetpack Compose + Material 3 (Material You)
**Arquitectura:** Clean Architecture + MVI (Model-View-Intent)
**Min SDK:** 24 (Android 7.0 Nougat)
**Target SDK:** 36 (Android 16)
**Build System:** Gradle con Kotlin DSL
**Idioma por defecto:** Español (documentación bilingüe ES/EN)

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
- Desarrollo TDD-first
- Seguridad-first para almacenamiento de credenciales (Tink + DataStore)
- Soporte de renderizado de diagramas: Mermaid, D2, Graphviz, Excalidraw
- Documentación bilingüe (ES/EN), español por defecto
- Guía de usuario con ejemplos gráficos: la documentación para el usuario (`/docs/es/user-guide/` y `/docs/en/user-guide/`) debe incorporar capturas de pantalla, mockups de interfaz con marcos de dispositivo o diagramas visuales paso a paso para tablets (TCL NXTPAPER) y smartphones
- Preparación para Google Play Store: Android App Bundle (.aab), firma segura desacoplada, Data Safety form y optimización para tablets (TCL NXTPAPER) y móviles
- Canal alternativo GitHub Releases: APKs standalone universales firmados para sideloading directo sin Google Play
- Estrategia de versionado semántico estricto (SemVer `vMAJOR.MINOR.PATCH`) sincronizado con Git tags y cálculo determinista de `versionCode`

## Repositorio y Distribución

- **Remote:** git@github.com:laralbir/md-gh-navigator.git
- **Canales de Distribución:**
  1. **Google Play Store:** Android App Bundle (`.aab`) mediante Play App Signing
  2. **GitHub Releases:** Standalone universal APK (`.apk`) firmado con hash SHA-256
- **Versionado:** SemVer 2.0 (`vX.Y.Z`) con tags de Git y generación de changelogs
- **Estrategia de ramas:** Git Flow
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

Los agentes IA (especialmente Antigravity) pueden ejecutar múltiples tareas en paralelo usando subagentes. Esto es fundamental para la productividad del proyecto:

- **Subagentes de investigación** (`research`): Explorar el codebase, buscar documentación
- **Subagentes de implementación** (`self`): Escribir código en ramas separadas
- **Subagentes especializados** (custom): Tareas específicas como generación de iconos

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
