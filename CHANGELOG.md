# Changelog

All notable changes to the kanban-kotlin workshop repo. Format: [Keep a Changelog](https://keepachangelog.com).

## [Unreleased]

## [1.0.0] — 2026-04-23

Initial workshop release. Sessions 1 and 2 are ready to be run.

### Added
- Pure Kotlin CLI Kanban Board — Card domain, in-memory `object CardRepository`, `CardService`.
- Session 1 content (block commits between `session-1-start` and `session-1-done`):
  - `Priority.emoji()` + `Status.label()` via exhaustive `when`.
  - `Card.isOverdue()` + `Card.displayTitle()` extension functions.
  - `validateTransition` as `when` enforcing the state machine (TODO → IN_PROGRESS → REVIEW → DONE / REVIEW → IN_PROGRESS).
  - Collections: `filter`, `groupBy` across `CardService`; column-based board rendering in `Main.printBoard`.
  - `Card.Companion.newTodo` factory with `require(title.isNotBlank())` validation.
- Session 2 content (block commits between `session-2-start` and `session-2-done`):
  - `sealed interface CardResult` with `Success` / `NotFound` / `InvalidTransition` / `ValidationError`.
  - `CardService.moveCardSafe` returning `CardResult`; CLI switched from try/catch to exhaustive `when(CardResult)`.
  - Scope-function refactor using `?.let { card -> ... } ?: CardResult.NotFound` and `.also { auditLog(it) }`.
  - Generic `Repository<T, ID>` interface; `CardRepository` now implements it.
  - `util/Reified.kt` — `typeName<T>()` and `Iterable<*>.ofType<T>()` showcasing `inline fun <reified T>`.
  - `cli/BoardReport.kt` — higher-order `summarizeBy`, `asSequence` pipelines, `Card::isOverdue` function reference.
- Workshop docs per session (`docs/SESSION_{1,2}.md`) + instructor notes and cheatsheets (`docs/lektor/`).
- Harness: `CLAUDE.md`, `.claude/hooks` (pre-bash guard + post-tool compile check), `.claude/skills` (workshop-exercise, kotlin-for-java-csharp, session-prep).
- GitHub Actions CI — `./mvnw compile test package` on push to main and on `session-*` tags.
- `.editorconfig`, Dependabot config, issue templates, CONTRIBUTING.md.

### Fixed
- Cleaned up pre-solved TODOs in `session-1-start` so `CardService.getCardsByStatus`, `getBoardGrouped`, `validateTransition` and `Main.printBoard` actually start empty (participants implement them in blocks 2 and 3).
- `docs/SESSION_1.md` replaced the non-existent `session-1-blockN-start` recovery tag with the supported `session-1-done` checkout patterns.
- `docs/SESSION_2.md` dropped a phantom `allowed: Set<Status>` field from the `CardResult.InvalidTransition` data class (the field never existed in the real code); `session-2-end` tag rename to `session-2-done`.
