# Configuración para Agentes IA

Este directorio contiene la configuración y el contexto para los agentes IA que trabajan en este proyecto.

## Agentes Soportados

- **Antigravity (Google)** — Agente principal, orquesta subagentes en paralelo
- **Claude Code (Anthropic)** — Alternativo/complementario
- **GitHub Copilot** — Completado de código y sugerencias
- **Cursor** — IDE con IA integrada

## Metodología: SSD (Specification-Driven Development)

1. **Análisis** — Investigación, requisitos, decisiones de arquitectura
2. **Plan** — Plan detallado de implementación con tareas e hitos
3. **Tareas** — Tareas granulares, testables, con criterios de aceptación
4. **Implementación** — Ejecución TDD (test first)

## Desarrollo Agéntico con Subagentes

Este proyecto utiliza un enfoque de **desarrollo agéntico** donde los agentes IA orquestan subagentes que trabajan en paralelo:

### Flujo de Trabajo Típico

```
┌─────────────────┐
│  Agente Principal │
│  (Orquestador)   │
└────────┬─────────┘
         │
    ┌────┼────────────┐
    │    │             │
    ▼    ▼             ▼
┌──────┐ ┌──────────┐ ┌──────────┐
│Invest│ │Implement │ │ Testing  │
│igador│ │ador      │ │          │
└──────┘ └──────────┘ └──────────┘
```

### Ejecución desde Terminal

```bash
# Antigravity CLI — agente principal
agy

# Comandos útiles dentro de agy:
/goal             # Ejecución autónoma hasta completar objetivo
/teamwork-preview # Múltiples subagentes en paralelo
/plan             # Planificación detallada
/boost            # Análisis profundo multi-perspectiva
/compact          # Compactar contexto largo
/schedule         # Tareas programadas o recurrentes
```

## Ficheros Clave

| Fichero | Propósito |
|---------|-----------|
| [CONTEXT.md](CONTEXT.md) | Contexto del proyecto, restricciones, dispositivo de pruebas |
| [CONVENTIONS.md](CONVENTIONS.md) | Convenciones de código, nombrado, estructura |
| [TASKS.md](TASKS.md) | Backlog de tareas por fase |
| [DECISIONS.md](DECISIONS.md) | Architecture Decision Records (ADRs) |

## Ficheros de Configuración por Agente

| Agente | Fichero | Ubicación |
|--------|---------|-----------|
| Antigravity | `.ai/*` + workspace skills | `.ai/` |
| Claude Code | `CLAUDE.md` | Raíz del repo |
| Copilot | `copilot-instructions.md` | `.github/` |
| Cursor | `.cursorrules` | Raíz del repo |

## Reglas Universales (Todos los Agentes)

1. **Leer contexto** antes de empezar: `CONTEXT.md` + `CONVENTIONS.md` + `TASKS.md`
2. **No hacer commit/push** sin aprobación explícita del usuario
3. **TDD**: tests antes de implementación
4. **Conventional Commits** para todos los mensajes
5. **Español** como idioma por defecto en documentación
6. **Git Flow** para la estrategia de ramas
