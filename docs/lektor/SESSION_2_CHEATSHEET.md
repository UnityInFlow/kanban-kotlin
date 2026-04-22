# Session 2 — Cheatsheet

## Sealed interface (misto exceptions pro business)

```kotlin
sealed interface CardResult {
    data class Success(val card: Card) : CardResult
    data object NotFound : CardResult
    data class InvalidTransition(val from: Status, val to: Status) : CardResult
    data class ValidationError(val field: String, val reason: String) : CardResult
}

fun moveCardSafe(id: Long, to: Status): CardResult {
    val card = repo.find(id) ?: return CardResult.NotFound
    if (!canTransition(card.status, to)) {
        return CardResult.InvalidTransition(card.status, to)
    }
    return CardResult.Success(repo.save(card.copy(status = to)))
}

// Volani — exhaustive, kompiler overi pokryti
when (val result = moveCardSafe(1, Status.DONE)) {
    is CardResult.Success           -> println("Moved: ${result.card}")
    CardResult.NotFound             -> println("Not found")
    is CardResult.InvalidTransition -> println("Cannot ${result.from} -> ${result.to}")
    is CardResult.ValidationError   -> println("Invalid ${result.field}: ${result.reason}")
}
```

## Scope functions — kdy kterou

```
            Receiver       Returns          Idiom
let         it             lambda result    null check + transform
run         this           lambda result    config + compute
apply       this           receiver         builder
also        it             receiver         log / debug side effect
with        this (arg)     lambda result    ne-chain work
```

### Priklady

```kotlin
// let — null safety
val upper = name?.let { it.uppercase() }

// apply — builder
val card = Card(1, "").apply {
    // zde this = Card
    // ale Card je data class — nelze mutovat, takze tenhle priklad je spis...
}
val sb = StringBuilder().apply {
    append("Hello")
    append(" World")
}

// also — log pri pruchodu
val c = createCard().also { log.info("Created: $it") }

// run — init + compute
val config = File("config.yaml").run {
    readText().parseYaml()
}

// with — bez chain
with(response) {
    println(status)
    println(body)
}
```

## Generics + reified

```kotlin
interface Repository<T, ID> {
    fun find(id: ID): T?
    fun save(entity: T): T
    fun all(): List<T>
}

// reified — T znamy za runtime
inline fun <reified T> parseJson(json: String): T {
    return mapper.readValue(json, T::class.java)
}

val card: Card = parseJson(raw)  // T = Card inferovan
```

Pozor: `reified` MUSI byt v `inline fun`. Bez `inline` je T erased.

## Sequence (lazy eval)

```kotlin
// Eager — kazdy krok novy List
val result = (1..1_000_000)
    .filter { it % 2 == 0 }    // vytvori List 500K
    .map { it * 2 }            // vytvori List 500K
    .first()                   // vzala jsme jeden, ale vytvorili 2x500K

// Lazy — stream-like
val result = (1..1_000_000).asSequence()
    .filter { it % 2 == 0 }
    .map { it * 2 }
    .first()                   // zastavi po prvnim, zadne mezikoleku
```

Sequence kdyz: velka data, pipeline, short-circuit (first/find/any/take).
List kdyz: male kolekce, potrebujes vysledek cely (size, random access).

## Higher-order functions + references

```kotlin
fun processCards(cards: List<Card>, filter: (Card) -> Boolean): List<Card> =
    cards.filter(filter)

processCards(all) { it.isOverdue() }
processCards(all, Card::isOverdue)      // function reference

// Metody
fun Card.displayTitle() = "[$priority] $title"
cards.map(Card::displayTitle)           // ekvivalent { it.displayTitle() }
```

## Anti-patterns — okamzite chytnout

| Vidis v kodu | Fix |
|---|---|
| `!!.` | `?.let { ... }` / `?: throw` / `?: return` |
| `if (x == null) throw ...` | `x ?: throw ...` |
| `try/catch` kolem business logiky | `sealed interface` result |
| `throw IllegalStateException` pro expectable case | `CardResult.X` variant |
| `list.filter {...}.first()` nad 10k items | `.asSequence().filter{...}.first()` |
| Retezec `let { let { let { ... } } }` | Early return nebo extract function |

## Zlata pravidla workshopu (pokracovani)

6. Business failure = `sealed interface`. Throw jen pro unexpected (OOM, corrupted state).
7. Scope function podle: _"co potrebuju vratit?"_ — receiver nebo lambda result?
8. `inline` a `reified` vyzaduji pozornost — zbytecne inlinovani = vetsi bytecode.
9. `Sequence` pro pipeline nad velkymi daty. `List` pro strict vysledky.
10. Function reference `::` kdyz lambda jen propaguje — zvysuje citelnost.
