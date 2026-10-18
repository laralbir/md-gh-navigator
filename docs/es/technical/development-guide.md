# Guía de Desarrollo — MD GitHub Navigator

Esta guía proporciona las instrucciones necesarias para clonar, configurar el entorno local y ejecutar el proyecto siguiendo la metodología **SSD (Specification-Driven Development)** y **TDD (Test-Driven Development)**.

---

## 1. Requisitos del Sistema

- **JDK:** OpenJDK 17 o 21/25 (configurado vía `JAVA_HOME`).
- **Android SDK:** API 35/36 instalado en `$HOME/Library/Android/sdk` (o `%LOCALAPPDATA%\Android\Sdk` en Windows).
- **Herramienta de compilación:** Gradle Wrapper (incluido en el repositorio).
- **IDE Recomendado:** Android Studio (Ladybug / Meerkat o superior) o Antigravity IDE / VS Code con extensiones de Kotlin y Android.

---

## 2. Variables de Entorno

Asegúrate de tener configurado en tu terminal (`.zshrc` o `.bashrc`):
```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

---

## 3. Flujo de Ramas y Commits (Git Flow)

Este proyecto sigue estrictamente el modelo de **Git Flow** y la especificación de **Conventional Commits**:
- `main`: Versiones de producción estables y etiquetadas.
- `develop`: Rama principal de integración para nuevas características.
- `feature/<nombre>`: Ramas de trabajo para cada funcionalidad específica.

> ⚠️ **Regla de Oro:** Ningún agente de IA realizará commits ni operaciones de push a ramas remotas sin el consentimiento expreso y previo del desarrollador.

### Formato de Commits:
```bash
git commit -m "feat(reader): añadir soporte para zoom en diagramas Mermaid"
git commit -m "test(datastore): añadir tests unitarios para cifrado con Tink"
```

### 3.1 Limpieza de Ramas y Changelog
- **Ramas protegidas:** `main` y `develop`.
- **Limpieza de ramas:** Toda rama (`feature/*`, `bugfix/*`) debe ser eliminada local y remotamente tras ser fusionada en `develop`.
- **Mantenimiento Continuo del Changelog:** El fichero `docs/CHANGELOG.md` debe irse rellenando progresivamente con los cambios, siguiendo el estándar [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) en la sección `[Unreleased]`.

### 3.2 Promoción por Fases mediante Pull Request a Main
- **Una PR a main por fase implementada**: Cada vez que se culmine la implementación de una fase completa en `develop` (con todos sus hitos y suites de pruebas al 100% en verde), se debe abrir y fusionar un Pull Request formal desde `develop` hacia `main`.
- **Requisitos de la PR de Fase**:
  1. Suite de tests unitarios e instrumentados pasando sin errores (`./gradlew test`).
  2. Actualización de `docs/CHANGELOG.md` convirtiendo las notas de `[Unreleased]` a la nueva versión semántica (`vMAJOR.MINOR.PATCH`).
  3. Creación y push del tag Git correspondiente para disparar la publicación de releases (Google Play AAB y GitHub Releases APK).

---

## 4. Ejecución de Tests (TDD)

```bash
# Ejecutar todas las pruebas unitarias
./gradlew test

# Ejecutar pruebas unitarias de un módulo concreto (ej. core:renderer)
./gradlew :core:renderer:test

# Ejecutar análisis de lint y formato de código
./gradlew ktlintCheck
```

---

## 5. Orquestación de Agentes en Paralelo y Anti-Deadlock

Para maximizar el rendimiento computacional de la máquina local (CPU multi-núcleo y aceleración por GPU):

1. **Paralelismo de Subagentes**: Se permite la ejecución concurrente de subagentes para tareas desacopladas (investigación, implementación de módulos ortogonales, generación de recursos).
2. **Optimización de Hardware**:
   - **CPU**: Compilación en paralelo con Gradle (`org.gradle.parallel=true`), limitando los subagentes concurrentes a $N \le \text{cores} - 1$ para evitar saturación del planificador.
   - **GPU**: Delegación de aceleración de renderizado en emuladores y procesamiento gráfico.
3. **Reglas Anti-Deadlock (Condiciones de Coffman)**:
   - **Workspaces Aislados**: Los agentes de escritura operan en branches o módulos independientes sin colisión de ficheros.
   - **Sin Bloqueos Activos (No Hold and Wait)**: Ningún agente retiene recursos compartidos exclusivos mientras aguarda respuestas externas.
   - **Topología DAG**: Dependencias unidireccionales estrictas (Orquestador $\rightarrow$ Subagente) sin esperas circulares.
   - **Event-Driven sin Polling**: Comunicación reactiva con timeouts defensivos evitando bucles de espera activa.
