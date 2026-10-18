---
name: github-issues-resolver
description: >-
  Revisa, analiza e investiga la causa raíz de issues en el repositorio GitHub
  (https://github.com/laralbir/md-gh-navigator/issues). Formula diagnósticos
  técnicos, propone soluciones en el código, responde a los issues y gestiona
  su estado (cerrar, comentar o etiquetar).
---

# GitHub Issues Resolver & RCA Skill

Esta habilidad guía a los agentes de IA (Antigravity, Claude Code, Copilot) para gestionar de forma autónoma o asistida los issues del repositorio **MD GitHub Navigator**.

## 🛠️ Herramienta CLI de Soporte

El proyecto cuenta con el script ejecutable:
```bash
./scripts/review-issues.sh <comando> [argumentos]
```

Comandos disponibles:
- `list`: Lista todos los issues abiertos.
- `view <numero>`: Muestra el contenido y comentarios completos del issue.
- `analyze <numero>`: Extrae la carga JSON del issue para análisis.
- `comment <numero> "<mensaje>"`: Publica una respuesta técnica en el issue.
- `close <numero> "<mensaje>"`: Publica comentario y cierra como completado.
- `wontfix <numero> "<motivo>"`: Cierra como no planificado.
- `reopen <numero> [mensaje]`: Reabre un issue previamente cerrado.
- `label <numero> "<etiqueta>"`: Añade etiquetas al issue.

---

## 📋 Protocolo de Ejecución Paso a Paso

### Paso 1: Exploración y Triaje
1. Ejecuta `./scripts/review-issues.sh list` para obtener la lista de issues abiertos.
2. Si se te ha indicado un issue específico (ej. `#4`), ejecuta `./scripts/review-issues.sh view 4`.
3. Lee con atención la descripción del usuario, pasos de reproducción, logs de error o comportamientos inesperados.

### Paso 2: Análisis de Causa Raíz (Root Cause Analysis - RCA)
1. **Localizar el código afectado:** Busca en el codebase las clases, métodos o layouts implicados usando herramientas de búsqueda de código (`grep`, `view_file`).
2. **Identificar la causa raíz:**
   - ¿Es un defecto de lógica en Kotlin/Compose?
   - ¿Es un problema de concurrencia o de corrutinas/Flows?
   - ¿Es un fallo de compatibilidad de API o versión de SDK?
   - ¿Es una regresión respecto a un commit anterior?
   - ¿Es una duda de uso o solicitud de nueva funcionalidad?
3. **Reproducir o comprobar con tests:** Ejecuta o diseña un test unitario en el módulo correspondiente que reproduzca el fallo si es posible (`./gradlew :<modulo>:testDebugUnitTest`).

### Paso 3: Formulación de Conclusión y Propuesta de Solución
Elabora una conclusión estructurada:
- **Causa Raíz:** Explicación técnica clara del origen del problema, citando los archivos concretos (ej. `feature/reader/.../ReaderViewModel.kt`).
- **Impacto:** Qué pantallas o flujos se ven afectados.
- **Solución Propuesta:** Código exacto o diff necesario para solventarlo, respetando TDD y Clean Architecture.

### Paso 4: Respuesta al Issue y Actualización de Estado
1. **Si el issue requiere cambios que aún no están aprobados o necesita aclaración:**
   Publica un comentario informativo con `./scripts/review-issues.sh comment <numero> "<cuerpo>"`.

   *Estructura recomendada del comentario:*
   ```markdown
   ### 🔬 Diagnóstico y Causa Raíz
   Tras analizar el código fuente en `com.laralbir.mdghnavigator...`:
   - [Explicación técnica de la causa raíz]

   ### 💡 Solución Propuesta
   - [Explicación del cambio o enlace al PR/rama propuesta]

   ### 📌 Siguiente paso
   - [Pregunta al usuario o estado de la tarea en backlog]
   ```

2. **Si el issue está resuelto (código fusionado o verificado):**
   Cierra el issue con `./scripts/review-issues.sh close <numero> "<cuerpo>"` explicando la solución aplicada y referenciando el commit o release correspondiente (ej. `v0.3.0` o hash de commit).

3. **Si el issue no procede o está fuera de alcance:**
   Cierra con `./scripts/review-issues.sh wontfix <numero> "<motivo razonado>"`.

