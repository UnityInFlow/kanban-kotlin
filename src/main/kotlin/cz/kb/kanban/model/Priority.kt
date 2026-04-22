package cz.kb.kanban.model

// SESSION 1 — BLOK 1
enum class Priority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    fun emoji(): String = when (this) {
        LOW      -> "🟢"
        MEDIUM   -> "🟡"
        HIGH     -> "🟠"
        CRITICAL -> "🔴"
    }
}
