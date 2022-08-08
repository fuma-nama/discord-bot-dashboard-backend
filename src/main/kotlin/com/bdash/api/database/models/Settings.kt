package com.bdash.api.database.models

import org.jetbrains.exposed.sql.Table

data class Setting(val id: Long, val say: String)

object Settings : Table() {
    val id = long("id").autoIncrement()
    val say = varchar("body", 1024).default("Hello World")

    override val primaryKey = PrimaryKey(id)
}