# MD GitHub Navigator

<p align="center">
  <strong>📖 Un lector eReader moderno de Markdown para repositorios GitHub en Android</strong>
</p>

<p align="center">
  <a href="#características">Características</a> •
  <a href="#stack-tecnológico">Tech Stack</a> •
  <a href="#desarrollo-agéntico">Desarrollo Agéntico</a> •
  <a href="#empezar">Empezar</a> •
  <a href="docs/es/README.md">Documentación (ES)</a> •
  <a href="docs/en/README.md">Documentation (EN)</a>
</p>

---

## Descripción

MD GitHub Navigator es una aplicación nativa Android que transforma repositorios de GitHub en una experiencia de lectura navegable — como un eReader para tu documentación, libros técnicos y contenido basado en Markdown.

## Características

- 📚 **Multi-repositorio** — Añade y gestiona múltiples repositorios de GitHub
- 📄 **Rendering Markdown completo** — Soporte GFM con syntax highlighting
- 📊 **Diagramas** — Mermaid, D2, Graphviz, Excalidraw y más
- 🔖 **Progreso de lectura** — Guardado automático de posición por documento
- 📝 **Anotaciones** — Resalta texto y añade notas
- 🗂️ **Índice navegable** — Tabla de contenidos colapsable con cache
- 🔍 **Zoom y tipografía** — Fuentes, tamaños e imágenes configurables
- 🌓 **Temas** — Modos Claro/Oscuro/Sepia con temas dinámicos
- 🔊 **Texto a Voz** — Lectura en voz alta con voces descargables
- 📂 **Ficheros locales** — Navega markdown desde el almacenamiento del dispositivo
- 🔒 **Seguro** — Almacenamiento cifrado de credenciales

## Stack Tecnológico

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Arquitectura:** Clean Architecture + MVI
- **Min SDK:** 24 (Android 7.0) | **Target SDK:** 36
- **Testing:** JUnit 5, Espresso, Compose Testing, MockK

## Desarrollo Agéntico

Este proyecto sigue una metodología de **desarrollo dirigido por agentes IA** con el enfoque **SSD (Specification-Driven Development)**: Análisis → Plan → Tareas → Implementación.

### Agentes Soportados

| Agente | Configuración | Uso |
|--------|--------------|-----|
| **Antigravity (Google)** | `.ai/`, skills del workspace | Agente principal, subagentes en paralelo |
| **Claude Code (Anthropic)** | `CLAUDE.md` | Alternativo/complementario |
| **GitHub Copilot** | `.github/copilot-instructions.md` | Completado de código |
| **Cursor** | `.cursorrules` | IDE con IA integrada |

### Ejecución desde Terminal

```bash
# Lanzar Antigravity en el directorio del proyecto
cd /Users/carlos/Projects/md-gh-navigator
agy

# Usar /goal para tareas largas con ejecución autónoma
/goal Implementar el módulo de rendering de Markdown con soporte Mermaid

# Usar /teamwork-preview para tareas grandes con múltiples subagentes
/teamwork-preview Crear el sistema de navegación completo con TOC, progreso y anotaciones

# Usar /plan para planificación detallada antes de implementar
/plan Diseñar el sistema de autenticación OAuth con GitHub

# Usar /boost para tareas complejas con múltiples perspectivas
/boost Optimizar el rendimiento del WebView para documentos largos con diagramas
```

### Contexto para Agentes

Todos los agentes deben leer estos ficheros antes de trabajar:

- [`.ai/CONTEXT.md`](.ai/CONTEXT.md) — Contexto del proyecto y restricciones
- [`.ai/CONVENTIONS.md`](.ai/CONVENTIONS.md) — Convenciones de código y nombrado
- [`.ai/TASKS.md`](.ai/TASKS.md) — Backlog de tareas actual
- [`.ai/DECISIONS.md`](.ai/DECISIONS.md) — Decisiones de arquitectura (ADRs)

## Empezar

Consulta la [Guía de Desarrollo](docs/es/technical/development-guide.md) para instrucciones de configuración.

## Documentación

| Idioma | Guía de Usuario | Guía Técnica |
|--------|----------------|--------------|
| 🇪🇸 Español | [Guía de Usuario](docs/es/user-guide/README.md) | [Guía Técnica](docs/es/technical/README.md) |
| 🇬🇧 English | [User Guide](docs/en/user-guide/README.md) | [Technical Guide](docs/en/technical/README.md) |

## Contribuir

Este proyecto sigue [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) y [Git Flow](https://nvie.com/posts/a-successful-git-branching-model/).

> ⚠️ **No se realizará ningún commit ni push sin consentimiento previo del usuario.**

## Licencia

TBD
