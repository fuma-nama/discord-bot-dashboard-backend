package com.bdash.api.database

import com.bdash.api.database.dao.ActionDAO
import com.bdash.api.database.dao.FeatureDAO
import com.bdash.api.database.table.Settings
import com.bdash.api.database.utils.returning.UpdateReturningStatement
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = "jdbc:postgresql:example"
        val database = Database.connect(jdbcURL, driverClassName, user = "postgres", password = "10124Lol")

        transaction(database) {
            SchemaUtils.create(
                Settings,
                * FeatureDAO.features.values.toTypedArray(),
                * ActionDAO.actions.values.toTypedArray()
            )
        }
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

fun <S> UpdateReturningStatement.setIf(column: Column<S>, element: JsonElement?, mapper: JsonElement.() -> S) {

    if (element != null) {
        this[column] = mapper(element)
    }
}

fun <S> InsertStatement<*>.setIf(column: Column<S>, element: JsonElement?, mapper: JsonElement.() -> S) {

    if (element != null) {
        this[column] = mapper(element)
    }
}

fun <S> UpdateBuilder<*>.setIf(column: Column<S>, element: JsonElement?, mapper: JsonElement.() -> S) {

    if (element != null) {
        this[column] = mapper(element)
    }
}