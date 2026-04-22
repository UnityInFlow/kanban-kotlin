package cz.kb.kanban.model

// SESSION 1 — BLOK 1
// Enum class — konecna mnozina hodnot, exhaustive `when`, muze mit metody a vlastnosti.
enum class Status {
    TODO,
    IN_PROGRESS,
    REVIEW,
    DONE;

    fun label(): String = when (this) {
        TODO        -> "Ceka"
        IN_PROGRESS -> "V prubehu"
        REVIEW      -> "Ke kontrole"
        DONE        -> "Hotovo"
    }
}
