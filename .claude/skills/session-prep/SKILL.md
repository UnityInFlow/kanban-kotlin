---
name: session-prep
description: Use when preparing, running, or authoring a 4-hour workshop session — enforces the block timing structure, check-in cadence, and instructor-note format used across all three sessions.
---

# Workshop Session Skill

A workshop session is 4 hours. The three sessions are already outlined in
`workshop-docs/SESSION_{1,2,3}.md`. This skill defines the common structure you
apply when editing those docs, writing instructor notes, or timing a live session.

## Standard 4-Hour Shape

```
00:00 - 00:50   Block 1   (intro + concept + guided exercise)
00:50 - 01:00   Break
01:00 - 01:50   Block 2   (concept + guided -> independent)
01:50 - 02:00   Break
02:00 - 02:50   Block 3   (concept + independent -> stretch)
02:50 - 03:10   Extended break / buffer
03:10 - 04:00   Block 4   (integration / recap / Q&A / stretch overflow)
```

Four blocks, ~50 min each. 10 min break between blocks, 20 min extended break at the 2:50 mark. Keep total working time ≤ 3h20m — real workshops overrun.

## Per-Block Structure (10 / 25 / 10 / 5)

Within a 50-minute block, target these proportions:

1. **Concept intro — 10 min.** Use the `kotlin-for-java-csharp` skill template: Java/C# analogue → Kotlin → why.
2. **Guided live-coding — 25 min.** Instructor and participants type the `guided` TODOs together. Commit at the end of this phase so everyone has a known-good state.
3. **Independent work — 10 min.** Participants solve `independent` TODOs alone. Instructor walks the room / monitors chat.
4. **Debrief + check-in — 5 min.** Answer the two check-in questions (below), flag blockers for the break.

Stretch tasks are opt-in and live outside these 50 minutes — they fill Block 4 or go home as homework.

## Check-In Questions (End of Every Block)

Ask these two, no more:

1. "Did the concept click? Thumb up / sideways / down."
2. "Who is still on the `guided` TODO?" — if >30% of room, slow down the next block's pacing.

Record the answers in the session's `INSTRUCTOR_NOTES.md` (see below) so future instructors see where the material actually breaks.

## Authoring Format for SESSION_N.md

Each SESSION file follows this structure (the existing files already use it — match the pattern when editing):

```
# Session N — <title> (4 hodiny)

## Kontext pro Claude Code
[role, project, carryover from previous session]

## Blok 1 (0:00–1:00) — <subtitle>
### Co ucastnici budou delat
[one paragraph]

### Soubory relevantni pro tento blok
- `path/File.kt`

### Ukoly pro ucastniky
**Guided (vsichni pisi spolecne):**
1. ...

**Independent (samostatne):**
1. ...

**Stretch (pro rychle):**
1. ...

## Blok 2 ...
## Blok 3 ...
## Blok 4 ...

## Caste chyby a reseni
[lookup table: error message -> fix + WHY]
```

Keep participant-facing docs in Czech without diacritics. Keep code and comments in English.

## Instructor Notes — Separate File

For each session, maintain `workshop-docs/lektor/SESSION_N_LEKTOR.md` — **not** the participant-facing file. Contains:

- Wall-clock milestones to hit (not just block durations — actual minute marks)
- The two most common questions you expect in each block, pre-answered
- Exact Java/C# analogues to show at the whiteboard
- Which TODOs to skip if running late (prioritize `guided` > `independent` > `stretch`)
- Post-session observations log (add after each run)

## Pacing Adjustment Rules

During the session, use these heuristics:

- >30% of room stuck on a `guided` TODO after 15 min → stop, re-explain the concept with a second analogue, then resume. Lose the stretch task in this block, not a guided one.
- >50% finish `independent` in under 5 min → that TODO is too easy, note it for the post-session log.
- Any `LazyInitializationException`, `No primary constructor`, or classpath surprise → stop, write it on the board as a "today's error" so it becomes teaching content, not just friction.

## Handoff Between Sessions

- Session 2 starts from `session-1-done` — verify the tag matches SESSION_1 final state before Session 2 begins.
- Session 3 starts from `session-2-done`, ported to Spring Boot (see SESSION_3 intro for the `val -> var`, `object -> @Repository interface` transitions).
- If Session 1 overruns and only gets through Block 3, push the missing block to Session 2's Block 1 — don't silently skip it. Update INSTRUCTOR_NOTES.
