package com.bdash.api.bot

import bjda.bjda
import bjda.plugins.IModule
import bjda.plugins.supercommand.supercommand
import bjda.plugins.ui.uiEvent
import bjda.wrapper.Mode
import com.bdash.api.bot.command.TestCommands
import net.dv8tion.jda.api.JDA

object Info {
    lateinit var jda: JDA
}

fun startBot() {
    main()
}

fun main() {
    bjda(Mode.Default) {
        config {
            setToken(System.getenv("TOKEN"))
        }
        supercommand(
            TestCommands
        )
        uiEvent()

        + object : IModule {
            override fun init(jda: JDA) {
                Info.jda = jda
            }
        }
    }
}