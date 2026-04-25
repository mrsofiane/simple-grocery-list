# Contributing to Grocery List

Thanks for considering a contribution! This is a small hobby app, so the bar is simple: keep it lightweight, offline-first, and ad/tracker-free.

## Reporting Bugs / Requesting Features

Open an issue at https://github.com/mrsofiane/SimpleGroceryList/issues and include:

- Android version and device (for bugs)
- Steps to reproduce, expected vs. actual behavior
- A screenshot or short screen recording if relevant

## Building Locally

```bash
git clone https://github.com/mrsofiane/SimpleGroceryList.git
cd SimpleGroceryList
./gradlew assembleDebug
```

You'll need JDK 11+ and the Android SDK (platform 36). Open the project in Android Studio if you'd rather use the IDE.

## Submitting a Pull Request

1. Fork the repo and create a topic branch from `main` (e.g. `feat/sort-by-category`)
2. Keep changes focused — one feature or fix per PR
3. Match the existing code style (Kotlin official style, idiomatic Compose)
4. If you change UI, attach before/after screenshots
5. Make sure `./gradlew assembleDebug` succeeds before opening the PR

Commit messages follow the pattern visible in `git log`:

- `feat: ...` — new feature
- `fix: ...` — bug fix
- `docs: ...` — README / docs only
- `chore: ...` — build, tooling, formatting

## License of Contributions

By submitting a contribution, you agree that your work will be licensed under the project's [GPL-3.0 License](LICENSE).
