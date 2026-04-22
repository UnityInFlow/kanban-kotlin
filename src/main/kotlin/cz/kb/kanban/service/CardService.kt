package cz.kb.kanban.service

import cz.kb.kanban.model.Card
import cz.kb.kanban.model.Priority
import cz.kb.kanban.model.Status
import cz.kb.kanban.repository.CardRepository
import java.time.LocalDate

// SESSION 1 — BLOK 2 + 3
// Tady implementujeme logiku nad CardRepository

class CardService(
    private val repository: CardRepository = CardRepository,
) {

    fun getAllCards(): List<Card> = repository.findAll()

    // TODO [S1 B3 — guided]: pouzit filter { it.status == status } nad repository.findAll()
    fun getCardsByStatus(status: Status): List<Card> =
        emptyList()

    // TODO [S1 B3 — independent]: pouzit filter + extension fun isOverdue()
    //   repository.findAll().filter { it.isOverdue() }
    fun getOverdueCards(): List<Card> =
        emptyList()

    // TODO [S1 B3 — guided]: pouzit groupBy { it.status } nad repository.findAll()
    fun getBoardGrouped(): Map<Status, List<Card>> =
        emptyMap()

    fun createCard(title: String, priority: Priority, dueDate: LocalDate? = null): Card {
        require(title.isNotBlank()) { "Card title must not be blank" }
        return repository.create(title.trim(), priority, dueDate)
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
