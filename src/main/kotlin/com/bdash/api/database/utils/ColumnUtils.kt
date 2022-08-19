package com.bdash.api.database

import com.bdash.api.database.utils.returning.UpdateReturningStatement
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder

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

fun <S> UpdateReturningStatement.setIf(column: Column<S>, element: JsonElement?, mapper: JsonElement.() -> S) {

    if (element != null) {
        this[column] = mapper(element)
    }
}