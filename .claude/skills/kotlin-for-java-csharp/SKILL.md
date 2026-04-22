---
name: kotlin-for-java-csharp
description: Use when explaining any Kotlin concept to a Java or C# developer — enforces the "analogue first, Kotlin second, why third" teaching template and provides the canonical parallels table.
---

# Kotlin for Java / C# Developers

Every explanation of a Kotlin concept must follow this three-beat structure:

1. **Analogue** — one snippet in Java *or* C# (pick the closest). If the concept has no clean analogue, say so explicitly.
2. **Kotlin** — the idiomatic form, minimal.
3. **Why** — one sentence on what Kotlin's version gives you that the analogue does not.

Keep each beat to ~5 lines of code max. If a participant needs more, hand them a workshop-docs pointer.

## Template

```
### <concept name>

**Java / C#:**
```<lang>
// minimal analogue
```

**Kotlin:**
```kotlin
// minimal idiomatic form
```

**Why:** <one sentence>.
```

## Canonical Parallels Table

Use the closest analogue; mention the other only if the participant asks.

| Kotlin | Java | C# | Notes |
|---|---|---|---|
| `data class` | `record` (Java 16+) / POJO+Lombok | `record class` (C# 9+) | Kotlin's also gets `copy()` with named args |
| `val` / `var` | `final` / non-final | `readonly` / mutable | `val` is the default in idiomatic Kotlin |
| `String?` (nullable) | `@Nullable String` | `string?` (NRT) | Compiler-enforced, no runtime NPE |
| `?.` safe call | manual `if (x != null)` | `?.` null-conditional | Chains, returns null on any null in chain |
| `?:` elvis | ternary `x != null ? x : y` | `??` null-coalescing | Also works with `?: throw ...` and `?: return` |
| `!!` non-null assert | unchecked | `!` null-forgiving | **Forbidden in workshop code** |
| `sealed interface` + `when` | `sealed interface` (Java 17) | abstract class hierarchy | Exhaustive `when` — compiler verifies |
| `enum class` | `enum` | `enum` | Kotlin's can hold state and methods cleanly |
| `object` | singleton pattern boilerplate | `static class` | True singleton, thread-safe by construction |
| `companion object` | `static` members | `static` members | Still a real object — can implement interfaces |
| Extension fn `fun X.foo()` | utility class `StringUtils.foo(x)` | extension method | No inheritance, resolved statically |
| Lambda `{ x -> ... }` | `x -> ...` | `x => ...` | Last-arg lambda can go outside parens |
| `filter/map/groupBy` | Streams `.filter/.map/.collect(...)` | LINQ `.Where/.Select/.GroupBy` | Eager on List; use `asSequence()` for lazy |
| `asSequence()` | `.stream()` | `IEnumerable` | Lazy chain, one pass |
| Scope fn `let` | `Optional.map` | `?.Invoke`-ish | Executes block if non-null, returns lambda result |
| Scope fn `apply` | builder pattern | object initializer `{ }` | Configures receiver, returns receiver |
| Scope fn `also` | tap / peek | `x.Also(...)` (not built-in) | Side effect, returns receiver |
| `when (x) { ... }` | `switch` expression (Java 14+) | `switch` expression (C# 8+) | Exhaustive; arbitrary predicates; returns a value |
| `?.let { ... }` | `Optional.ifPresent` | `if (x is not null) { ... }` | Idiomatic null handling |
| Named args + defaults | overloads | named + optional args | Kills the builder pattern in most cases |
| `inline` + `reified` | type erased | generic + `typeof(T)` | Keeps type info at call site |
| Coroutines | virtual threads / reactive | `async`/`await` + `Task` | Structured concurrency (not used in this workshop) |

## Common Gotchas to Surface

Bring these up proactively when the relevant code appears:

- `data class` + JPA: JPA needs a no-arg constructor, so `@Entity` uses `class` (not `data class`) with `var` properties. Add the `kotlin-jpa` compiler plugin.
- `class Foo` is `final` by default — Spring AOP proxies need `open`. The `kotlin-spring` plugin makes Spring-annotated classes open automatically.
- `val` vs Java `final`: `val` means "reassignment forbidden", it does **not** mean "deeply immutable".
- `when` without a subject is a ladder of `if/else if`, not a pattern match.
- `Unit` is Kotlin's `void`, but it's a real value — you can return it.
- `List<T>` in Kotlin is read-only (not immutable); `MutableList<T>` is the writable variant. Java `List` maps to either depending on platform type.

## Anti-Patterns to Call Out

If you see these in participant code, stop and explain the idiomatic form:

- `!!.` — replace with `?.let` / `?:` / early return.
- `if (x == null) throw ...` — replace with `x ?: throw ...`.
- Try/catch around business failures — replace with `sealed interface` result type.
- `Mockito` — this project uses MockK.
- Field injection with `lateinit var` — replace with constructor injection.

## When the Analogue Doesn't Exist

Examples: scope functions, top-level functions, destructuring in parameters, `inline` + `reified`.
Say so explicitly: "There's no clean Java/C# analogue — here's how Kotlin's version reads." Don't force a bad parallel.
