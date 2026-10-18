# Estrategia de Testing — MD GitHub Navigator

Este documento formaliza la metodología **TDD (Test-Driven Development)** y la pirámide de pruebas aplicada en el desarrollo de la aplicación.

---

## 1. Pirámide de Pruebas

```
           / \
          /   \     E2E Tests (~10%)
         / E2E \    Espresso + UIAutomator
        /-------\   Flujos críticos de usuario
       /         \
      / Integrac. \  Integration Tests (~20%)
     /             \ Room DB, DataStore, Network Interceptors
    /---------------\
   /                 \ Unit Tests (~70%)
  /     Unitarias     \ JUnit 5, MockK, Turbine (Coroutines & Flows)
 /_____________________\ UseCases, ViewModels, Parsers, Loggers
```

---

## 2. Metodología TDD (Red-Green-Refactor)

Para cada funcionalidad:
1. **Red**: Escribir primero el test unitario en `src/test/` que defina el comportamiento esperado. Ejecutar y verificar que falla.
2. **Green**: Escribir la implementación mínima necesaria para hacer pasar el test.
3. **Refactor**: Limpiar código, optimizar estructuras y verificar que todos los tests se mantienen en verde.

---

## 3. Coberturas Objetivo por Capa

| Capa | Framework | Objetivo de Cobertura |
| :--- | :--- | :--- |
| **Domain** (UseCases, Entities) | JUnit 5 + MockK | **> 90%** |
| **Data** (Room, Repositorios, Network) | JUnit 5 + MockWebServer + Room In-Memory | **> 80%** |
| **Presentation** (ViewModels, MVI) | JUnit 5 + Turbine + Coroutines Test | **> 85%** |
| **UI** (Composables, Navigation) | Compose Test Rule | **> 70%** |

---

## 4. Pruebas End-to-End (E2E)

Los tests E2E verifican la integración completa en el dispositivo físico o emulador:
1. **Flujo de Onboarding & Configuración**: Configuración de tema Scholar y credenciales.
2. **Adición de Repositorio**: Conexión a un repositorio de prueba y descarga de árbol documental.
3. **Lectura y Navegación**: Despliegue de TOC, navegación entre capítulos y persistencia del scroll.
4. **Modo Offline**: Desconexión de red y validación de lectura de documentos cacheados.
5. **Anotaciones y Resaltado**: Creación de una nota y persistencia tras reinicio de la app.
