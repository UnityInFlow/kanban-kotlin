package cz.kb.kanban.service

import cz.kb.kanban.model.Card
import cz.kb.kanban.model.Priority
import cz.kb.kanban.model.Status
import cz.kb.kanban.model.isOverdue
import cz.kb.kanban.repository.CardRepository
import java.time.LocalDate

// SESSION 1 — BLOK 2 + 3
// Tady implementujeme logiku nad CardRepository

class CardService(
    private val repository: CardRepository = CardRepository,
) {

    fun getAllCards(): List<Card> = repository.findAll()

    fun getCardsByStatus(status: Status): List<Card> =
        repository.findAll().filter { it.status == status }

    fun getOverdueCards(): List<Card> =
        repository.findAll().filter { it.isOverdue() }

    fun getBoardGrouped(): Map<Status, List<Card>> =
        repository.findAll().groupBy { it.status }

    fun createCard(title: String, priority: Priority, dueDate: LocalDate? = null): Card {
        // Validation lives in the Card.newTodo factory (companion object) — single source of truth.
        val draft = Card.newTodo(title, priority, dueDate)
        return repository.create(draft.title, draft.priority, draft.dueDate)
    }

    // SESSION 1 VERZE — jednoduchá, hazi exception
    // V S2 B1 tento kod nahradime za CardResult
    fun moveCard(cardId: Long, to: Status): Card {
        val card = repository.findById(cardId)
            ?: throw NoSuchElementException("Card $cardId not found")

        validateTransition(card.status, to)

        val updated = card.copy(status = to)
        return repository.save(updated)
    }

    fun deleteCard(id: Long): Boolean = repository.delete(id)

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

    // ─────────────────────────────────────────────────
    // SESSION 2 — BLOK 1: nahradit moveCard() za verzi
    // ktera vraci CardResult misto throw exception
    // ─────────────────────────────────────────────────

    // TODO [S2 B1 — guided]: implementovat moveCardSafe() vracejici CardResult
    // fun moveCardSafe(cardId: Long, to: Status): CardResult { ... }

    // TODO [S2 B2 — independent]: pouzit let pro null check misto elvis throw
    // repository.findById(cardId)?.let { ... } ?: CardResult.NotFound(cardId)
}
