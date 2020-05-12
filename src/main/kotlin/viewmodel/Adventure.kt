package viewmodel

import model.Adventure
import model.AdventureCreate
import tornadofx.ItemViewModel

class AdventureViewModel : ItemViewModel<Adventure>() {
    val id = bind(Adventure::id)
    val name = bind(Adventure::name)

    override fun toString(): String {
        return name.value
    }
}

class AdventureCreateViewModel : ItemViewModel<AdventureCreate>(AdventureCreate()) {
    val name = bind(AdventureCreate::name)
}