package net.sonmoosans.kbot

import com.bdash.api.API
import com.bdash.api.Notification
import com.bdash.api.json
import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

typealias Context = PipelineContext<Unit, ApplicationCall>

class APIImpl : API {
    override fun Context.getGuildDetail(guild: String): JsonElement {

        return ServerDetails(4).json()
    }

    override fun Context.getGuildDetailAdvanced(guild: String): JsonElement {

        return ServerDetailsAdvanced(5).json()
    }

    override fun Context.getNotifications(guild: String): Array<Notification> {
        return arrayOf(
            Notification("Kane is a Gay", "The god of world: King Shark just found that Kane is a Gay")
        )
    }
}

@Serializable
class ServerDetails(
    val members: Int,
)

@Serializable
class ServerDetailsAdvanced(
    val members: Int,
)