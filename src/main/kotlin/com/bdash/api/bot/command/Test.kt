package com.bdash.api.bot.command

import bjda.plugins.supercommand.SuperCommandGroup
import com.bdash.api.database.dao.SettingsDAO
import com.bdash.api.database.table.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

object EventCoroutine : CoroutineScope {
    override val coroutineContext = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
}

val TestCommands = SuperCommandGroup.create("test", "Test commands", guildOnly = true) {

    command("say", "Say Something") {

        execute(EventCoroutine) {
            val guild = SettingsDAO.getSettings(event.guild!!.idLong)

            event.reply(guild?.get(Settings.say) ?: "Unknown").queue()
        }
    }
}