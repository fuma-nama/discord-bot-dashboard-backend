package com.bdash.api.bot.command

import bjda.plugins.supercommand.SuperCommandGroup
import com.bdash.api.database.dao.SettingsDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

val TestCommands = SuperCommandGroup.create("test", "Test commands", guildOnly = true) {
    val scope = EventCoroutine()

    command("say", "Say Something") {

        execute {
            scope.launch {
                val guild = SettingsDAO.get(event.guild!!.idLong)

                event.reply(guild?.say?: "Unknown").queue()
            }
        }
    }
}

val eventThread = Executors.newFixedThreadPool(10).asCoroutineDispatcher()

class EventCoroutine : CoroutineScope {
    override val coroutineContext = eventThread
}


