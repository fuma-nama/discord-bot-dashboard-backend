package com.bdash.api.database.dao

import com.bdash.api.database.DatabaseFactory.dbQuery
import com.bdash.api.database.models.actions.KillKane
import com.bdash.api.database.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

object ActionDAO {
    val actions = arrayOf(KillKane).associateBy { it.actionId }

    suspend fun getActionDetail(guild: Long, actionId: String): ActionDetail? {
        val tasks = getTasks(guild, actionId) ?: return null

        return ActionDetail(tasks)
    }

    suspend fun getTasks(guild: Long, actionId: String): List<TaskInfo>? {
        val action = actions[actionId] ?: return null

        return dbQuery {
            action.select { action.guild eq guild }
                .map(action::info)
        }
    }

    suspend fun getTaskDetail(guild: Long, actionId: String, task: Int): TaskDetail? {
        val action = actions[actionId] ?: return null

        return dbQuery {
            action.select { action.guild eq guild; action.id eq task }
                .singleOrNull()
                ?.run(action::detail)
        }
    }

    /**
     * Insert and respond the task info
     *
     * respond 404 if action id doesn't exist
     */
    suspend fun PipelineContext<*, ApplicationCall>.addTask(
        guild: Long,
        actionId: String,
        name: String,
        options: JsonObject,
    ) {
        val action = actions[actionId] ?: return call.actionNotFound()

        val info = addTask(guild, action, name, options)
        call.respond(HttpStatusCode.OK, info)
    }

    suspend fun updateTask(guild: Long, actionId: String, task: Int, payload: TaskBody) =
        updateTask(guild, actionId, task, payload.name, payload.options)

    suspend fun updateTask(guild: Long, actionId: String, task: Int, name: String, options: JsonObject): Int? {
        val action = actions[actionId] ?: return null

        return dbQuery {
            action.update({ action.guild eq guild; action.id eq task }) {
                it[action.name] = name
                action.onUpdate(it, options)
            }
        }
    }

    /**
     * @return Created Task info
     */
    suspend fun addTask(guild: Long, action: Action, name: String, options: JsonObject) = dbQuery {
        val insert = action.insert {
            it[action.guild] = guild
            it[action.name] = name

            action.onInsert(it, options)
        }

        val result = insert.resultedValues!!.single()

        action.detail(result)
    }

    suspend fun deleteTask(guild: Long, actionId: String, task: Int): Int? {
        val action = actions[actionId] ?: return null

        return dbQuery {
            action.deleteWhere { action.guild eq guild; action.id eq task }
        }
    }

    suspend fun ApplicationCall.actionNotFound() {
        respond(HttpStatusCode.NotFound, "Action Id doesn't exists")
    }
}