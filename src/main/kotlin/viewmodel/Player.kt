package viewmodel

import model.ItemSlot
import model.PlayerAvailableSlots
import model.PlayerConfiguration
import org.jetbrains.exposed.sql.select
import tornadofx.*
import tornadofx.ItemViewModel

fun PlayerConfiguration.fromViewModel(configuration: SimplePlayerConfigurationViewModel) {
    name = configuration.name.value
    maxSkills = configuration.maxSkills.value
}

open class SimplePlayerConfigurationViewModel(initialValue: PlayerConfiguration? = null) :
    ItemViewModel<PlayerConfiguration>(initialValue) {

    val name = bind(PlayerConfiguration::name)
    val maxSkills = bind(PlayerConfiguration::maxSkills)

    open fun saveData() {
        item?.let {
            it.fromViewModel(this)
            rollback()
        }
    }
}

class MasterPlayerConfigurationViewModel(initialValue: PlayerConfiguration? = null) :
    SimplePlayerConfigurationViewModel(initialValue) {

    val slotsCount = property(initialValue?.slots?.count()).fxProperty
    val skillsCount = property(initialValue?.skills?.count()).fxProperty
    val statisticsCount = property(initialValue?.statistics?.count()).fxProperty
}

class DetailPlayerConfigurationViewModel(initialValue: PlayerConfiguration? = null) :
    SimplePlayerConfigurationViewModel(initialValue) {

    val skills get() = item?.skills?.map { SkillViewModel(it) }?.toList()?.asObservable() ?: observableListOf()
    val slots
        get() =
            item?.let {
                PlayerAvailableSlots.select { PlayerAvailableSlots.playerConf eq it.id }
                    .map { row ->
                        PlayerAvailableSlotViewModel(
                            ItemSlot.findById(row[PlayerAvailableSlots.itemSlot])!!,
                            row[PlayerAvailableSlots.name]
                        )
                    }.toList().asObservable()
            } ?: observableListOf()
    val statistics
        get() = item?.statistics?.map { StatisticViewModel(it) }?.toList()?.asObservable() ?: observableListOf()
}

class PlayerAvailableSlotViewModel(slot: ItemSlot? = null, name: String = "") : ViewModel() {
    val itemSlot = property(slot?.let { ItemSlotViewModel(slot) }).fxProperty
    val name = property(name).fxProperty
}