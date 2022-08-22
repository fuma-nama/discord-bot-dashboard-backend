package net.sonmoosans.kbot

import bjda.bjda
import bjda.plugins.supercommand.supercommand
import bjda.plugins.ui.uiEvent
import bjda.wrapper.Mode
import com.bdash.api.startServer
import net.sonmoosans.kbot.command.TestCommands
import net.sonmoosans.kbot.database.table.BotSettings
import org.jetbrains.exposed.sql.Database
import net.sonmoosans.kbot.database.table.actions.KillKane as KillKaneAction
import net.sonmoosans.kbot.database.table.features.KillKane as KillKaneFeature

suspend fun main() {
    val bot = bjda(Mode.Default) {
        config {
            setToken(System.getenv("TOKEN"))
        }
        supercommand(
            TestCommands
        )
        uiEvent()
    }

    startServer {
        api = APIImpl()

        action(
            KillKaneAction
        )

        feature(
            KillKaneFeature
        )

        settings = BotSettings

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

        host = "http://localhost:8080"
        allowHost += "localhost:3000"

        oauth = {
            clientId = System.getenv("CLIENT_ID")
            clientSecret = System.getenv("CLIENT_SECRET")
            redirect = "http://localhost:3000/callback"
        }

        encrypt = {
            encryptKey = System.getenv("ENCRYPT_KEY")
            signKey = System.getenv("SIGN_KEY")
        }

        this.bot = {
            jda = bot.jda
        }
    }
}