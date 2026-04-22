package cz.kb.kanban.repository

// Generic CRUD-style repository contract.
// T  — entity type (Card, User, Order, ...).
// ID — identifier type (Long, UUID, String, ...).
//
// Why a separate interface?
//   - Decouples callers from a specific implementation (in-memory, JPA, exposed, ktorm).
//   - Enables test doubles — swap the real repo for a `Repository<Card, Long>` fake.
//   - Establishes a consistent shape across the codebase — every entity follows the same idiom.
interface Repository<T, ID> {
    fun findAll(): List<T>
    fun findById(id: ID): T?
    fun save(entity: T): T
    fun delete(id: ID): Boolean
}
