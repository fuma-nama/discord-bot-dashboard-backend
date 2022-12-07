package com.bdash.api.database.utils

import com.bdash.api.database.DatabaseFactory.dbQuery
import com.bdash.api.database.utils.returning.updateReturning
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.sql.select

suspend fun <E> OptionsContainer<E>.getOptions(guild: Long): E? = dbQuery {

    select { this@getOptions.guild eq guild }
        .map(::options)
        .singleOrNull()
}

/**
 * @return Updated Option Values
 */
suspend fun <E> OptionsContainer<E>.updateOptions(guild: Long, options: JsonElement): E? {
    val updated = dbQuery {
        val update = updateReturning(where = { this@updateOptions.guild eq guild }) {
            onUpdate(it, options)
        }

        update.singleOrNull()
    }

    return updated?.let { options(it) }
}