package cz.kb.kanban.model

// SESSION 1 — BLOK 1
// TODO: Prozkoumej enum class v Kotlinu vs Java enum
// Co umi navic? (properties, metody, when exhaustive)
enum class Status {
    TODO,
    IN_PROGRESS,
    REVIEW,
    DONE;

    // TODO [S1 B2 — stretch]: pridat fun label(): String pres when expression
    // fun label(): String = when(this) { ... }
}
