# Code Conventions

## Kotlin

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use `ktlint` for formatting enforcement
- KDoc for all public APIs
- Prefer `val` over `var`
- Use sealed classes/interfaces for state modeling
- Coroutines for async operations (no RxJava)
- Flow for reactive streams

## Package Structure (Clean Architecture)

```
com.laralbir.mdghnavigator/
├── app/                    # Application class, DI setup
├── core/                   # Shared utilities, extensions, base classes
│   ├── common/
│   ├── designsystem/       # Theme, colors, typography, components
│   ├── network/            # HTTP client, interceptors
│   ├── database/           # Room database, DAOs
│   └── datastore/          # Preferences, encrypted storage
├── feature/
│   ├── library/            # Repository library management
│   ├── reader/             # Markdown reading experience
│   ├── navigator/          # Document navigation / TOC
│   ├── annotations/        # Highlights and notes
│   ├── settings/           # App configuration
│   └── tts/               # Text-to-Speech
└── domain/
    ├── model/              # Domain entities
    ├── repository/         # Repository interfaces
    └── usecase/            # Business logic use cases
```

## Naming

- **Files:** PascalCase for classes, camelCase for functions/properties
- **Packages:** lowercase, dot-separated
- **Composables:** PascalCase, verb or noun describing the UI
- **ViewModels:** `<Feature>ViewModel`
- **UseCases:** `<Action><Entity>UseCase` (e.g., `GetRepositoryListUseCase`)
- **Repositories:** `<Entity>Repository` (interface), `<Entity>RepositoryImpl`
- **Screens:** `<Feature>Screen` (composable), `<Feature>Route` (navigation)

## Testing

- **Unit tests:** `src/test/` — JUnit 5 + MockK + Turbine (for Flows)
- **Android tests:** `src/androidTest/` — Compose Testing + Espresso
- **E2E tests:** `src/androidTest/e2e/`
- **Test naming:** `should <expected> when <condition>`

## Git

- **Branches:** `feature/<ticket>-<short-description>`, `bugfix/<ticket>-<short-description>`
- **Commits:** Conventional Commits format
- **PR titles:** Same as commit convention
- **Ramas protegidas:** `main` y `develop` (nunca se borran).
- **Limpieza de ramas:** Toda rama de trabajo (`feature/*`, `bugfix/*`, `release/*`) debe ser eliminada local (`git branch -d`) y remotamente (`git push origin --delete`) tan pronto como sea fusionada con éxito en `develop` o `main`.

## Documentation & Changelog

- **Bilingual Documentation:** All documentation must be preserved bilingually in Spanish (`docs/es/`) and English (`docs/en/`).
- **Continuous Changelog Maintenance:** The file `docs/CHANGELOG.md` must be progressively updated with every notable change, feature, or bug fix under `## [Unreleased]` adhering to [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) categories (`Added`, `Changed`, `Deprecated`, `Removed`, `Fixed`, `Security`).

## Concurrencia Agéntica y Prevención de Deadlocks

- **Particionado Ortogonal de Tareas:** Los subagentes concurrentes deben trabajar en módulos o ficheros disjuntos para evitar conflictos y bloqueos de I/O.
- **Workspaces Aislados:** Utilizar `Workspace: 'branch'` o `'share'` para subagentes que generen o modifiquen código.
- **Topología DAG:** Orquestación jerárquica unidireccional sin ciclos de espera mutua (sin bucles A espera a B y B espera a A).
- **Prohibición de Polling Activo:** Comunicación asíncrona basada en eventos reactivos (mensajes directos y eventos del sistema) con timeouts defensivos.
- **Aprovechamiento Hardware (CPU/GPU):** Limitar la concurrencia activa a los hilos de CPU reales ($N \le \text{cores} - 1$) y delegar aceleración de render/emulación a la GPU.
