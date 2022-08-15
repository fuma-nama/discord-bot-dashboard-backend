package com.bdash.api.database.utils.returning

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.StatementType

class DeleteReturningStatement(
    private val table: Table,
    private val where: Op<Boolean>? = null,
    private val limit: Int? = 0,
    private val returning: ColumnSet? = null
) : ReturningStatement(StatementType.DELETE, listOf(table)) {
    override val set: FieldSet = returning ?: table

    override fun prepareSQL(transaction: Transaction): String = buildString {
        append("DELETE FROM ")
        append(transaction.identity(table))
        if (where != null) {
            append(" WHERE ")
            append(QueryBuilder(true).append(where).toString())
        }
        if (limit != null) {
            append(" LIMIT ")
            append(limit)
        }
        append(" RETURNING ")
        if (returning != null) {
            append(QueryBuilder(true).append(returning).toString())
        } else {
            append("*")
        }
    }

    override fun arguments(): Iterable<Iterable<Pair<IColumnType, Any?>>> =
        QueryBuilder(true).run {
            where?.toQueryBuilder(this)
            listOf(args)
        }

    companion object {
        fun where(
            table: Table,
            op: Op<Boolean>,
            limit: Int? = 0,
            returning: ColumnSet? = null
        ): DeleteReturningStatement = DeleteReturningStatement(
            table,
            op,
            limit,
            returning
        ).apply {
            exec()
        }
    }
}

fun Table.deleteReturningWhere(
    limit: Int? = 0,
    returning: ColumnSet? = null,
    where: SqlExpressionBuilder.() -> Op<Boolean>
): DeleteReturningStatement =
    DeleteReturningStatement.where(
        this,
        SqlExpressionBuilder.run(where),
        limit,
        returning
    )