package com.bdash.api.database.utils.returning

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.StatementType

class UpdateReturningStatement(
    private val table: Table,
    private val where: Op<Boolean>? = null,
    private val limit: Int? = null,
    private val returning: ColumnSet? = null
) : ReturningStatement(StatementType.DELETE, listOf(table)) {
    override val set: FieldSet = returning ?: table

    private val firstDataSet: List<Pair<Column<*>, Any?>>
        get() = values.toList()

    override fun prepareSQL(transaction: Transaction): String =
        with(QueryBuilder(true)) {
            +"UPDATE "
            table.describe(transaction, this)

            if (firstDataSet.isNotEmpty()) {
                firstDataSet.appendTo(this, prefix = " SET ") { (col, value) ->
                    append("${transaction.identity(col)}=")
                    registerArgument(col, value)
                }
            }


            where?.let {
                +" WHERE "
                +it
            }
            limit?.let {
                +" LIMIT "
                +it
            }

            +" RETURNING "
            if (returning != null) {
                append(returning)
            } else {
                +"*"
            }

            toString()
        }

    override fun arguments(): Iterable<Iterable<Pair<IColumnType, Any?>>> =
        QueryBuilder(true).run {
            for ((key, value) in values) {
                registerArgument(key, value)
            }
            where?.toQueryBuilder(this)
            listOf(args)
        }

    // region UpdateBuilder
    private val values: MutableMap<Column<*>, Any?> = LinkedHashMap()

    operator fun <S> set(column: Column<S>, value: S) {
        when {
            values.containsKey(column) -> error("$column is already initialized")
            !column.columnType.nullable && value == null -> error("Trying to set null to not nullable column $column")
            else -> values[column] = value
        }
    }

    @JvmName("setWithEntityIdExpression")
    operator fun <S, ID : EntityID<S>, E : Expression<S>> set(
        column: Column<ID>,
        value: E
    ) {
        require(!values.containsKey(column)) { "$column is already initialized" }
        values[column] = value
    }

    @JvmName("setWithEntityIdValue")
    operator fun <S : Comparable<S>, ID : EntityID<S>, E : S?> set(
        column: Column<ID>,
        value: E
    ) {
        require(!values.containsKey(column)) { "$column is already initialized" }
        values[column] = value
    }

    operator fun <T, S : T, E : Expression<S>> set(column: Column<T>, value: E) =
        update(column, value)

    operator fun <S> set(column: CompositeColumn<S>, value: S) {
        @Suppress("UNCHECKED_CAST")
        column.getRealColumnsWithValues(value).forEach { (realColumn, itsValue) ->
            set(
                realColumn as Column<Any?>,
                itsValue
            )
        }
    }

    fun <T, S : T?> update(column: Column<T>, value: Expression<S>) {
        require(!values.containsKey(column)) { "$column is already initialized" }
        values[column] = value
    }

    fun <T, S : T?> update(
        column: Column<T>,
        value: SqlExpressionBuilder.() -> Expression<S>
    ) {
        require(!values.containsKey(column)) { "$column is already initialized" }
        values[column] = SqlExpressionBuilder.value()
    }
    // endregion
}

fun <T : Table> T.updateReturning(
    where: SqlExpressionBuilder.() -> Op<Boolean>,
    limit: Int? = null,
    returning: ColumnSet? = null,
    body: T.(UpdateReturningStatement) -> Unit
): UpdateReturningStatement = UpdateReturningStatement(
    this,
    SqlExpressionBuilder.run(where),
    limit,
    returning
).apply {
    this@updateReturning.body(this)
    exec()
}