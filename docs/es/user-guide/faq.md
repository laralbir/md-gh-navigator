# Preguntas Frecuentes (FAQ)

Respuestas a las dudas más habituales sobre el funcionamiento de **MD GitHub Navigator**.

---

### ¿Funciona la aplicación sin conexión a Internet (offline)?
**Sí.** Gracias al sistema de caché selectivo, una vez que sincronizas un repositorio o abres una carpeta local, todos los documentos Markdown e imágenes vinculadas quedan almacenados en tu dispositivo para su lectura y consulta sin conexión.

### ¿Se descargan los ficheros compilados o ejecutables del repositorio?
**No.** Para proteger el almacenamiento interno de tu dispositivo, la aplicación filtra y excluye binarios, instaladores o carpetas ajenas a la documentación. Si pulsas sobre un enlace a un archivo binario, se abrirá mediante el navegador web o tu gestor de descargas habitual.

### ¿Puedo utilizar la aplicación con repositorios privados de mi empresa?
**Sí.** Puedes iniciar sesión mediante el flujo OAuth Device Flow o añadir un Personal Access Token (PAT) con alcance de lectura (`repo:read`). Todos los tokens se almacenan cifrados con hardware criptográfico (Android Keystore + Google Tink).

### ¿Cuáles son los temas de visualización disponibles?
La aplicación incluye por defecto el tema **GitHub Modern (Paper)**, con fondo blanco limpio, acentos en azul eléctrico y alto contraste, garantizando una lectura técnica fresca y nítida. También dispones del tema **Scholar** (paleta suave de contraste equilibrado ideal para sesiones prolongadas y pantallas mate como la **TCL NXTPAPER 11 Plus**) y **Neon Dev** (modo oscuro estilo terminal). Puedes alternar entre ellos con un solo toque desde la barra superior o en los ajustes.

### ¿Se pueden exportar las anotaciones que realizo en los documentos?
**Sí.** Puedes exportar tus citas y notas en formato Markdown estándar para importarlas en herramientas como Obsidian, Logseq, Notion o enviarlas por correo electrónico.
