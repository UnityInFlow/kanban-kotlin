# Contributing

This is a workshop repo used for teaching Kotlin to Java and C# developers. We
welcome fixes, better analogies, and new stretch exercises.

## When to open an issue vs a PR

- **Open an issue** when you hit something confusing as a learner, or something
  that did not match what `docs/SESSION_N.md` said. See the issue templates
  in `.github/ISSUE_TEMPLATE/`.
- **Open a PR** when you have a concrete fix, a better Java/C# analogy for an
  existing concept, or a new stretch task on the back of an existing block.

## PR rules (important — this repo is tagged-milestone-driven)

The repo has three canonical tags that participants check out:

- `session-1-start` — vychozi TODO stav pro Session 1
- `session-1-done` ≡ `session-2-start` — S1 vyreseno
- `session-2-done` — S1 + S2 vyreseno

**Changes on `main` get folded into the next published release**, but these
tags are never force-moved once they are out. If your PR modifies code that is
visible at `session-1-start`, it changes what every future participant sees on
day one — discuss in the issue first.

## Style

- `val` by default, `var` only where genuine mutation requires it.
- No `!!` operators.
- Business failures as `sealed interface` variants, not exceptions.
- No comments that repeat what the code already says; only `// WHY:` comments for non-obvious intent.
- See `.editorconfig` for indent / line endings (LF, 4-space for Kotlin, 2-space for YAML and Markdown).

## Local setup

```bash
git clone git@github.com:UnityInFlow/kanban-kotlin.git
cd kanban-kotlin
./mvnw compile
./mvnw exec:java
```

JDK 21 or higher. Maven is provided via the wrapper — do not install it globally.

## Adding a new exercise

1. Decide the tier: `guided` (we code together), `independent` (you try alone, hints on ask), `stretch` (optional bonus).
2. Add a TODO marker in code with the exact format:
   ```kotlin
   // TODO [SX BY — guided|independent|stretch]: <one-line instruction>
   ```
3. Add the participant-facing prose to `docs/SESSION_N.md` under the right Block heading.
4. Implement the solution on a `session-N-solution` branch, PR into main, then re-tag.
