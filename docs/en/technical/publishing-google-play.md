# Google Play Store Publishing Guide — MD GitHub Navigator

This document details the architecture, guidelines, and procedures to prepare, sign, and deploy **MD GitHub Navigator** to the official Google Play Store.

---

## 1. Identifiers and Publishing Parameters

- **Package Name (Application ID):** `com.laralbir.mdghnavigator`
- **Target SDK:** API 36 (Android 16) — Complies with Google Play requirements.
- **Min SDK:** API 24 (Android 7.0 Nougat) — Over 99% device ecosystem coverage.
- **Distribution Format:** **Android App Bundle (`.aab`)**. Enables Play Feature Delivery and Dynamic Delivery to minimize download size.

---

## 2. Cryptographic Signing Management (Play App Signing)

The application adopts the official **Google Play App Signing** model:
1. **Upload Key:** Generated locally by the developer to sign App Bundles before uploading them to Play Console.
2. **App Signing Key:** Stored securely within Google's cloud infrastructure, used to sign optimized end-user APKs.

Signing credentials are never tracked in Git. They are retrieved from environment variables (CI/CD) or a local `secrets.properties` file (ignored in `.gitignore`).

---

## 3. Production App Bundle Build

```bash
./gradlew bundleRelease
```
The resulting bundle is generated at:
`app/build/outputs/bundle/release/app-release.aab`

---

## 4. Store Assets Requirements

- **App Icon:** 512 x 512 px (32-bit PNG)
- **Feature Graphic:** 1024 x 500 px (JPEG or 24-bit PNG)
- **Phone Screenshots:** At least 4 screenshots (1080 x 1920 / 1080 x 2400 px)
- **7-inch & 10-inch Tablet Screenshots:** Optimized for the 3:2 ratio on the **TCL NXTPAPER 11 Plus** (2200 x 1440 px).

---

## 5. Data Safety & Policies

- Tokens and personal data are kept locally on device and never shared with third parties.
- Network access is solely used for GitHub API and user-requested asset synchronization.
- Storage Access Framework (SAF) is used to avoid broad storage permission requests.
- Public Privacy Policy hosted via GitHub Pages.
