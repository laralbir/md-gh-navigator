# Distribución vía GitHub Releases y Versionado — MD GitHub Navigator

Este documento detalla el entorno, la estrategia de versionado y la automatización para compilar y distribuir **MD GitHub Navigator** de forma directa e independiente a través de **GitHub Releases**, permitiendo la instalación en cualquier dispositivo Android sin necesidad de Google Play Store.

---

## 1. Estrategia de Versionado Semántico (SemVer)

El proyecto sigue la especificación [SemVer 2.0.0](https://semver.org/spec/v2.0.0.html) y la correlaciona de forma estricta con los parámetros de empaquetado de Android:

### Formato de Git Tags
Cada versión publicada se marca con un tag inmutable con el prefijo `v`:
```
vMAJOR.MINOR.PATCH (ejemplo: v1.0.0, v1.2.3)
vMAJOR.MINOR.PATCH-PRERELEASE (ejemplo: v1.0.0-rc.1, v1.1.0-beta.2)
```

### Derivación Automática en Gradle (`app/build.gradle.kts`)
Para evitar discrepancias entre Git y los binarios compilados, Gradle extrae la versión del tag o de las propiedades del proyecto:

```kotlin
fun getGitVersionName(): String {
    return try {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine("git", "describe", "--tags", "--always", "--dirty")
            standardOutput = stdout
        }
        stdout.toString().trim().removePrefix("v")
    } catch (e: Exception) {
        "1.0.0-dev"
    }
}

fun calculateVersionCode(versionName: String): Int {
    val clean = versionName.split("-")[0].removePrefix("v")
    val parts = clean.split(".")
    val major = parts.getOrNull(0)?.toIntOrNull() ?: 1
    val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0
    // Fórmula determinista para asegurar actualización incremental
    return major * 10000 + minor * 100 + patch
}

android {
    defaultConfig {
        versionName = getGitVersionName()
        versionCode = calculateVersionCode(versionName)
    }
}
```

---

## 2. Compilación de APKs para Distribución Directa

A diferencia de Google Play (que requiere `.aab`), la distribución por GitHub requiere **ficheros APK instalables directamente**:

### Generación del APK Universal Firmado
```bash
./gradlew assembleRelease
```
El APK universal se genera en:
`app/build/outputs/apk/release/app-universal-release.apk`

### Generación de Sumas de Verificación (Checksums SHA-256)
Para que los usuarios puedan verificar la integridad del binario descargado:
```bash
cd app/build/outputs/apk/release/
sha256sum app-universal-release.apk > app-universal-release.apk.sha256
```

---

## 3. Pipeline Automatizado de Releases (GitHub Actions)

Al pushear un tag que comience por `v*` (ej. `git push origin v1.0.0`), un flujo de trabajo de GitHub Actions automatiza el proceso:

```yaml
name: Release Pipeline

on:
  push:
    tags:
      - 'v*'

jobs:
  build-and-release:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'

      - name: Setup Android SDK
        uses: android-actions/setup-android@v3

      - name: Decrypt Keystore
        env:
          KEYSTORE_BASE64: ${{ secrets.RELEASE_KEYSTORE_BASE64 }}
        run: echo "$KEYSTORE_BASE64" | base64 --decode > app/release.jks

      - name: Build Standalone APK & Play App Bundle
        env:
          KEYSTORE_PATH: "release.jks"
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
        run: |
          ./gradlew bundleRelease
          ./gradlew assembleRelease

      - name: Generate SHA-256
        run: |
          cd app/build/outputs/apk/release/
          sha256sum app-universal-release.apk > app-universal-release.apk.sha256

      - name: Create GitHub Release
        uses: softprops/action-gh-release@v2
        with:
          files: |
            app/build/outputs/apk/release/app-universal-release.apk
            app/build/outputs/apk/release/app-universal-release.apk.sha256
            app/build/outputs/bundle/release/app-release.aab
          generate_release_notes: true
```

---

## 4. Detección de Actualizaciones Dentro de la App

Para los usuarios que han instalado el APK directamente desde GitHub (sin servicios de Google Play Store):
1. La aplicación detecta en tiempo de ejecución si fue instalada desde Play Store (`context.packageManager.getInstallSourceInfo(...)`).
2. Si la fuente es externa o nula, la pantalla de **Ajustes** consulta periódicamente el endpoint público de GitHub:
   ```
   GET https://api.github.com/repos/laralbir/md-gh-navigator/releases/latest
   ```
3. Si el `tag_name` remoto representa una versión mayor que la actual, la app muestra un banner informativo con el registro de cambios (changelog) y un botón para descargar el APK directamente en el navegador del dispositivo.

