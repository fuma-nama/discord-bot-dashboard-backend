package com.bdash.api.database.utils

import com.bdash.api.TaskDetail
import com.bdash.api.TaskInfo
import com.bdash.api.database.utils.returning.UpdateReturningStatement
import com.bdash.api.utils.toJsonObject
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.statements.InsertStatement

abstract class Action(name: String = "") : Table(name) {
    abstract val actionId: String

    val id = integer("id").autoIncrement()
    val name = varchar("name", 200)
    val guild = long("guild")
    val createdAt = datetime("date_created").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(guild, id)

    fun info(self: ResultRow): TaskInfo {
        return TaskInfo(self[id], self[name], self[createdAt])
    }

    fun detail(self: ResultRow): TaskDetail {
        return TaskDetail(
            self[id], self[name], self[createdAt],
            options(self).toJsonObject()
        )
    }

    abstract fun onInsert(statement: InsertStatement<*>, options: JsonObject)

    abstract fun onUpdate(statement: UpdateReturningStatement, options: JsonObject)

    abstract fun options(self: ResultRow): Map<String, JsonElement>
}