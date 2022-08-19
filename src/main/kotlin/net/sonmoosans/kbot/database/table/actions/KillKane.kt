package net.sonmoosans.kbot.database.table.actions

import com.bdash.api.database.setIf
import com.bdash.api.database.utils.Action
import com.bdash.api.database.utils.returning.UpdateReturningStatement
import com.bdash.api.utils.long
import com.bdash.api.utils.toJsonElement
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement

object KillKane : Action("action_kill_kane") {
    override val actionId = "kill_kane"

    val channel = long("channel")

    override fun onInsert(statement: InsertStatement<*>, options: JsonObject) {
        with(statement) {
            setIf(channel, options["channel"]) { long()!! }
        }
    }

    override fun onUpdate(statement: UpdateReturningStatement, options: JsonObject) {
        with(statement) {
            setIf(channel, options["channel"]) { long()!! }
        }
    }

    override fun options(self: ResultRow): Map<String, JsonElement> {
        return mapOf(
            "channel" to self[channel].toJsonElement()
        )
    }
}