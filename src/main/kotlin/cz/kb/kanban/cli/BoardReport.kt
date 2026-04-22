package cz.kb.kanban.cli

import cz.kb.kanban.model.Card
import cz.kb.kanban.model.Status
import cz.kb.kanban.model.isOverdue
import cz.kb.kanban.service.CardService

// Higher-order functions + `asSequence` + function references.
//
// Why `asSequence`?
//   - `List.filter { ... }.count()` materialises an intermediate list.
//   - `asSequence().filter { ... }.count()` streams one-by-one, no intermediate allocation.
//   - For a 4-item demo it does not matter; teach the discipline so it scales.
//
// Why function references (`::`)?
//   - `filter(Card::isOverdue)` reads better than `filter { it.isOverdue() }` when the
//     lambda just forwards the argument. Same bytecode after inlining.

fun CardService.summarizeBy(predicate: (Card) -> Boolean): Int =
    getAllCards().asSequence().filter(predicate).count()

fun CardService.topPriorityOverdue(limit: Int = 3): List<Card> =
    getAllCards().asSequence()
        .filter(Card::isOverdue)
        .sortedByDescending { it.priority }
        .take(limit)
        .toList()

fun printReport(service: CardService) {
    println("── Report ──────────────────────────────")
    println("Vsech karet:    ${service.summarizeBy { true }}")
    println("Po termine:     ${service.summarizeBy(Card::isOverdue)}")
    println("V prubehu:      ${service.summarizeBy { it.status == Status.IN_PROGRESS }}")
    val top = service.topPriorityOverdue().joinToString { "[${it.id}] ${it.title}" }
    println("Top prioritizovane po termine: ${top.ifEmpty { "zadne" }}")
    println()
}
