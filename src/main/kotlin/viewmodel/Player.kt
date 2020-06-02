package viewmodel

import model.PlayerConfiguration
import tornadofx.ItemViewModel

fun PlayerConfiguration.fromViewModel(configuration: PlayerConfigurationViewModel) {
    name = configuration.name.value
    maxSkills = configuration.maxSkills.value
}

class PlayerConfigurationViewModel(initialValue: PlayerConfiguration? = null) :
    ItemViewModel<PlayerConfiguration>(initialValue) {

    val name = bind(PlayerConfiguration::name)
    val maxSkills = bind(PlayerConfiguration::maxSkills)

    fun saveData() {
        item?.let {
            it.fromViewModel(this)
            rollback()
        }
    }
}