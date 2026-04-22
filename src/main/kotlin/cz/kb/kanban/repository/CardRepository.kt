package cz.kb.kanban.repository

import cz.kb.kanban.model.Card
import cz.kb.kanban.model.Priority
import cz.kb.kanban.model.Status
import java.time.LocalDate

// `object` = singleton in Kotlin. Implements the generic Repository<Card, Long> contract from S2.
// Java/C# analogue: class with private constructor + static INSTANCE / static members.
object CardRepository : Repository<Card, Long> {

    // DISCUSS [S1 B1]: proc mutableListOf a ne listOf? (Kotlin rozlisuje read-only vs mutable)
    private val cards: MutableList<Card> = mutableListOf(
        Card(1, "Setup CI/CD pipeline",   Status.TODO,        Priority.CRITICAL, LocalDate.now().plusDays(2)),
        Card(2, "Code review guidelines", Status.IN_PROGRESS, Priority.HIGH,     LocalDate.now().plusDays(7)),
        Card(3, "Write unit tests",       Status.REVIEW,      Priority.MEDIUM,   LocalDate.now().minusDays(1)),
        Card(4, "Update README",          Status.DONE,        Priority.LOW,      null),
    )

    private var nextId: Long = 5L

    override fun findAll(): List<Card> = cards.toList()

    override fun findById(id: Long): Card? = cards.find { it.id == id }

    override fun save(entity: Card): Card {
        cards.removeIf { it.id == entity.id }
        cards.add(entity)
        return entity
    }

    override fun delete(id: Long): Boolean = cards.removeIf { it.id == id }

    // Domain helper beyond the generic contract — assigns the next id and defaults status to TODO.
    fun create(title: String, priority: Priority, dueDate: LocalDate? = null): Card =
        Card(
            id = nextId++,
            title = title,
            status = Status.TODO,
            priority = priority,
            dueDate = dueDate,
        ).also { cards.add(it) }

    fun clear() { cards.clear(); nextId = 1L }
}
