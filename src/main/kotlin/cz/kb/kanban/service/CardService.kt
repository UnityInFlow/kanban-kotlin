package cz.kb.kanban.service

import cz.kb.kanban.model.Card
import cz.kb.kanban.model.CardResult
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

    // Business-safe variant of moveCard — returns every possible outcome as a sealed-interface
    // value instead of throwing. Caller must exhaustively match via `when`.
    //
    // Scope-function usage:
    //   - `?.let { card -> ... } ?: CardResult.NotFound` — null check + transform in one expression.
    //   - `.also { auditLog(it) }` — side effect without breaking the chain; returns the receiver.
    fun moveCardSafe(cardId: Long, to: Status): CardResult =
        repository.findById(cardId)?.let { card ->
            if (canTransition(card.status, to)) {
                repository.save(card.copy(status = to))
                    .also { auditLog("moved card ${it.id}: ${card.status} -> ${to}") }
                    .let(CardResult::Success)
            } else {
                CardResult.InvalidTransition(card.status, to)
            }
        } ?: CardResult.NotFound(cardId)

    private fun auditLog(message: String) {
        // Minimal stand-in for a real logger — teaches `also` without dragging in slf4j.
        println("[audit] $message")
    }

    private fun canTransition(from: Status, to: Status): Boolean =
        to in when (from) {
            Status.TODO        -> setOf(Status.IN_PROGRESS)
            Status.IN_PROGRESS -> setOf(Status.REVIEW)
            Status.REVIEW      -> setOf(Status.DONE, Status.IN_PROGRESS)
            Status.DONE        -> emptySet()
        }
}
