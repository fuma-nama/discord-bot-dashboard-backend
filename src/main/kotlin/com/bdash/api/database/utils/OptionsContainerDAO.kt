package com.bdash.api.database.utils

import com.bdash.api.database.DatabaseFactory.dbQuery
import com.bdash.api.database.utils.returning.updateReturning
import com.bdash.api.utils.toJsonObject
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.sql.select

interface OptionsContainerDAO {
    suspend fun OptionsContainer.getOptions(guild: Long): Map<String, JsonElement>? = dbQuery {

        select { this@getOptions.guild eq guild }
            .map(::options)
            .singleOrNull()
    }

    /**
     * @return Updated Option Values
     */
    suspend fun PipelineContext<*, ApplicationCall>.updateOptions(guild: Long, container: OptionsContainer) {
        val options = call.receive<JsonObject>()

        val updated = with(container) {
            dbQuery {
                val update = updateReturning(where = { container.guild eq guild }) {
                    onUpdate(it, options)
                }

                update.single()
            }
        }

        call.respond(HttpStatusCode.OK, container.options(updated).toJsonObject())
    }
}