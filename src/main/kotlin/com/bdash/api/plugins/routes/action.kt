package com.bdash.api.plugins.routes

import com.bdash.api.TaskBody
import com.bdash.api.database.dao.ActionDAO
import com.bdash.api.database.dao.ActionDAO.addTask
import com.bdash.api.plugins.get
import com.bdash.api.utils.actionNotFound
import com.bdash.api.utils.verify
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.actions() = route("/action/{action}") {
    get {
        val (guild, action) = call["guild", "action"]

        verify(guild) {
            val detail = ActionDAO.getActionDetail(guild.toLong(), action)

            if (detail == null) {
                call.actionNotFound()
            } else {
                call.respond(detail)
            }
        }
    }

    post<TaskBody> { body ->
        val (guild, action) = call["guild", "action"]

        verify(guild) {
            if (body.name == null) {
                call.respond(HttpStatusCode.BadRequest, "Name is missing")
            }

            addTask(guild.toLong(), action, body.name!!, body.options)
        }
    }

    route("/{task}") {
        get {
            val (guild, action, task) = call["guild", "action", "task"]

            verify(guild) {
                val detail = ActionDAO.getTaskDetail(guild.toLong(), action, task.toInt())

                if (detail == null) {
                    call.actionNotFound()
                } else {
                    call.respond(detail)
                }
            }
        }

        patch<TaskBody> { body ->
            val (guild, action, task) = call["guild", "action", "task"]

            verify(guild) {
                val updated = ActionDAO.updateTask(
                    guild.toLong(), action, task.toInt(), body
                )

                if (updated == null) {
                    call.actionNotFound()
                } else {
                    call.respond(HttpStatusCode.OK, updated)
                }
            }
        }

        delete {
            val (guild, action, task) = call["guild", "action", "task"]

            verify(guild) {
                val result = ActionDAO.deleteTask(
                    guild.toLong(), action, task.toInt()
                )

                if (result == null) {
                    call.actionNotFound()
                } else {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}