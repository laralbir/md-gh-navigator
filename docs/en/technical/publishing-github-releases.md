# Distribution via GitHub Releases & Versioning — MD GitHub Navigator

This document details the environment, versioning strategy, and automation pipeline to build and distribute **MD GitHub Navigator** directly and independently via **GitHub Releases**, allowing installation on any Android device without Google Play Store dependencies.

---

## 1. Semantic Versioning Strategy (SemVer)

The project adheres to [SemVer 2.0.0](https://semver.org/spec/v2.0.0.html) and correlates it deterministically with Android packaging parameters:

- **Git Tags:** Inmutable tag format prefixed by `v` (e.g., `v1.0.0`, `v1.1.0-beta.1`).
- **versionName:** Derived automatically from the Git tag.
- **versionCode:** Deterministic integer calculated as `major * 10000 + minor * 100 + patch` ensuring sequential, conflict-free updates.

---

## 2. Standalone APK Builds

Unlike Google Play (which requires `.aab`), direct distribution requires **installable APK files**:

- **Command:** `./gradlew assembleRelease`
- **Output:** `app/build/outputs/apk/release/app-universal-release.apk`
- **Checksum:** Generated via `sha256sum app-universal-release.apk > app-universal-release.apk.sha256`

---

## 3. GitHub Actions Release Automation

Pushing any Git tag matching `v*` triggers an automated GitHub Actions pipeline that:
1. Tests and validates the code.
2. Builds both the signed `.aab` (for Google Play) and signed universal `.apk`.
3. Calculates SHA-256 checksums.
4. Publishes a new GitHub Release with changelog generated from Conventional Commits.

---

## 4. In-App Update Checker for Sideloaded Installs

For sideloaded installations, the application periodically queries GitHub's public API:
```
GET https://api.github.com/repos/laralbir/md-gh-navigator/releases/latest
```
When a newer version is found, an update banner informs the user and allows direct APK download.

