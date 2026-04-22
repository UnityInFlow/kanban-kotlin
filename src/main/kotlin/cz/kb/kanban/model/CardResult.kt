package cz.kb.kanban.model

// Sealed interface — the Kotlin way to model a closed set of possible outcomes.
// Why prefer this over thrown exceptions for business failures?
//  - Caller sees every possible outcome in the return type signature.
//  - Compiler enforces exhaustive handling via `when`.
//  - Each variant carries its own payload (NotFound has an id, InvalidTransition has from/to).
//
// Java 17+ analogue: `sealed interface` + permits clause.
// C# analogue: discriminated union (not yet first-class in the language — usually emulated).
sealed interface CardResult {
    data class Success(val card: Card) : CardResult
    data class NotFound(val id: Long) : CardResult
    data class InvalidTransition(val from: Status, val to: Status) : CardResult
    data class ValidationError(val message: String) : CardResult
}
