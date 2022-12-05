package net.sonmoosans.kbot

import com.bdash.api.startServer
import net.dv8tion.jda.api.JDABuilder
import net.sonmoosans.kbot.database.table.BotSettings
import org.jetbrains.exposed.sql.Database
import net.sonmoosans.kbot.database.table.actions.KillKane as KillKaneAction
import net.sonmoosans.kbot.database.table.features.KillKane as KillKaneFeature

suspend fun main() {
    val bot = JDABuilder.createDefault(System.getenv("TOKEN"))
        .build()
        .awaitReady()

    startServer {
        api = APIImpl()
        jda = bot
        host = "http://localhost:8080"
        allowOrigin += "localhost:3000"
        settings = BotSettings

        action(
            KillKaneAction
        )

        feature(
            KillKaneFeature
        )

        connect = {
            val driverClassName = "org.postgresql.Driver"
            val jdbcURL = "jdbc:postgresql:example"

            Database.connect(
                jdbcURL,
                driverClassName,
                user = System.getenv("DB_USER"),
                password = System.getenv("DB_PASSWORD")
            )
        }

        oauth = {
            clientId = System.getenv("CLIENT_ID")
            clientSecret = System.getenv("CLIENT_SECRET")
            redirect = "http://localhost:3000/callback"
        }

        encrypt = {
            encryptKey = System.getenv("ENCRYPT_KEY")
            signKey = System.getenv("SIGN_KEY")
        }
    }
}