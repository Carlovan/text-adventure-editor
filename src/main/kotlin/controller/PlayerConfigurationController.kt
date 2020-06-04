package controller

import model.PlayerAvailableSlots
import model.PlayerConfiguration
import model.PlayerConfigurations
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.safeTransaction
import tornadofx.asObservable
import viewmodel.*

class PlayerConfigurationController : ControllerWithContextAdventure() {
    val playerConfigurations
        get() = transaction {
            PlayerConfiguration.find { PlayerConfigurations.adventure eq contextAdventure!!.item.id }
                .map { MasterPlayerConfigurationViewModel(it) }
                .toList().asObservable()
        }

    fun createConfiguration(configuration: SimplePlayerConfigurationViewModel) = safeTransaction {
        PlayerConfiguration.new {
            adventure = contextAdventure!!.item.id
            fromViewModel(configuration)
        }
    }

    fun deleteConfiguration(configuration: SimplePlayerConfigurationViewModel) = safeTransaction {
        configuration.item.delete()
    }

    fun commit(changes: Sequence<SimplePlayerConfigurationViewModel>) = safeTransaction {
        changes.forEach { it.saveData() }
    }

    fun commit(change: SimplePlayerConfigurationViewModel) =
        commit(sequenceOf(change))

    fun removeSlot(configuration: SimplePlayerConfigurationViewModel, slot: PlayerAvailableSlotViewModel) =
        safeTransaction {
            PlayerAvailableSlots.deleteWhere {
                (PlayerAvailableSlots.playerConf eq configuration.item.id) and
                        (PlayerAvailableSlots.itemSlot eq slot.itemSlot.value.item.id) and
                        (PlayerAvailableSlots.name eq slot.name.value)
            }
        }

    fun addSlot(configuration: SimplePlayerConfigurationViewModel, slot: PlayerAvailableSlotViewModel) =
        safeTransaction {
            println("In safeTransaction")
            PlayerAvailableSlots.insert {
                it[itemSlot] = slot.itemSlot.value.item.id
                it[playerConf] = configuration.item.id
                it[name] = slot.name.value
            }
        }

    fun getDetail(configuration: MasterPlayerConfigurationViewModel) =
        transaction { DetailPlayerConfigurationViewModel(configuration.item) }
}