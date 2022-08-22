package com.bdash.api

import com.bdash.api.database.DatabaseFactory
import com.bdash.api.database.utils.Action
import com.bdash.api.database.utils.Feature
import com.bdash.api.database.utils.Settings
import com.bdash.api.discord.DiscordApi
import com.bdash.api.plugins.configureRouting
import com.bdash.api.plugins.configureSecurity
import com.bdash.api.utils.APIException
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.JDA
import org.jetbrains.exposed.sql.Database
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation

val httpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }

    install(HttpRequestRetry) {
        retryIf { _, response -> !response.status.isSuccess() }
    }
}

suspend fun startServer(init: Configuration.() -> Unit) {
    val config = Configuration().apply(init)
    val auth = OAuthBuilder().apply(config.oauth)
    val encrypt = EncryptBuilder().apply(config.encrypt)
    val bot = BotBuilder().apply(config.bot)
    val api = config.api

    embeddedServer(Netty, port = 8080) {
        DiscordApi.init(bot.jda)
        DatabaseFactory.init(config)
        configureSecurity()
        configureRouting(auth, api)

        install(CORS) {
            allowCredentials = true

            for (host in config.allowHost) {
                allowHost(host)
            }

            allowMethod(HttpMethod.Patch)
            allowMethod(HttpMethod.Delete)

            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
        }

        install(StatusPages) {
            exception<APIException> { call, ex ->
                call.respond(ex.code, ex.message.orEmpty())
            }
        }

        install(ServerContentNegotiation) {
            json(Json {
                encodeDefaults = true
                ignoreUnknownKeys = true
            })
        }

        install(Sessions) {

            cookie<UserSession>("user_session") {
                val secretEncryptKey = hex(encrypt.encryptKey)
                val secretSignKey = hex(encrypt.signKey)

                cookie.path = "/"
                cookie.maxAge = encrypt.cookieExpire
                transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
            }
        }

        install(Authentication) {

            oauth("discord-oauth2") {
                urlProvider = { "${config.host}/callback" }
                providerLookup = {
                    OAuthServerSettings.OAuth2ServerSettings(
                        name = "discord",
                        authorizeUrl = "https://discord.com/api/oauth2/authorize",
                        accessTokenUrl = "https://discord.com/api/oauth2/token",
                        requestMethod = HttpMethod.Post,
                        clientId = auth.clientId,
                        clientSecret = auth.clientSecret,
                        defaultScopes = auth.scopes
                    )
                }
                client = httpClient
            }
        }
    }.start(wait = true)
}

@DslMarker
annotation class DslBuilder

@DslBuilder
class Configuration {
    lateinit var api: API
    val features = arrayListOf<Feature>()
    val actions = arrayListOf<Action>()
    lateinit var settings: Settings

    fun action(vararg actions: Action) {
        this.actions.addAll(actions)
    }

    fun feature(vararg features: Feature) {
        this.features.addAll(features)
    }

    /**
     * Server Url
     */
    lateinit var host: String

    val allowHost = arrayListOf<String>()

    /**
     * Client origin
     */
    lateinit var connect: () -> Database
    lateinit var oauth: OAuthBuilder.() -> Unit
    lateinit var encrypt: EncryptBuilder.() -> Unit
    lateinit var bot: BotBuilder.() -> Unit
}

@DslBuilder
class BotBuilder {
    lateinit var jda: JDA
}

@DslBuilder
class EncryptBuilder {
    lateinit var signKey: String
    lateinit var encryptKey: String
    var cookieExpire = 3.toDuration(DurationUnit.DAYS)
}

@DslBuilder
class OAuthBuilder {
    /**
     * Url to redirect after login (should be client url)
     */
    lateinit var redirect: String
    lateinit var clientId: String
    lateinit var clientSecret: String
    val scopes = arrayListOf("guilds", "identify")
}

data class UserSession(val token: String, val token_type: String)