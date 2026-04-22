package cz.kb.kanban.model

import java.time.LocalDate

// SESSION 1 — BLOK 1
// TODO [guided]: Prozkoumej co vse data class generuje automaticky
//   - equals() / hashCode() / toString() / copy()
//   - Zkus: card.copy(status = Status.IN_PROGRESS)
//   - Porovnej s Java POJO + Lombok @Data nebo C# record

data class Card(
    val id: Long,
    val title: String,
    val status: Status,
    val priority: Priority,
    val dueDate: LocalDate?,        // nullable — proc? co se stane kdyz dam LocalDate bez ??
    val description: String = "",   // default value — jak to nahrazuje method overloading?
)

// TODO [S1 B2 — guided]: extension function isOverdue()
// fun Card.isOverdue(): Boolean = ???

// TODO [S1 B2 — stretch]: extension function displayTitle() vracejici "[CRITICAL] Setup CI 🔴"
// fun Card.displayTitle(): String = ???
