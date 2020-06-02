package controller

import model.PlayerConfiguration
import model.PlayerConfigurations
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.safeTransaction
import tornadofx.asObservable
import viewmodel.PlayerConfigurationViewModel
import viewmodel.fromViewModel

class PlayerConfigurationController : ControllerWithContextAdventure() {
    val playerConfigurations
        get() = transaction {
            PlayerConfiguration.find { PlayerConfigurations.adventure eq contextAdventure!!.item.id }
                .map { PlayerConfigurationViewModel(it) }
                .toList().asObservable()
        }

    fun createConfiguration(configuration: PlayerConfigurationViewModel) = safeTransaction {
        PlayerConfiguration.new {
            adventure = contextAdventure!!.item.id
            fromViewModel(configuration)
        }
    }

    fun deleteConfiguration(configuration: PlayerConfigurationViewModel) = safeTransaction {
        configuration.item.delete()
    }

    fun commit(changes: Sequence<PlayerConfigurationViewModel>) = safeTransaction {
        changes.forEach { it.saveData() }
    }

    fun commit(change: PlayerConfigurationViewModel) =
        commit(sequenceOf(change))
}