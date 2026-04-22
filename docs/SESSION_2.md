# Session 2 — Idiomatic Kotlin (4 hodiny)

## Kontext pro Claude Code

Jsi pomocnik na workshopu "Kotlin pro Java a C# developery" — Session 2.
Ucastnici dokoncili Session 1 a maji fungujici CLI Kanban Board.
Nyni refaktorujeme kod aby vypadal jako skutecny Kotlin — ne Java s jinym syntaxem.

**Projekt:** `kanban-kotlin` — pokracovani z Session 1.
**Startovaci bod:** `git checkout session-2-start` (nebo pokracovani z S1).
**Cil session:** Pridani `sealed interface CardResult`, scope functions, genericky Repository.

### Co je uz hotove ze Session 1

- `data class Card`, `enum Status`, `enum Priority`
- `object CardRepository` s in-memory listem
- `CardService` s `moveCard()` ktera hazi exception
- `fun main()` ktera zobrazuje board

---

## Blok 1 (0:00–1:00) — sealed interface CardResult

### Motivace — ukazat problem PRED resenim

**Live coding: anti-pattern (5 minut)**

```kotlin
// Toto je soucasny kod v CardService.moveCard():
fun moveCard(cardId: Long, to: Status): Card {
    val card = repository.findById(cardId)
        ?: throw NoSuchElementException("Card $cardId not found")
    validateTransition(card.status, to)   // muze hodit IllegalStateException
    return repository.save(card.copy(status = to))
}

// Caller nevi co ocekavat:
try {
    val card = service.moveCard(99L, Status.DONE)
    // zpracuj kartu...
} catch (e: NoSuchElementException) {
    // ...
} catch (e: IllegalStateException) {
    // ...
}
// Problem: compiler ti nerekne ze musis osetrit oba pripady!
```

**Reseni: sealed interface (ukazat hotove, pak ucastnici pisi)**

### Ukoly pro ucastniky

**Guided (20 minut):**

```
Otevri model/CardResult.kt — je tam jen komentar s TODO.
Implementuj sealed interface:

sealed interface CardResult {
    data class Success(val card: Card) : CardResult
    data class NotFound(val id: Long) : CardResult
    data class InvalidTransition(
        val from: Status,
        val to: Status,
        val allowed: Set<Status>,
    ) : CardResult
    data class ValidationError(val message: String) : CardResult
}

Pak otevri CardService.kt a pridej novou metodu moveCardSafe():

fun moveCardSafe(cardId: Long, to: Status): CardResult {
    val card = repository.findById(cardId)
        ?: return CardResult.NotFound(cardId)

    val allowed = when (card.status) {
        Status.TODO        -> setOf(Status.IN_PROGRESS)
        Status.IN_PROGRESS -> setOf(Status.REVIEW)
        Status.REVIEW      -> setOf(Status.DONE, Status.IN_PROGRESS)
        Status.DONE        -> emptySet()
    }

    if (to !in allowed) {
        return CardResult.InvalidTransition(card.status, to, allowed)
    }

    val updated = repository.save(card.copy(status = to))
    return CardResult.Success(updated)
}
```

**Independent (15 minut):**

```
Aktualizuj cli/Main.kt — nahrad puvodni try/catch za when na CardResult:

val result = service.moveCardSafe(cardId, newStatus)
when (result) {
    is CardResult.Success ->
        println("✅ Karta presunuta: ${result.card.displayTitle()}")
    is CardResult.NotFound ->
        println("❌ Karta ${result.id} nenalezena")
    is CardResult.InvalidTransition ->
        println("⚠️ Nelze presunout: ${result.from} -> ${result.to}. Povolene: ${result.allowed}")
    is CardResult.ValidationError ->
        println("❌ Validace: ${result.message}")
}

Vsimni si:
- Kompilator vynuti ze osетris VSECHNY pripady (exhaustive when)
- Smart cast — uvnitr is CardResult.Success mas pristup k result.card bez castovania
- Kdyz pridame novy case do sealed interface, kompilator nas upozorni vsude kde chybi
```

**Stretch:**

```
Pridej do CardService metodu createCard() ktera vraci CardResult:
- CardResult.ValidationError kdyz je title prazdny nebo kratsi nez 3 znaky
- CardResult.ValidationError kdyz uz existuje karta se stejnym title
- CardResult.Success s novou kartou

fun createCardSafe(title: String, priority: Priority, dueDate: LocalDate? = null): CardResult
```

### Kotlin vs Java/C# srovnani

| Java | C# | Kotlin |
|---|---|---|
| Checked exceptions (throws) | Neexistuje | `sealed interface` — compiler enforced |
| `instanceof` + cast | `is` + cast | `is` + smart cast (automaticky) |
| Pattern matching (Java 21) | `switch` expression | `when` — cleaner syntax |
| `Optional<T>` | `T?` | `T?` — primo v type systemu |

---

## Blok 2 (1:00–2:00) — Scope functions

### Motivace

```kotlin
// Bez scope functions:
val card = repository.findById(id)
if (card != null) {
    logger.info("Processing card: $card")
    val updated = card.copy(status = to)
    repository.save(updated)
}

// Se scope functions — jeden expresivni chain:
repository.findById(id)
    ?.also { logger.info("Processing card: $it") }
    ?.copy(status = to)
    ?.let { repository.save(it) }
    ?: CardResult.NotFound(id)
```

### Rozhodovaci pravidlo (ukazat na tabuli/slajdu)

```
Potrebujes null check?          -> let
Konfigurujes objekt?            -> apply
Chces blok ktery vraci hodnotu? -> run
Chces side effect (log, debug)? -> also
Non-null, vice volani na nem?   -> with
```

### Ukoly pro ucastniky

**Live coding (15 minut) — lektor ukazuje, vsichni pisi:**

```kotlin
// let — null check + transform
val title = repository.findById(1L)?.let { it.title.uppercase() } ?: "NOT FOUND"

// also — side effect, vraci original
val card = repository.create("New task", Priority.HIGH)
    .also { println("Created: $it") }
    .also { /* mohli bychom poslat event, logovat, etc */ }

// apply — konfigurace, vraci receiver
// (pouzijeme v B3 pri buildovani request objektu)

// run — blok vraci vysledek
val summary = run {
    val all = repository.findAll()
    "Total: ${all.size}, Done: ${all.count { it.status == Status.DONE }}"
}
```

**Guided (20 minut):**

```
Otevri CardService.kt — refaktoruj moveCardSafe() pouzitim let a also:

fun moveCardSafe(cardId: Long, to: Status): CardResult =
    repository.findById(cardId)
        ?.also { logger.info("Moving card ${it.id} from ${it.status} to $to") }
        ?.let { card ->
            val allowed = allowedTransitions(card.status)
            if (to !in allowed) {
                CardResult.InvalidTransition(card.status, to, allowed)
            } else {
                CardResult.Success(repository.save(card.copy(status = to)))
            }
        }
        ?: CardResult.NotFound(cardId)

// Helper funkce:
private fun allowedTransitions(from: Status): Set<Status> = when (from) {
    Status.TODO        -> setOf(Status.IN_PROGRESS)
    Status.IN_PROGRESS -> setOf(Status.REVIEW)
    Status.REVIEW      -> setOf(Status.DONE, Status.IN_PROGRESS)
    Status.DONE        -> emptySet()
}

// Pridat simple logger:
private val logger = java.util.logging.Logger.getLogger(CardService::class.java.name)
```

**Independent (15 minut):**

```
Pouzij scope functions v Main.kt:

1. Pouzij also pro logovani kdyz se vytvori nova karta:
   service.createCard("Deploy hotfix", Priority.CRITICAL)
       .also { println("LOG: Karta vytvorena v ${LocalDateTime.now()}: $it") }

2. Pouzij let pro zobrazeni jen kdyz je karta overdue:
   service.getOverdueCards()
       .also { if (it.isEmpty()) println("Zadne overdue karty 🎉") }
       .forEach { card -> println("OVERDUE: ${card.displayTitle()}") }

3. Pouzij run pro sestaveni souhrnne zpravy:
   val zprava = run {
       val summary = service.getSummary()
       """
       Board Summary:
       Total: ${summary.total}
       Done: ${summary.byStatus[Status.DONE] ?: 0}
       Overdue: ${summary.overdue}
       """.trimIndent()
   }
   println(zprava)
```

**Stretch — anti-pattern ukazka:**

```
Toto je spatne — nikdy nezanoruj let do let:

// ❌ NEDELAT:
repository.findById(id)?.let { card ->
    card.dueDate?.let { date ->
        date.isBefore(LocalDate.now()).let { overdue ->
            if (overdue) println("overdue")
        }
    }
}

// ✅ Lepe:
val card = repository.findById(id) ?: return
val isOverdue = card.isOverdue()
if (isOverdue) println("overdue")
```

---

## Blok 3 (2:00–3:00) — Generics, reified, sequences

### Ukoly pro ucastniky

**Live coding (15 minut):**

```kotlin
// Genericky repository interface — proc?
// Chceme v budoucnu mit CardRepository, UserRepository, ProjectRepository
// Vsechny maji stejne zakladni operace

interface Repository<T, ID> {
    fun findById(id: ID): T?
    fun findAll(): List<T>
    fun save(entity: T): T
    fun delete(id: ID): Boolean
}

// CardRepository implementuje tento interface:
object CardRepository : Repository<Card, Long> {
    // ... existujici implementace
}
```

**Guided (20 minut):**

```
Vytvor souboru repository/Repository.kt:

interface Repository<T, ID> {
    fun findById(id: ID): T?
    fun findAll(): List<T>
    fun save(entity: T): T
    fun delete(id: ID): Boolean
    fun count(): Int = findAll().size   // default implementace!
}

Aktualizuj CardRepository aby implementoval Repository<Card, Long>.

Pak pridej reified extension funkci:

inline fun <reified T> Repository<T, *>.findAllOfType(): List<T> =
    findAll().filterIsInstance<T>()

// Proc reified? Bez nej nelze pouzit T za runtime (type erasure v JVM).
// Inline + reified "vykopiruje" typ do mista volani.
```

**Independent (15 minut):**

```
Aktualizuj filtrovani karet aby pouzivalo Sequence misto List kde to ma smysl:

// Proc Sequence?
// List: vsechny operace se provedou okamzite (eager)
// Sequence: operace se provedou az kdyz potrebujeme vysledek (lazy)
// Pro velke kolekce = usetri pamet a cas

// Porovnej:
// List verze — vytvori intermediate List po kazde operaci:
val result1 = repository.findAll()
    .filter { it.isOverdue() }
    .sortedBy { it.priority }
    .take(5)

// Sequence verze — lazy, jeden pruchod:
val result2 = repository.findAll()
    .asSequence()
    .filter { it.isOverdue() }
    .sortedBy { it.priority }
    .take(5)
    .toList()

Implementuj v CardService:
fun getTopOverdue(limit: Int = 5): List<Card> =
    repository.findAll()
        .asSequence()
        .filter { it.isOverdue() }
        .sortedByDescending { it.priority }
        .take(limit)
        .toList()
```

---

## Blok 4 (3:00–4:00) — Higher-order functions + finalni refactor

### Ukoly pro ucastniky

**Guided (10 minut):**

```
Pridej do CardService inline funkci s retry logikou:

inline fun <T> withRetry(times: Int = 3, block: () -> T): T {
    var lastException: Exception? = null
    repeat(times) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            lastException = e
            println("Pokus ${attempt + 1} selhal: ${e.message}")
        }
    }
    throw lastException ?: IllegalStateException("Vsechny pokusy selhaly")
}

// Pouziti:
val result = withRetry(3) {
    service.moveCardSafe(cardId, newStatus)
}
```

**Guided (15 minut):**

```
Pridej extension function na List<Card> pro hezky vypis boardu:

fun List<Card>.printBoard(title: String = "Kanban Board") {
    println("\n╔══════════════════════════════════╗")
    println("║  $title".padEnd(35) + "║")
    println("╚══════════════════════════════════╝")

    groupBy { it.status }.forEach { (status, cards) ->
        println("\n── ${status.label()} (${cards.size}) ────────────────")
        cards
            .sortedByDescending { it.priority }
            .forEach { println("  ${it.displayTitle()}") }
    }
}

// Pouziti — function reference misto lambdy:
service.getAllCards().printBoard("Muj Board")
```

**Independent (25 minut) — finalni milestone:**

```
Spust aplikaci: main() v cli/Main.kt

Overen ze funguje:
✅ Board se zobrazi grouped by Status
✅ Overdue karty jsou oznaceny
✅ moveCardSafe() vraci CardResult, ne exception
✅ Invalid prechod (napr. TODO -> DONE) vraci InvalidTransition
✅ Neexistujici karta vraci NotFound

Pokud neco nefunguje, pouzij: git checkout session-2-end
```

**Stretch — mini DSL:**

```kotlin
// Kotlin umoznuje vytvorit DSL (domain specific language)
// Pouziva "function with receiver"

fun card(block: CardBuilder.() -> Unit): Card {
    val builder = CardBuilder()
    builder.block()
    return builder.build()
}

class CardBuilder {
    var title: String = ""
    var priority: Priority = Priority.MEDIUM
    var dueDate: LocalDate? = null

    fun build() = Card(
        id = 0,
        title = title.ifBlank { error("title je povinny") },
        status = Status.TODO,
        priority = priority,
        dueDate = dueDate,
    )
}

// Pouziti:
val newCard = card {
    title = "Deploy to production"
    priority = Priority.CRITICAL
    dueDate = LocalDate.now().plusDays(1)
}
```

### ✅ Session 2 Milestone

```
Po Session 2 musi platit:

1. CardService.moveCardSafe() vraci sealed interface CardResult
2. Caller pouziva when(result) { is Success -> ... } bez try/catch  
3. CardRepository implementuje genericky Repository<Card, Long>
4. Service pouziva scope functions (let, also) — ne jen if/else
5. Kod vypada jako Kotlin, ne prepisana Java
```

### Tipy pro lektora

- **Sealed interface je hlavni "klik" teto session** — venuj mu cas
- C# devs: discriminated unions (F#) jsou presne to — ale v C# to chybi, Kotlin to ma
- Java devs: checked exceptions byly pokus o toto, ale selhaly — sealed interface je lepsi reseni
- **Scope functions**: neukazuj vsech 5 najednou — ukazuj vzdy kdyz je prirozene misto v kodu
- Blok 4 independent je dlouhy (25 min) — to je schvalne, lide si musi odnet neco cim jsou hrdí
