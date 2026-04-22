package cz.kb.kanban.cli

import cz.kb.kanban.model.Priority
import cz.kb.kanban.model.Status
import cz.kb.kanban.service.CardService

// SESSION 1 — BLOK 3
// Spousteci bod aplikace — ukazuje board v terminalu

fun main() {
    val service = CardService()

    println("╔══════════════════════════════════════╗")
    println("║      Personal Kanban Board v1.0      ║")
    println("╚══════════════════════════════════════╝")
    println()

    // TODO [S1 B3 — guided]: zobrazit board groupBy status
    printBoard(service)

    println()
    println("--- Demo operace ---")

    // Vytvorit novou kartu
    val newCard = service.createCard("Implement login", Priority.HIGH)
    println("Vytvorena karta: $newCard")

    // Presunout kartu
    try {
        val moved = service.moveCard(newCard.id, Status.IN_PROGRESS)
        println("Karta presunuta: ${moved.status}")
    } catch (e: IllegalStateException) {
        // TODO [S2 B1]: toto try/catch nahradime za when(CardResult) { }
        println("Chyba prechodu: ${e.message}")
    }

    println()
    printBoard(service)
}

// TODO [S1 B3 — guided]: vylepsit vyvoj — pouzit service.getBoardGrouped() + Status.entries.forEach
//   Soucasna implementace vypisuje jen ploche seznamy; cil je zobrazit sloupce per status.
fun printBoard(service: CardService) {
    service.getAllCards().forEach { card ->
        // TODO [S1 B2 — independent]: pouzit card.displayTitle() extension fun
        val overdue = if (card.dueDate != null && card.dueDate.isBefore(java.time.LocalDate.now())) " ⚠️ OVERDUE" else ""
        println("  [${card.id}] ${card.title} | ${card.status} | ${card.priority}$overdue")
    }
}
