# Testing Strategy — MD GitHub Navigator

This document formalizes the **TDD (Test-Driven Development)** methodology and testing pyramid applied throughout the project.

---

## 1. Testing Pyramid

```
           / \
          /   \     E2E Tests (~10%)
         / E2E \    Espresso + UIAutomator
        /-------\   Critical user journeys
       /         \
      / Integrat. \ Integration Tests (~20%)
     /             \ Room DB, DataStore, Network Interceptors
    /---------------\
   /                 \ Unit Tests (~70%)
  /    Unit Tests     \ JUnit 5, MockK, Turbine (Coroutines & Flows)
 /_____________________\ UseCases, ViewModels, Parsers, Loggers
```

---

## 2. TDD Methodology (Red-Green-Refactor)

For every feature:
1. **Red**: Write a unit test in `src/test/` asserting expected behavior. Execute and verify failure.
2. **Green**: Implement the minimal code required to satisfy the test.
3. **Refactor**: Clean up and optimize while keeping all tests passing.

---

## 3. Coverage Targets by Layer

| Layer | Framework | Target Coverage |
| :--- | :--- | :--- |
| **Domain** (UseCases, Entities) | JUnit 5 + MockK | **> 90%** |
| **Data** (Room, Repositories, Network) | JUnit 5 + MockWebServer + Room In-Memory | **> 80%** |
| **Presentation** (ViewModels, MVI) | JUnit 5 + Turbine + Coroutines Test | **> 85%** |
| **UI** (Composables, Navigation) | Compose Test Rule | **> 70%** |

---

## 4. End-to-End (E2E) Test Scenarios

E2E tests validate complete integration on physical tablets or emulators:
1. **Onboarding & Setup**: Scholar theme configuration and token storage.
2. **Add Repository**: Connect to a remote repo and sync Markdown documents.
3. **Reading & Navigation**: TOC sidebar expansion, chapter navigation, and scroll progress restoration.
4. **Offline Mode**: Network disconnection verifying local cache reading.
5. **Annotations & Highlights**: Create a bookmark/note and assert persistence after app relaunch.
