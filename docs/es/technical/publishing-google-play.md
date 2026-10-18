# Guía de Publicación en Google Play Store — MD GitHub Navigator

Este documento describe la arquitectura, directrices y procedimientos para preparar, firmar y desplegar **MD GitHub Navigator** en la tienda oficial Google Play Store.

---

## 1. Identificadores y Parámetros de Publicación

- **Package Name (Application ID):** `com.laralbir.mdghnavigator`
- **Target SDK:** API 36 (Android 16) — Cumple con el requisito de Google Play de alinearse con el último nivel de API.
- **Min SDK:** API 24 (Android 7.0 Nougat) — Cobertura superior al 99% del parque activo de dispositivos.
- **Formato de Distribución:** **Android App Bundle (`.aab`)**. Google Play no admite APKs monolíticos para nuevas publicaciones; el bundle permite que Play Feature Delivery y Dynamic Delivery reduzcan el tamaño descargado por cada usuario.

---

## 2. Gestión de Firma Criptográfica (Play App Signing)

La app utiliza el modelo oficial de **Google Play App Signing**:
1. **Upload Key (Clave de Subida):** Generada localmente por el desarrollador para firmar los App Bundles antes de enviarlos a Play Console.
2. **App Signing Key (Clave de Firma de la App):** Custodiada en la infraestructura de claves seguras de Google, con la que se firman los APKs finales optimizados que reciben los usuarios.

### Configuración Segura en Gradle (`app/build.gradle.kts`)
Las claves de firma **jamás se guardan en el repositorio de Git**. Se configuran para ser leídas de variables de entorno (ideal para CI/CD como GitHub Actions) o de un fichero local `secrets.properties` (excluido en `.gitignore`):

```kotlin
val keystorePropertiesFile = rootProject.file("secrets.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    signingConfigs {
        create("release") {
            storeFile = file(
                System.getenv("KEYSTORE_PATH")
                    ?: keystoreProperties.getProperty("RELEASE_KEYSTORE_PATH")
                    ?: "keystore/release.jks"
            )
            storePassword = System.getenv("KEYSTORE_PASSWORD")
                ?: keystoreProperties.getProperty("RELEASE_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
                ?: keystoreProperties.getProperty("RELEASE_KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
                ?: keystoreProperties.getProperty("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## 3. Generación del Bundle de Producción

Para compilar el artefacto final listo para la subida a Play Console:

```bash
./gradlew bundleRelease
```
El archivo resultante se generará en:
`app/build/outputs/bundle/release/app-release.aab`

Asimismo, Gradle generará el archivo de mapeo R8/ProGuard en:
`app/build/outputs/mapping/release/mapping.txt`
(indispensable para desofuscar errores y reportes en Crashlytics y Play Console).

---

## 4. Requisitos de Ficha y Recursos Gráficos

Para publicar en Google Play se requieren los siguientes activos gráficos:

| Activo | Dimensiones / Formato | Propósito |
| :--- | :--- | :--- |
| **Icono de la App** | 512 x 512 px (PNG 32 bits con canal alfa) | Ficha en la tienda de Google Play |
| **Gráfico de Funciones** | 1024 x 500 px (JPEG o PNG 24 bits) | Cabecera destacada en la app store |
| **Capturas para Móvil** | Mínimo 4 capturas (1080 x 1920 px o 1080 x 2400 px) | Demostración de la interfaz en smartphones |
| **Capturas para Tablet 7"** | Mínimo 1 captura (ratio 16:9 / 16:10) | Experiencia en tabletas medianas |
| **Capturas para Tablet 10"** | Mínimo 1 captura (2200 x 1440 px o 2560 x 1600 px) | **Especial énfasis en TCL NXTPAPER 11 Plus** |

---

## 5. Cumplimiento de Políticas y Formulario de Seguridad de Datos (Data Safety)

Para la aprobación por parte del equipo de revisión de Google Play:

1. **Tokens de GitHub y Credenciales:**
   - Se declaran como datos procesados localmente en el dispositivo para autenticación.
   - **No compartidos con terceros** (no se venden ni se transmiten a analytics).
   - Se almacenan cifrados con hardware criptográfico (Google Tink + Keystore).
2. **Permisos de Red:**
   - Utilizados únicamente para sincronizar repositorios y assets autorizados expresamente por el usuario.
3. **Almacenamiento Local:**
   - La app utiliza el Storage Access Framework (SAF) de Android para respetar el Scoped Storage sin requerir permisos invasivos de almacenamiento global (`MANAGE_EXTERNAL_STORAGE`).
4. **Política de Privacidad:**
   - Debe estar enlazada en la ficha de Google Play y ser accesible públicamente (ej. vía GitHub Pages en `docs/`).

---

## 6. Automatización del Despliegue (Pipeline CI/CD)

Se contempla la integración con **Gradle Play Publisher (GPP)** (`com.github.triplet.play`) o **Fastlane** para automatizar el lanzamiento a través de las siguientes pistas (tracks):
1. **Pista Interna (Internal Testing):** Pruebas inmediatas con el equipo de desarrollo y el dispositivo de test TCL NXTPAPER.
2. **Pista Cerrada (Alpha/Beta):** Pruebas de compatibilidad con usuarios colaboradores.
3. **Pista de Producción:** Publicación escalonada (rollout progresivo: 10% -> 25% -> 50% -> 100%).
