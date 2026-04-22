package cz.kb.kanban.model

// SESSION 2 — BLOK 1
// Tento soubor je zatim prazdny — vyplnime ho v Session 2.
//
// MOTIVACE (ukazeme v S2 B1 pred napsanim):
//   Proc nechceme tohle:
//
//   fun moveCard(card: Card, to: Status): Card {
//       if (card.status == Status.DONE) throw IllegalStateException("Card is already done!")
//       if (to == Status.TODO) throw IllegalArgumentException("Cannot move back to TODO!")
//       return card.copy(status = to)
//   }
//
//   Problem: caller nevi ze funkce muze vyhodit exception.
//   Reseni: sealed interface jako explicitni return typ.

// TODO [S2 B1 — guided]: vytvorit sealed interface CardResult
//
// sealed interface CardResult {
//     data class Success(val card: Card) : CardResult
//     data class NotFound(val id: Long) : CardResult
//     data class InvalidTransition(val from: Status, val to: Status) : CardResult
//     data class ValidationError(val message: String) : CardResult
// }
