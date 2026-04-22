package cz.kb.kanban.util

// `inline fun <reified T>` keeps the runtime type that a plain generic would erase.
// Java cannot express this — you'd need to pass `Class<T>` as an argument.
//
// Trade-off: `reified` requires `inline`, which copies the function body to each call site.
// Use it for small helpers (type checks, casts, factories), not big methods.

// Returns the simple name of T — handy for logs and error messages.
inline fun <reified T : Any> typeName(): String = T::class.simpleName ?: "Unknown"

// Keeps only elements of type T — wraps Kotlin stdlib's filterIsInstance for symmetry.
inline fun <reified T : Any> Iterable<*>.ofType(): List<T> = filterIsInstance<T>()
