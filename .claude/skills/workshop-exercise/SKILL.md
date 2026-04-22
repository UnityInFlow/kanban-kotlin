---
name: workshop-exercise
description: Use when adding or editing a workshop exercise — defines the TODO-marker convention, difficulty tiers, and the git tag/branch workflow that holds the canonical solutions.
---

# Workshop Exercise Skill

You are editing or creating a teaching exercise in `kanban-kotlin/` or `kanban-spring/`.
The curriculum lives in `workshop-docs/SESSION_{1,2,3}.md`. This skill defines how exercise
code is marked, how difficulty tiers behave, and how solutions are stored.

## TODO Marker Convention

Every exercise slot in source code uses one of these comment forms:

```kotlin
// TODO [S1 B2 — guided]: <one-line instruction>
// TODO [S1 B3 — independent]: <one-line instruction>
// TODO [S2 B1 — stretch]: <one-line instruction>
```

- `S1 B2` = Session 1, Block 2. Blocks are defined in the SESSION_N.md files.
- Tier controls how the assistant responds when a participant asks for help (see below).
- Keep the instruction to one line. Longer context belongs in SESSION_N.md, not the comment.

## Difficulty Tiers — How to Respond

| Tier | Participant asks "how do I do this?" |
|---|---|
| `guided` | Walk through the solution step-by-step. This is live-coded together. |
| `independent` | Give hints, not the answer. Ask what they have tried. Reveal the solution only after a genuine attempt or after the participant explicitly asks to see it. |
| `stretch` | Optional. Only attempt when participant has finished the required tiers. Give a pointer to the concept, not the full code. |

Never spoil an `independent` or `stretch` task unprompted, even if fixing an adjacent bug.

## Solution Storage — Git Tags

Canonical solutions live in git tags. Never overwrite them.

```
session-1-start              <- starting state handed to participants
session-1-block1-done        <- blocks N-done accumulate
session-1-block2-done
session-1-done               <- all blocks of session 1 solved
session-2-start              <- same as session-1-done, for continuity
...
```

**Rules:**
- When you add a new exercise, leave the `// TODO` marker in place on the `session-N-start` tag and provide the solution on `session-N-blockM-done`.
- When writing a solution, keep the original TODO comment line deleted (not commented out). Participants already know from SESSION_N.md what the block was about.
- Never rewrite or force-push published tags. If a tag is wrong, create `session-N-done-v2` and note the replacement in CHANGELOG.
- Before `git checkout <tag>`, verify working tree is clean. If not, stash and tell the user the stash name.

## Checklist When Adding a New Exercise

- [ ] Decide tier: `guided` / `independent` / `stretch`
- [ ] Add TODO marker in code with `[SX BY — tier]` prefix
- [ ] Write the participant-facing instruction block in `workshop-docs/SESSION_N.md` under the right Block heading
- [ ] Implement the solution in a new commit on a working branch
- [ ] Tag: `git tag session-N-blockM-done` (and `session-N-done` if last block)
- [ ] Update `session-(N+1)-start` to match `session-N-done`
- [ ] Note in CHANGELOG.md: which exercise was added, which tag carries the solution

## Checklist When Editing an Existing Exercise

- [ ] Check which tag range the exercise appears in
- [ ] Make the change on the earliest affected `-start` tag's content
- [ ] Cascade the change forward through every `-blockM-done` and `-done` tag that follows
- [ ] Do not break the monotonic property: `session-N-done` must equal `session-(N+1)-start`

## Output Format for Solution Reveals

When revealing a solution to a participant, use this structure:

```
<one-line summary of what the solution does>

<code block>

<why this is idiomatic Kotlin — one sentence, linking to the Java/C# analogue>
```
