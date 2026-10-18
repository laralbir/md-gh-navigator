# Visión de Producto y Negocio — MD GitHub Navigator

Este documento detalla los objetivos estratégicos, la propuesta de valor y el modelo de usuario para **MD GitHub Navigator**.

---

## 1. Declaración de Misión

Convertir cualquier repositorio de documentación o libro técnico alojado en GitHub en una experiencia de lectura fluida, ergonómica y análoga a la de un dispositivo eReader especializado (Kobo, Kindle, Boox), aprovechando al máximo pantallas de última generación como la tecnología mate anti-reflejo **TCL NXTPAPER 11 Plus**.

---

## 2. Propuesta de Valor

1. **Lectura Técnica Sin Fricciones**: Olvídate de navegar ramas de código o interfaces web densas para leer manuales; disfruta de una experiencia tipográfica cuidada, con modo noche, sepia y zoom responsivo.
2. **Soporte Nativo de Diagramas Abiertos**: Visualización completa de arquitecturas complejas representadas en Mermaid, Graphviz, D2 y Excalidraw directamente en el flujo de lectura.
3. **Caché Inteligente de Bajo Consumo**: Descarga únicamente el contenido documental y sus activos visuales, optimizando la memoria local de la tablet y posibilitando el estudio offline en viajes o desplazamientos.
4. **Herramientas de Estudio Avanzadas**: Anotaciones contextuales, marcadores rápidos y síntesis de voz (Text-to-Speech) para escuchar documentación mientras realizas otras actividades.

---

## 3. Perfiles de Usuario (Personas)

### Perfil A: Desarrollador / Arquitecto de Software
- **Objetivo:** Leer Whitepapers, ADRs, guías de diseño y documentación técnica de repositorios abiertos o privados de la empresa.
- **Necesidad:** Visualizar diagramas de arquitectura (Mermaid/D2) con claridad y poder resaltar conceptos clave para luego exportar sus notas.

### Perfil B: Estudiante / Investigador Técnico
- **Objetivo:** Estudiar cursos, tutoriales y libros de texto Open Source organizados en repositorios de GitHub.
- **Necesidad:** Experiencia de lectura prolongada sin fatiga visual (modo Scholar/NXTPAPER) y lectura guiada por voz (TTS).

---

## 4. Métricas de Éxito del Producto (KPIs)

- **Tiempo de Carga Inicial**: Renderizado de un documento Markdown promedio (< 100 KB) en menos de 300 ms.
- **Eficiencia de Almacenamiento**: Ahorro superior al 85% de espacio respecto a un clonado `git clone` convencional al descargar exclusivamente archivos Markdown e imágenes referenciadas.
- **Estabilidad y Confiabilidad**: Tasa de sesiones libres de crash (Crash-free users) superior al 99.5%.
- **Cobertura de Pruebas**: Mínimo 80% de cobertura en tests unitarios y de integración según la metodología TDD.
