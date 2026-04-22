# Session 1 — Kotlin základy (4 hodiny)

## Kontext pro Claude Code

Jsi pomocnik na workshopu "Kotlin pro Java a C# developery".
Ucastnici maji zkusenosti s Javou nebo C# ale Kotlin neznaji.
Pomahej jim resit TODO ukoly v projektu `kanban-kotlin`.
Vysvetluj koncepty v kontextu Javy nebo C# — vzdy ukazuj paralelu.

**Projekt:** `kanban-kotlin` — pure Kotlin CLI aplikace, zadny framework.
**Cilova aplikace:** Personal Kanban Board (add/list/move/delete karet).
**Spusteni:** `fun main()` v `cz.kb.kanban.cli.Main.kt`

---

## Blok 1 (0:00–1:00) — Datovy model

### Co ucastnici budou delat

Prozkoumaji hotovy datovy model a doplni prvni TODO ukoly.

### Soubory relevantni pro tento blok

- `model/Card.kt` — data class s nullable polem
- `model/Status.kt` — enum class
- `model/Priority.kt` — enum class

### Ukoly pro ucastniky

**Guided (vsichni pisi spolecne):**

```
Otevri model/Priority.kt a dopln metodu emoji() ktera vraci emoji pro kazdou prioritu.
Pouzij when expression (ne if/else).

fun Priority.emoji(): String = when(this) {
    Priority.LOW      -> "🟢"
    Priority.MEDIUM   -> "🟡"
    Priority.HIGH     -> "🟠"
    Priority.CRITICAL -> "🔴"
}
```

**Independent (samostatne, 12 minut):**

```
1. Otevri model/Card.kt
2. Zkus vytvorit Card s null dueDate — projde kompilace?
3. Zkus pridat val name: String bez default hodnoty — co rika kompilator?
4. Zkus: val card2 = card.copy(status = Status.IN_PROGRESS)
   Vypis card a card2 — jsou to stejne objekty? Proc?
5. Zkus card == card2 — co vraci a proc?
```

**Stretch:**

```
Pridej do enum class Status metodu label() ktera vraci hezky text:
  TODO -> "To Do"
  IN_PROGRESS -> "In Progress"
  REVIEW -> "In Review"
  DONE -> "Done ✅"
```

### Caste chyby a jak pomoci

- **"Nemohu dat null do val dueDate: LocalDate"** — spravne, musi byt `LocalDate?`
- **"copy() nefunguje"** — jen na `data class`, ne na obycejnou class
- **"when nema else"** — enum when je exhaustive, else neni potreba — to je vyhoda!

### Kotlin vs Java/C# srovnani pro vysvetleni

| Java | C# | Kotlin |
|---|---|---|
| `class Task { ... }` + Lombok `@Data` | `record Task(...)` | `data class Card(...)` |
| `String name = null` (runtime NPE) | `string? name = null` | `val name: String?` (kompilator) |
| `task.getTitle()` | `task.Title` | `card.title` |
| switch statement | switch expression (C# 8+) | `when` — vzdy expression |

---

## Blok 2 (1:00–2:00) — Funkce, when, extension functions

### Co ucastnici budou delat

Implementuji logiku pro zobrazeni a presun karet.

### Soubory relevantni pro tento blok

- `model/Card.kt` — doplnit extension functions
- `service/CardService.kt` — `moveCard()` a `validateTransition()`

### Ukoly pro ucastniky

**Guided (15 minut live coding):**

```
Do souboru model/Card.kt (za data class) doplnime extension function:

fun Card.isOverdue(): Boolean =
    dueDate?.isBefore(LocalDate.now()) ?: false

Vysvetleni:
- dueDate? — nullable, mozna null
- ?.isBefore() — bezpecne volani jen kdyz neni null
- ?: false — kdyz je null, vrat false
```

**Guided (20 minut):**

```
Otevri service/CardService.kt — najdi metodu validateTransition().
Je tam TODO. Implementuj ji pres when expression:

private fun validateTransition(from: Status, to: Status) {
    val allowed = when (from) {
        Status.TODO        -> setOf(Status.IN_PROGRESS)
        Status.IN_PROGRESS -> setOf(Status.REVIEW)
        Status.REVIEW      -> setOf(Status.DONE, Status.IN_PROGRESS)
        Status.DONE        -> emptySet()
    }
    if (to !in allowed) {
        throw IllegalStateException("Invalid transition: $from -> $to")
    }
}

Otazky k zamysleni:
- Co se stane kdyz pridam novy Status do enum a zapomenu ho pridat do when?
- Proc pouzivame setOf() a ne listOf()?
```

**Independent (15 minut):**

```
Dopln extension function displayTitle() na Card:
- Format: "[CRITICAL] Setup CI/CD 🔴 ⚠️OVERDUE"
- Pouzij Priority.emoji() z Bloku 1
- Pouzij card.isOverdue() pro OVERDUE flag
- Pouzij string templates

Pak ji pouzij v cli/Main.kt misto soucasneho vystupu.
```

**Stretch:**

```
Pridej extension function na List<Card>:

fun List<Card>.countByStatus(): String =
    Status.entries.joinToString(" | ") { status ->
        "${status.label()}: ${count { it.status == status }}"
    }

Kde ji pouzit? V cli/Main.kt pri vypisu boardu.
```

### Caste chyby a jak pomoci

- **"?. nefunguje na non-nullable"** — spravne, `?.` jen na nullable typ
- **"when nema return"** — when jako expression automaticky vraci hodnotu posledniho vyrazu
- **"Extension function nevidim"** — musi byt ve stejnem package nebo importovana

---

## Blok 3 (2:00–3:00) — Collections a lambdy

### Co ucastnici budou delat

Implementuji board display a filtrovani pres Kotlin collections API.

### Soubory relevantni pro tento blok

- `service/CardService.kt` — `getOverdueCards()`, `getBoardGrouped()`
- `cli/Main.kt` — `printBoard()`

### Ukoly pro ucastniky

**Live coding (15 minut) — lector ukazuje na projektoru:**

```kotlin
// Otevreni IntelliJ REPL nebo scratch file — zkousime collections interaktivne:
val cards = listOf(
    Card(1, "Task A", Status.TODO,        Priority.HIGH,   null),
    Card(2, "Task B", Status.IN_PROGRESS, Priority.LOW,    null),
    Card(3, "Task C", Status.TODO,        Priority.MEDIUM, null),
)

cards.filter { it.status == Status.TODO }         // 2 karty
cards.map { it.title }                            // listOf("Task A", "Task B", "Task C")
cards.sortedBy { it.priority }                    // serazene LOW->HIGH
cards.groupBy { it.status }                       // Map<Status, List<Card>>
cards.count { it.status == Status.TODO }          // 2
cards.firstOrNull { it.id == 99L }               // null (ne exception!)
```

**Guided (20 minut):**

```
Otevri service/CardService.kt — implementuj getOverdueCards():

fun getOverdueCards(): List<Card> =
    repository.findAll().filter { it.isOverdue() }

Pak implementuj getBoardGrouped() — uz je tam, ale zkontroluj ze chápeš jak funguje:

fun getBoardGrouped(): Map<Status, List<Card>> =
    repository.findAll().groupBy { it.status }
```

**Independent (15 minut):**

```
Otevri cli/Main.kt — implementuj printBoard() funkci:

fun printBoard(service: CardService) {
    val board = service.getBoardGrouped()

    Status.entries.forEach { status ->
        val cards = board[status] ?: emptyList()
        println("── ${status.label()} (${cards.size}) ──────────────────")
        cards.forEach { card ->
            println("  ${card.displayTitle()}")
        }
        println()
    }
}

Bonus: pridej na konec radu s celkovym poctem karet a poctem overdue.
```

**Stretch:**

```
Pridej do CardService metodu getSummary() ktera vraci:

data class BoardSummary(
    val total: Int,
    val byStatus: Map<Status, Int>,
    val overdue: Int,
    val completionRate: Double,
)

fun getSummary(): BoardSummary {
    val all = repository.findAll()
    return BoardSummary(
        total = all.size,
        byStatus = all.groupBy { it.status }.mapValues { it.value.size },
        overdue = all.count { it.isOverdue() },
        completionRate = if (all.isEmpty()) 0.0
                         else all.count { it.status == Status.DONE }.toDouble() / all.size,
    )
}
```

### Caste chyby a jak pomoci

- **"filter vraci List, ne MutableList"** — spravne, immutable by default
- **"groupBy vraci Map<Status, List<Card>> ale ja chci Map<String, ...>"** — `.groupBy { it.status.name }` pro String klice
- **"firstOrNull vs first"** — `first()` hodi NoSuchElementException, `firstOrNull()` vraci null

### ✅ Milestone Bloku 3

Po tomto bloku musi fungovat: `fun main()` vypise board grouped by status, barevne priority, overdue flag.

---

## Blok 4 (3:00–4:00) — OOP, object singleton, interface

### Co ucastnici budou delat

Refaktoruji CardRepository na `object` singleton, pridaji `BoardFilter` interface.

### Soubory relevantni pro tento blok

- `repository/CardRepository.kt` — je uz jako object, diskuse proc
- Novy soubor: `repository/BoardFilter.kt`

### Ukoly pro ucastniky

**Guided (15 minut):**

```
Vytvor novy soubor repository/BoardFilter.kt:

interface BoardFilter {
    fun filter(cards: List<Card>): List<Card>
}

class StatusFilter(private val status: Status) : BoardFilter {
    override fun filter(cards: List<Card>): List<Card> =
        cards.filter { it.status == status }
}

class PriorityFilter(private val priority: Priority) : BoardFilter {
    override fun filter(cards: List<Card>): List<Card> =
        cards.filter { it.priority == priority }
}

// TODO: pridej OverdueFilter
```

**Independent (25 minut):**

```
1. Implementuj OverdueFilter : BoardFilter

2. Pridej do CardService metodu:
   fun getFiltered(filter: BoardFilter): List<Card> =
       filter.filter(repository.findAll())

3. Pouzij v Main.kt:
   val highPriority = service.getFiltered(PriorityFilter(Priority.HIGH))
   println("High priority karty: ${highPriority.size}")

4. STRETCH: Vytvor CompositeFilter ktery kombinuje vice filtru:
   class CompositeFilter(private val filters: List<BoardFilter>) : BoardFilter {
       override fun filter(cards: List<Card>): List<Card> =
           filters.fold(cards) { acc, f -> f.filter(acc) }
   }
   // Pouziti: CompositeFilter(listOf(StatusFilter(TODO), PriorityFilter(HIGH)))
```

### Preview na Session 2

```
Pozor na open class — zkuste pridat:

open class BaseService

class CardService : BaseService()

Proc potrebujeme 'open'? V Session 3 uvidime jak to Spring resi automaticky
pres kotlin-spring plugin — bez 'open' by Spring nemohl vytvaret proxy.
```

---

## Tipy pro lektora

- **Cas na blok je 60 minut — drz ho.**
- Kdyz nekdo uvazne: checkout `git tag session-1-blockN-start`
- C# devs: zduraznuj ze `?.` je jejich `?.` a `?:` je jejich `??`
- Java devs: zduraznuj ze `.filter { }` nepotrebuje `.stream()` a `.collect()`
- Nikdy nerikej "to je jednoduche" — pro nekoho to jednoduche neni
