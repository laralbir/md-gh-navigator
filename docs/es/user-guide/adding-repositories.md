# Añadir y Gestionar Repositorios

En **MD GitHub Navigator** puedes vincular múltiples fuentes de contenido: repositorios remotos en GitHub y carpetas locales de tu tablet o smartphone.

---

## 1. Conectar Repositorios de GitHub

Para añadir un repositorio alojado en GitHub:
1. Pulsa el botón flotante **`+ Añadir Repositorio`** en la pantalla principal de la Biblioteca.
2. Introduce la URL o identificador del repositorio (por ejemplo: `https://github.com/laralbir/md-gh-navigator` o `facebook/react`).
3. Elige la rama deseada (por defecto la rama principal, ej. `main` o `master`).
4. **Fichero de inicio:** Por defecto la aplicación seleccionará `README.md` del directorio raíz, pero puedes seleccionar cualquier otro archivo `.md` como punto de entrada de la lectura.

### Repositorios Privados o de Compañeros
Si el repositorio es privado o pertenece a una organización:
- **Opción A (Recomendada):** Autenticación vía **OAuth Device Flow**. La app te mostrará un código de 8 dígitos y abrirá una ventana de GitHub para autorizar el acceso en segundos.
- **Opción B:** Introducir un **Personal Access Token (PAT)** o Fine-Grained Token con permisos de lectura (`repo:read`).

---

## 2. Conectar un Directorio de Almacenamiento Local

Si almacenas notas o documentación en tu dispositivo (por ejemplo, sincronizadas con Syncthing o tu tarjeta interna):
1. En el diálogo de adición, selecciona la pestaña **Carpeta Local**.
2. Mediante el selector oficial de Android (Storage Access Framework), concede acceso a la carpeta donde residen tus documentos.
3. La aplicación indexará todos los archivos `.md` y creará un árbol de navegación idéntico al de los repositorios remotos.

---

## 3. Caché Selectivo y Ahorro de Espacio

La aplicación aplica una política de **descarga selectiva**:
- Solo se descargan a la memoria del dispositivo los ficheros de texto Markdown y los recursos gráficos vinculados dentro de ellos.
- Ficheros binarios, instaladores o carpetas ajenas a la documentación no se descargarán, ahorrando decenas de megabytes por repositorio.
