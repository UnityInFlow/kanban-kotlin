# Session 2 — Lektorske poznamky

4 hodiny, 4 bloky po ~50 min. Zacinas ze stavu `session-1-done` (≡ `session-2-start`).
Viz `docs/SESSION_2.md` pro ucastnicke zadani.

## Hlavni zprava Session 2

Session 1 ucila Kotlin syntax — ted refaktorujeme kod aby byl **idiomatic**.
Ucastnici budou mit pocit ze "to uz umim napsat v Kotlinu". Cil: ukazat ze
Kotlin neni Java s jinou syntaxi, ale ma vlastni patterns (sealed + when, scope fns, Sequence).

## Wall-clock milestones

| Cas | Kde byt |
|-----|---------|
| 0:15 | Motivace odpalena — "proc refaktorovat, kdyz kod funguje?" Ukazat anti-pattern (throw) |
| 0:45 | Blok 1 `guided` — `sealed interface CardResult` + `moveCardSafe()` hotove |
| 1:40 | Blok 2 `guided` — scope functions (`let` pro null check, `apply` pro builder) |
| 2:35 | Blok 3 `guided` — generic `Repository<T, ID>` + `reified` (ukazat bytecode) |
| 3:30 | Blok 4 `guided` — higher-order fns + Sequence |
| 3:55 | Q&A — ocekavej "kdy scope fn X vs Y" |

## Ocekavane otazky

### Blok 1 — sealed interface

- **"Proc misto `throw`?"** → Exceptions jsou pro _unexpected_ pripady (OOM, IO). Business "card neexistuje" NENI vyjimka — je to ocekavany vysledek. `sealed interface` ho dela explicitnim typu, compiler vynuti exhaustive handling.
- **"Proc nestaci `Result<T>`?"** → `Result` je generic success/failure. Tady mas RUZNE druhy failure (NotFound vs InvalidTransition vs ValidationError). Sealed dovoluje pattern matching s ruznymi daty per variant.
- **"Paralely v Java / C#?"** → Java 17 `sealed interface`, C# discriminated union (navrh jeste neni v jazyku, delaji se rucne). Rust `enum`, Scala ADT.
- **"Nevytvari to moc boilerplate?"** → Ne. Tri data classy s 1-2 poly, vsechny dohromady ~10 radku. Za to dostanes type safety.

### Blok 2 — scope functions

Toto je nejvic matouci blok. Pripravte si tento diagram:

```
| fn    | Receiver    | Return         | Hlavni use case               |
| let   | it          | lambda result  | null check `?.let { ... }`    |
| run   | this        | lambda result  | config + vraci hodnotu        |
| apply | this        | receiver       | builder pattern               |
| also  | it          | receiver       | side effect (log, debug)      |
| with  | this (arg)  | lambda result  | work with object (ne chain)   |
```

- **"Kdy `let` vs `apply`?"** → `let` kdyz potrebujes vysledek lambdy (mapping). `apply` kdyz konfigurujes objekt (side effect na receiver, ale vratit receiver).
- **"`run` vs `with`?"** → Prakticky stejny ucinek. `run` je extension fn, `with` je top-level. Volba stylu. V workshopu preferujeme `run` (konzistentni chain syntax).
- **"Co se vnorenym `let`?"** → Vyhnete se. Pokud potrebujete `a?.let { b?.let { ... } }`, refactor na early return nebo extract function. Radka nad 80 znaku = prilis vnorene.

### Blok 3 — generics + reified

- **"Proc `reified`?"** → Java generic type info se na runtime maze (type erasure). `inline fun` s `reified T` kopiruje kod na call-site, kde `T` jeste je znamy. Umozni `T::class` v body.
- **"Kdy `inline`?"** → Kdyz mas vysoke-order fn a chces aby se lambdy vlozily inline (vyssi perf, ale vetsi bytecode). Pro generic + `reified` MUSI byt `inline`.
- **"Ukazat kompilovany bytecode?"** → Ano, IntelliJ → Tools → Kotlin → Show Kotlin Bytecode → Decompile. Porovnej `inline fun` vs non-inline.

### Blok 4 — higher-order + Sequence

- **"Kdy `Sequence` vs `List`?"** → List = eager, kazdy krok vytvori novou kolekci. Sequence = lazy, krok-po-kroku per element. Pro velke datasety nebo short-circuit (`first`, `find`) vzdy Sequence.
- **"Paralela?"** → Java `Stream`, C# `IEnumerable` (LINQ). V Kotlinu List je eager by default (na rozdil od LINQ!).
- **"Function reference `::foo` vs lambda `{ foo(it) }`?"** → Reference je citelnejsi kdyz lambda jen propaguje arg. `{ it.toUpperCase() }` → `String::toUpperCase`. Ale pokud chces pridat logiku, lambda.

## Live-coding anti-pattern — Blok 1 start (5 min)

Nez zacnes sealed interface, ukaz tento kod:

```kotlin
// Starting state — S1 code s throws
fun moveCard(id: Long, to: Status): Card {
    val card = repo.find(id) ?: throw IllegalArgumentException("Card $id not found")
    if (!canTransition(card.status, to)) {
        throw IllegalStateException("Cannot move ${card.status} -> $to")
    }
    return repo.save(card.copy(status = to))
}

// CLI
try {
    moveCard(1, Status.DONE)
} catch (e: IllegalArgumentException) { ... }
catch (e: IllegalStateException) { ... }
```

Otazky do auditoria:
1. "Co kdyz zapomenu na `catch`?" (runtime crash)
2. "Jak zjistim co vsechno moze selhat?" (musim cist interni kod metody)
3. "Jak kompiler me ochrani?" (neochrani, exceptions nejsou v signature)

Potom ukaz `CardResult` reseni — kompiler vynuti exhaustive `when`, typy v signature. Ucastnici uvidi PROC refaktorujeme.

## Skip-if-late priorities

1. **Prvni preskocit:** generic Repository v Bloku 3 (stretch). Staci ukazat teorii.
2. **Druhe preskocit:** `with`/`run` v Bloku 2. Ucastnici zvladnou `let`/`apply`/`also`.
3. **Treti preskocit:** Sequence+benchmarks v Bloku 4. Vystaci `asSequence()` teorie.
4. **NIKDY nepreskakuj:** `sealed interface CardResult` v Bloku 1 — toto je vic nez pulka hodnoty session.

## Post-run log

```
Datum: YYYY-MM-DD
Pocet ucastniku:
Dokoncene bloky: / 4
Scope fn nejcasteji pletene: let/apply/run/also/with? 
"Aha moment" na sealed interface: ano / slabe / ne
Nejcastejsi chyba: <...>
Co upravit priste: <...>
```
