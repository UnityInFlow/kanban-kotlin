# Session 1 — Cheatsheet

1-pager, vytiskni a rozdej ucastnikum na zacatku.

## Basics

| Java / C# | Kotlin | Poznamka |
|---|---|---|
| `final String x = "..."` / `readonly string x` | `val x = "..."` | Imutable, type inferovan |
| `String x = "..."` / `string x` | `var x = "..."` | Mutable |
| `@Nullable String x` / `string? x` | `val x: String?` | Nullable typ |
| `String x` / `string x` | `val x: String` | Non-null |

## Null safety

```kotlin
val name: String? = getName()

name?.length                 // safe call, vrati Int? (null kdyz name null)
name?.length ?: 0            // elvis, default pri null
name ?: return               // early return
name ?: throw Error("...")   // throw pri null
name?.let { println(it) }    // blok se spusti jen kdyz non-null
// name!!.length             <-- NIKDY, je to runtime NPE
```

## Data class

```kotlin
data class Card(
    val id: Long,
    val title: String,
    val dueDate: LocalDate? = null  // optional s default
)

val c = Card(1, "Test")
val moved = c.copy(dueDate = LocalDate.now())   // immutable update
```

Generuje: `equals`, `hashCode`, `toString`, `copy`, `componentN` pro destructuring.

Java analog: `record Card(long id, String title, LocalDate dueDate) {}`
C# analog: `record class Card(long Id, string Title, LocalDate? DueDate);`

## Enum

```kotlin
enum class Status { TODO, IN_PROGRESS, REVIEW, DONE }

// muze mit metody a stav
enum class Priority(val label: String) {
    LOW("nizka"), HIGH("vysoka");

    fun isUrgent() = this == HIGH
}
```

## When

```kotlin
// Expression (vraci hodnotu)
val color = when (status) {
    Status.TODO        -> "grey"
    Status.IN_PROGRESS -> "blue"
    Status.REVIEW      -> "yellow"
    Status.DONE        -> "green"
}  // exhaustive — bez else, compiler overi

// Statement s podminkami
when {
    age < 18       -> deny()
    age in 18..65  -> allow()
    else           -> specialCase()
}
```

## Extension function

```kotlin
fun Card.isOverdue(): Boolean =
    dueDate?.isBefore(LocalDate.now()) == true

card.isOverdue()             // cte se jako metoda
```

Java analog: `CardUtils.isOverdue(card)` — jen syntax sugar.

## Collections

```kotlin
val active = cards.filter { it.status != Status.DONE }
val titles = cards.map { it.title }
val byStatus = cards.groupBy { it.status }           // Map<Status, List<Card>>
val sorted = cards.sortedByDescending { it.priority }

// Chain
cards.filter { it.isOverdue() }
     .sortedBy { it.dueDate }
     .map { it.title }
```

Java: `cards.stream().filter(...).map(...).collect(toList())` — eager na List.
C#: `cards.Where(...).Select(...).ToList()` — LINQ lazy na IEnumerable.

## Object singleton

```kotlin
object CardRepository {                  // standalone singleton
    private val cards = mutableListOf<Card>()
    fun all(): List<Card> = cards.toList()
}

CardRepository.all()

class Card(val id: Long) {
    companion object {                   // singleton uvnitr tridy
        fun createNew(title: String) = Card(nextId())
    }
}

Card.createNew("Foo")                    // bez `new`, pristup pres tridu
```

## Caste chyby a jak je cist

| Chyba | Proc | Jak opravit |
|---|---|---|
| `Val cannot be reassigned` | Prirazujete do `val` | `val` → `var`, NEBO `copy()` |
| `Only safe (?.) or non-null asserted calls allowed` | `.` na nullable typu | `?.` NEBO `?: default` |
| `when must be exhaustive` | Chybi case v `when` | Dopln case (lip) NEBO `else ->` |
| `Type mismatch: expected X, found X?` | Davate nullable kam se ceka non-null | `?:` default NEBO `!!` (neeee) |
| `None of the following candidates is applicable` | Spatne typy argumentu | Zkontroluj typy — Kotlin je strictnejsi |

## Zlata pravidla workshopu

1. `val` by default. `var` jen kdyz fakt musis.
2. Zadne `!!`. Nikdy. Ani jednou.
3. Kdyz compiler hlasi chybu — ctete ji. Kotlin je obvykle velmi jasny.
4. `data class` pro modely, `object` pro singletony, `class` pro vse ostatni.
5. `when` expression misto `if/else if` kdyz pracujes s enum / sealed.
