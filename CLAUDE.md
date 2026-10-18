# Instrucciones Claude Code para MD GitHub Navigator

Estás trabajando en una aplicación Android nativa escrita en Kotlin con Jetpack Compose.

## Contexto del Proyecto

- Lee `.ai/CONTEXT.md` para el contexto completo del proyecto
- Lee `.ai/CONVENTIONS.md` para las convenciones de código
- Lee `.ai/TASKS.md` para el backlog de tareas actual
- Lee `.ai/DECISIONS.md` para las decisiones de arquitectura

## Desarrollo Agéntico

Este proyecto se desarrolla con agentes IA usando la metodología **SSD** (Specification-Driven Development):
1. Análisis → 2. Plan → 3. Tareas → 4. Implementación

Los agentes pueden orquestar **subagentes en paralelo** para tareas independientes.

## Reglas Clave

1. **No hacer commits ni pushes sin consentimiento explícito del usuario**
2. Seguir Conventional Commits v1.0.0
3. Estrategia de ramas Git Flow
4. Escribir tests ANTES de la implementación (TDD)
5. Kotlin + Jetpack Compose + Material 3
6. Clean Architecture + MVI pattern
7. Persistir toda configuración (Tink + DataStore)
8. Nunca hardcodear secretos ni tokens
9. KDoc para todas las APIs públicas
10. Documentación bilingüe (docs/es/ y docs/en/), **español por defecto**
11. **Limpieza de ramas**: Borrar ramas (`git branch -d` y `git push origin --delete`) tras fusionarlas en `develop` o `main` (solo `main` y `develop` son protegidas).
12. Marcadores (Bookmarks), notas y destacados: soporte unificado para navegación rápida.

## Testing

- JUnit 5 + MockK para tests unitarios
- Compose Testing para tests de UI
- Espresso para tests E2E
- Cobertura mínima objetivo: 80%

## Gestión y Resolución de Issues (RCA)

Para inspeccionar y resolver incidencias de GitHub:
- Listar: `./scripts/review-issues.sh list`
- Ver detalle: `./scripts/review-issues.sh view <numero>`
- Analizar causa raíz (RCA) y proponer solución técnica
- Comentar: `./scripts/review-issues.sh comment <numero> "<mensaje>"`
- Cerrar: `./scripts/review-issues.sh close <numero> "<mensaje>"`
