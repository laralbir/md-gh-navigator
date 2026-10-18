# Security Model — MD GitHub Navigator

This document details the security guidelines, threat model, and mitigation mechanisms adopted in **MD GitHub Navigator**.

---

## 1. Secure Credentials Storage

### Problem
Google has discouraged `EncryptedSharedPreferences` due to main-thread I/O blocking and key corruption bugs observed on several OEM devices.

### Solution: Google Tink + Jetpack DataStore + Android Keystore
- GitHub access tokens (obtained via OAuth Device Flow or Personal Access Tokens) are encrypted using **Google Tink** with envelope encryption.
- The master key resides in device hardware security (**TEE** or **StrongBox Keymaster**) via Android Keystore API.
- Persistence is completely asynchronous using **Jetpack DataStore**, preventing UI lockups.

---

## 2. Network Communication Security

1. **Cleartext Traffic Prohibited**: All non-TLS HTTP communication is strictly blocked in `network_security_config.xml`.
2. **TLS 1.3 Enforced**: All requests to GitHub endpoints (`api.github.com`, `raw.githubusercontent.com`) enforce modern TLS.
3. **Certificate Pinning**: OkHttp enforces public cryptographic key pins to mitigate Man-In-The-Middle (MITM) attacks and malicious corporate proxies.
4. **Log Sanitization**: Network logging interceptors automatically strip `Authorization: Bearer <token>` headers in release builds and diagnostic logs.

---

## 3. WebView Canvas Security

The Markdown rendering engine embeds an isolated local WebView that must be strictly hardened:
- **Strict Origin Isolation**: Only content loaded from `file:///android_asset/` or verified schemes (`https://appassets.androidplatform.net/`) is permitted.
- **No Arbitrary External Navigation**: The WebView never navigates directly to external web pages. Any web hyperlink clicked is intercepted and delegated to the system default browser via Android Intents.
- **Input Sanitization**: Markdown source code is sanitized against malicious cross-site scripting (XSS) before DOM insertion.
- **Hardened JS Interface**: Methods exposed via `@JavascriptInterface` perform strict argument validation and execute on background worker threads.

---

## 4. Backup & File Protection

- **Auto-Backup Disabled for Sensitive Data**:
  ```xml
  <application
      android:allowBackup="false"
      android:fullBackupContent="false" ... >
  ```
  Prevents authentication tokens from leaking to Google Drive or ADB backups in plaintext.
- **Private Storage Isolation**: Cached repository files reside in app-private directories (`context.noBackupFilesDir` or `context.filesDir`), inaccessible to other apps without root privileges.
