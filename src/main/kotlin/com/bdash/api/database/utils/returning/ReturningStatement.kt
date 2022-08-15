package com.bdash.api.database.utils.returning

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.jetbrains.exposed.sql.statements.Statement
import org.jetbrains.exposed.sql.statements.StatementType
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.ResultSet

abstract class ReturningStatement(type: StatementType, targets: List<Table>) :
    Iterable<ResultRow>, Statement<ResultSet>(type, targets) {
    protected val transaction get() = TransactionManager.current()

    abstract val set: FieldSet

    override fun PreparedStatementApi.executeInternal(transaction: Transaction): ResultSet =
        executeQuery()

    private var iterator: Iterator<ResultRow>? = null

    fun exec() {
        require(iterator == null) { "already executed" }

        val resultIterator = ResultIterator(transaction.exec(this)!!)
        iterator = if (transaction.db.supportsMultipleResultSets) resultIterator
        else Iterable { resultIterator }.toList().iterator()
    }

    override fun iterator(): Iterator<ResultRow> =
        iterator ?: throw IllegalStateException("must call exec() first")

    protected inner class ResultIterator(val rs: ResultSet) : Iterator<ResultRow> {
        private var hasNext: Boolean? = null

        private val fieldsIndex = set.realFields.toSet().mapIndexed { index, expression -> expression to index }.toMap()

        override operator fun next(): ResultRow {
            if (hasNext == null) hasNext()
            if (hasNext == false) throw NoSuchElementException()
            hasNext = null
            return ResultRow.create(rs, fieldsIndex)
        }

        override fun hasNext(): Boolean {
            if (hasNext == null) hasNext = rs.next()
            if (hasNext == false) rs.close()
            return hasNext!!
        }
    }
}