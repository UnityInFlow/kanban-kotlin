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

// Extension functions — Kotlin way to add behaviour without inheritance.
// Resolved statically at call site; does not modify the class bytecode.

fun Card.isOverdue(): Boolean =
    dueDate?.isBefore(LocalDate.now()) == true && status != Status.DONE

fun Card.displayTitle(): String =
    "[${priority.name}] $title ${priority.emoji()}"
