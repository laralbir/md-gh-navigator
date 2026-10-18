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
