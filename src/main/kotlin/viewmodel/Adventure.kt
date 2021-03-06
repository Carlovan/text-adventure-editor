package viewmodel

import model.Adventure
import tornadofx.ItemViewModel

fun Adventure.fromViewModel(adv: AdventureViewModel) {
    name = adv.name.value
}

class AdventureViewModel : ItemViewModel<Adventure>() {
    val id = bind(Adventure::id)
    val name = bind(Adventure::name)

    override fun toString(): String {
        return name.value
    }
}