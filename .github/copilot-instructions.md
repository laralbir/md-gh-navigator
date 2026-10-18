# Instrucciones Copilot para MD GitHub Navigator

Estás trabajando en una aplicación Android escrita en Kotlin con Jetpack Compose.

## Contexto del Proyecto

- Lee `.ai/CONTEXT.md` para el contexto completo del proyecto
- Lee `.ai/CONVENTIONS.md` para las convenciones de código
- Lee `.ai/TASKS.md` para el backlog de tareas actual

## Desarrollo Agéntico

Este proyecto se desarrolla dirigido por agentes IA (Antigravity, Claude Code, Copilot, Cursor) con metodología **SSD**: Análisis → Plan → Tareas → Implementación.

## Reglas Clave

- Seguir Conventional Commits para todos los mensajes de commit
- Escribir tests ANTES de la implementación (TDD)
- Usar Kotlin coroutines y Flow (no RxJava)
- Usar Jetpack Compose para toda la UI (no XML layouts)
- Usar componentes Material 3
- Persistir toda configuración
- Nunca hacer commit de secretos ni tokens
- KDoc para todas las APIs públicas
- Español por defecto en documentación y comentarios de usuario
