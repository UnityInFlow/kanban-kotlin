package cz.kb.kanban.repository

import cz.kb.kanban.model.Card
import cz.kb.kanban.model.Priority
import cz.kb.kanban.model.Status
import java.time.LocalDate

// SESSION 1 — BLOK 4
// object = singleton v Kotlinu
// Porovnej: Java static class / C# static class
object CardRepository {

    // DISCUSS [S1 B1]: proc mutableListOf a ne listOf? (Kotlin rozlisuje read-only vs mutable)
    private val cards: MutableList<Card> = mutableListOf(
        Card(1, "Setup CI/CD pipeline",   Status.TODO,        Priority.CRITICAL, LocalDate.now().plusDays(2)),
        Card(2, "Code review guidelines", Status.IN_PROGRESS, Priority.HIGH,     LocalDate.now().plusDays(7)),
        Card(3, "Write unit tests",       Status.REVIEW,      Priority.MEDIUM,   LocalDate.now().minusDays(1)),
        Card(4, "Update README",          Status.DONE,        Priority.LOW,      null),
    )

    private var nextId: Long = 5L

    fun findAll(): List<Card> = cards.toList()

    fun findById(id: Long): Card? = cards.find { it.id == id }

    fun save(card: Card): Card {
        cards.removeIf { it.id == card.id }
        cards.add(card)
        return card
    }

    fun create(title: String, priority: Priority, dueDate: LocalDate? = null): Card {
        val card = Card(
            id = nextId++,
            title = title,
            status = Status.TODO,
            priority = priority,
            dueDate = dueDate,
        )
        cards.add(card)
        return card
    }

    fun delete(id: Long): Boolean = cards.removeIf { it.id == id }

    fun clear() { cards.clear(); nextId = 1L }
}
