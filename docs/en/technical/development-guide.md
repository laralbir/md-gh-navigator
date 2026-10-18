# Development Guide — MD GitHub Navigator

This guide provides instructions to clone, set up the local environment, and develop for the project following **SSD (Specification-Driven Development)** and **TDD (Test-Driven Development)**.

---

## 1. System Requirements

- **JDK:** OpenJDK 17 or 21/25 (configured via `JAVA_HOME`).
- **Android SDK:** API 35/36 installed in `$HOME/Library/Android/sdk` (or `%LOCALAPPDATA%\Android\Sdk` on Windows).
- **Build tool:** Gradle Wrapper (included in the repository).
- **Recommended IDE:** Android Studio (Ladybug / Meerkat or higher) or Antigravity IDE / VS Code with Kotlin and Android extensions.

---

## 2. Environment Variables

Ensure your terminal (`.zshrc` or `.bashrc`) includes:
```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

---

## 3. Branching and Commits (Git Flow)

This project strictly follows **Git Flow** and **Conventional Commits**:
- `main`: Production-ready, tagged releases.
- `develop`: Integration branch for new features.
- `feature/<name>`: Working branches for specific features.

> ⚠️ **Golden Rule:** AI agents must never commit or push to remote branches without explicit user consent.

### Commit Format:
```bash
git commit -m "feat(reader): add zoom support for Mermaid diagrams"
git commit -m "test(datastore): add unit tests for Tink encryption"
```

### 3.1 Branch Cleanup & Continuous Changelog Maintenance
- **Protected branches:** `main` and `develop` (never deleted).
- **Branch cleanup:** All working branches (`feature/*`, `bugfix/*`) must be deleted locally (`git branch -d`) and remotely (`git push origin --delete`) immediately after being merged into `develop` or `main`.
- **Continuous Changelog Maintenance:** The file `docs/CHANGELOG.md` must be progressively updated with notable changes under `[Unreleased]` following [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## 4. Testing (TDD)

```bash
# Run all unit tests
./gradlew test

# Run unit tests for a specific module (e.g. core:renderer)
./gradlew :core:renderer:test

# Run lint and code style checks
./gradlew ktlintCheck
```

---

## 5. Parallel Agent Orchestration & Deadlock Prevention

To maximize hardware utilization (multi-core CPU and GPU acceleration) without risking system deadlocks:

1. **Subagent Concurrency**: Concurrent execution of subagents is encouraged for decoupled tasks (investigation, orthogonal feature implementations, graphics/assets generation).
2. **Hardware Optimization**:
   - **CPU**: Parallel Gradle builds (`org.gradle.parallel=true`) with worker concurrency capped at $N \le \text{cores} - 1$ to prevent scheduler thrashing.
   - **GPU**: Offload emulator UI acceleration, canvas rendering, and local neural inference pipelines.
3. **Anti-Deadlock Rules (Coffman Conditions)**:
   - **Isolated Workspaces**: Writing subagents work on isolated git branches or disjoint modules without file write collisions.
   - **No Hold and Wait**: Agents must not retain exclusive locks (database, daemon, ports) while awaiting external responses.
   - **Strict DAG Topology**: Hierarchical orchestration (Parent Agent $\rightarrow$ Subagent) strictly prohibiting circular wait loops.
   - **Event-Driven without Polling**: Asynchronous reactive messaging with defensive timeouts replacing busy-waiting loops.
