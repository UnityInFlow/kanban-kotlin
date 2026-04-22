# kanban-kotlin

Workshop projekt: **Kotlin pro Java a C# developery** — pure Kotlin CLI.
Pokryva **Session 1** (Kotlin zaklady) a **Session 2** (idiomatic refactor).
Na Spring + REST + FE pokracuje navazujici repo `kanban-spring`.

Workshop probiha pod hlavickou **Kotlin Server Squad**, Ceska sporitelna.

## Co budujes

Personal Kanban Board jako CLI aplikace. Karta ma `id`, `title`, `status`
(TODO/IN_PROGRESS/REVIEW/DONE), `priority` a optional `dueDate`. Prace
probiha pres TODO markery v kodu, po blocich podle session popisu.

## Pozadavky

- JDK 21+
- Maven 3.8+
- IntelliJ IDEA doporuceno (debugger + inline run)

## Spusteni

```bash
mvn compile
mvn exec:java                # spust CLI
mvn test                     # spust testy (pokud existuji)
```

Nebo v IntelliJ — spust `fun main()` v `src/main/kotlin/cz/kb/kanban/cli/Main.kt`.

## Struktura

```
src/main/kotlin/cz/kb/kanban/
├── cli/Main.kt                # vstupni bod
├── model/
│   ├── Card.kt                # data class
│   ├── Status.kt              # enum
│   ├── Priority.kt            # enum
│   └── CardResult.kt          # sealed interface (S2)
├── repository/CardRepository.kt   # object singleton
└── service/CardService.kt     # business logika

docs/
├── SESSION_1.md               # zadani S1 po blocich
├── SESSION_2.md               # zadani S2 po blocich
└── lektor/                    # lektorske poznamky + cheatsheet
```

## Jak pracovat s repozitarem

Kazda session ma **startovaci a hotovy stav** jako git tag. Mezi nimi je jeden commit per blok.

| Tag | Co obsahuje |
|-----|-------------|
| `session-1-start` | vychozi stav — vsechny S1 TODO jeste neresene |
| `session-1-done` ≡ `session-2-start` | vsechny S1 TODO vyreseny |
| `session-2-done` | S1 + S2 vyreseny, kompletne idiomatic Kotlin |

```bash
# Checkout konkretniho stavu
git checkout session-1-start       # zacatek S1
git checkout session-1-done        # pro porovnani / kdyz zasekl

# Zobrazit reseni jednoho souboru bez prepnuti
git show session-1-done -- src/main/kotlin/cz/kb/kanban/model/Card.kt

# Vypsat commity blok po bloku
git log session-1-start..session-1-done --oneline
```

## Uroven obtiznosti TODO markeru

V kodu najdes znacky `// TODO [SX BY — <tier>]:` kde tier je:

- **guided** — lektor to pisa se vsemi zaroven, nebojte se ptat
- **independent** — pracujes sam, zkus to prvni sam; kdyz zasekl, zeptej se
- **stretch** — nepovinne, pro rychle; kdyz nestihnes, neresi to

## Koncepty po blocich

| Blok | Klicove koncepty |
|------|-------------------|
| S1 B1 | `val`/`var`, `data class`, `enum class`, null safety `?`, string templates |
| S1 B2 | `when` expression, `copy()`, extension functions, `?.` a `?:` |
| S1 B3 | `filter`, `map`, `groupBy`, `sortedBy`, lambdy, `it` |
| S1 B4 | `object`, `companion object`, validace pres `when` |
| S2 B1 | `sealed interface`, exhaustive `when`, smart cast |
| S2 B2 | `let`, `apply`, `also`, `run`, `with` |
| S2 B3 | Generics, `reified`, `inline fun`, `Sequence` |
| S2 B4 | Higher-order functions, function references `::` |

## Licence

Workshop material — pouzij volne pro vyuku a samostudium.
