package viewmodel

import model.Adventure

class AdventureViewModel : CommittableItemViewModel<Adventure>() {
    val id = bind(Adventure::id)
    val name = bind(Adventure::name)

    override fun toString(): String {
        return name.value
    }
}