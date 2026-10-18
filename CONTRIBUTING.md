# Contributing to MD GitHub Navigator

## Branch Strategy (Git Flow)

- `main` — Production releases only
- `develop` — Integration branch for features
- `feature/*` — New features (branch from `develop`)
- `bugfix/*` — Bug fixes (branch from `develop`)
- `release/*` — Release preparation (branch from `develop`)
- `hotfix/*` — Critical production fixes (branch from `main`)

## Commit Convention

We follow [Conventional Commits v1.0.0](https://www.conventionalcommits.org/en/v1.0.0/).

### Format

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### Types

| Type | Description |
|------|-------------|
| `feat` | A new feature |
| `fix` | A bug fix |
| `docs` | Documentation only changes |
| `style` | Code style (formatting, semicolons, etc.) |
| `refactor` | Code change that neither fixes a bug nor adds a feature |
| `perf` | Performance improvement |
| `test` | Adding or correcting tests |
| `build` | Build system or external dependency changes |
| `ci` | CI configuration changes |
| `chore` | Other changes that don't modify src or test files |
| `revert` | Reverts a previous commit |

### Examples

```
feat(reader): add text-to-speech support
fix(github-api): handle rate limiting with exponential backoff
docs(readme): update installation instructions
test(annotations): add unit tests for highlight persistence
```

## Code Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use [ktlint](https://pinterest.github.io/ktlint/) for formatting
- All public APIs must have KDoc documentation

## Testing Requirements

- **TDD approach**: Write tests before implementation
- **Unit tests**: For all business logic and ViewModels
- **Integration tests**: For repository and data layer
- **E2E tests**: For critical user flows
- Minimum coverage target: 80%

## Pull Request Process

1. Create a feature branch from `develop`
2. Write tests first (TDD)
3. Implement the feature
4. Ensure all tests pass
5. Update documentation if needed
6. Submit PR to `develop`
