# Modelo de Seguridad — MD GitHub Navigator

Este documento detalla las directrices de seguridad, el modelo de amenazas y los mecanismos de mitigación adoptados en **MD GitHub Navigator**.

---

## 1. Almacenamiento Seguro de Credenciales

### Problema
El uso de `EncryptedSharedPreferences` ha sido desaconsejado por Google debido a problemas de bloqueo en el hilo principal y fallos de corrupción de claves en determinados fabricantes OEM.

### Solución: Google Tink + Jetpack DataStore + Android Keystore
- Los tokens de acceso de GitHub (obtenidos vía OAuth Device Flow o ingresados como Personal Access Tokens) se cifran mediante **Google Tink** utilizando cifrado por envolvente (envelope encryption).
- La clave maestra reside en el hardware de seguridad del dispositivo (**TEE** o **StrongBox Keymaster**) a través de la API Android Keystore.
- La persistencia se realiza de forma totalmente asíncrona mediante **Jetpack DataStore**, evitando cuelgues o bloqueos de UI.

---

## 2. Seguridad en las Comunicaciones de Red

1. **Tráfico Cleartext Prohibido**: Se bloquea por completo cualquier comunicación HTTP sin cifrar en `network_security_config.xml`.
2. **TLS 1.3 Forzado**: Las llamadas a los servicios de GitHub (`api.github.com`, `raw.githubusercontent.com`) se realizan exclusivamente sobre TLS moderno.
3. **Certificate Pinning**: OkHttp implementa validación de huellas públicas criptográficas (pins) para evitar ataques de intermediarios (Man-In-The-Middle / proxy corporativo hostil).
4. **Sanitización de Logs en Producción**: Los interceptores de logging de red eliminan automáticamente las cabeceras `Authorization: Bearer <token>` tanto en compilaciones Release como en volcados de logs de diagnóstico.

---

## 3. Seguridad en el Lienzo WebView

El motor de renderizado de Markdown utiliza un componente WebView local que debe estar rigurosamente protegido:
- **Origen Estricto**: Sólo se permite la carga de contenido desde `file:///android_asset/` o esquemas locales controlados.
- **Sin Conectividad Externa Arbitraria**: El WebView no debe navegar directamente a páginas externas. Todo enlace web detectado se intercepta y se delega al navegador predeterminado del sistema operativo vía Android Intents.
- **Sanitización de Entradas**: El contenido Markdown se procesa para evitar inyecciones de scripts maliciosos (XSS) antes de su ensamblado en el DOM.
- **Interfaces JS Seguras**: Los métodos expuestos a través de `@JavascriptInterface` realizan validación estricta de tipos y argumentos y operan en hilos de trabajo seguros.

---

## 4. Copias de Seguridad y Protección de Ficheros

- **Desactivación de Auto-Backup para Datos Sensibles**:
  ```xml
  <application
      android:allowBackup="false"
      android:fullBackupContent="false" ... >
  ```
  Evita que los tokens de autenticación se sincronicen en texto claro con Google Drive o copias de seguridad de ADB.
- **Aislamiento en Almacenamiento Privado**: Los ficheros cacheados de los repositorios se almacenan en el directorio privado de la app (`context.noBackupFilesDir` o `context.filesDir`), inaccesible para otras aplicaciones sin privilegios root.
