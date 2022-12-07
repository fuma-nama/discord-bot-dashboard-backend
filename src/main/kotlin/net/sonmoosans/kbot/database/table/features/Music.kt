package net.sonmoosans.kbot.database.table.features

import com.bdash.api.database.setIf
import com.bdash.api.database.utils.Feature
import com.bdash.api.database.utils.returning.UpdateReturningStatement
import com.bdash.api.utils.long
import com.bdash.api.utils.string
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.LongAsStringSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class MusicData(
    val message: String,
    @Serializable(with = LongAsStringSerializer::class)
    val channel: Long?,
)

object Music : Feature<MusicData>("music") {
    override val id = "music"

    /**
     * Message to send after killing Kane
     */
    val message = varchar("message", 1024).default("Kane is died!")

    /**
     * Listening channel
     */
    val channel = long("channel").nullable()

    override fun options(self: ResultRow): MusicData {
        return MusicData(self[message], self[channel])
    }

    override fun onUpdate(statement: UpdateReturningStatement, options: JsonElement) {
        val obj = options.jsonObject

        statement.run {
            setIf(message, obj["message"]) { string()!! }
            setIf(channel, obj["channel"]) { long() }
        }
    }
}