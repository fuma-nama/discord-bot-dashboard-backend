package com.bdash.api.database

import com.bdash.api.Configuration
import com.bdash.api.database.dao.ActionDAO
import com.bdash.api.database.dao.FeatureDAO
import com.bdash.api.database.dao.SettingsDAO
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(config: Configuration) {
        val database = config.connect()

        FeatureDAO.register(config.features)
        ActionDAO.register(config.actions)
        SettingsDAO.register(config.settings)

        transaction(database) {
            SchemaUtils.create(
                config.settings,
                * config.features.toTypedArray(),
                * config.actions.toTypedArray()
            )
        }
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}