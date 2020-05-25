package viewmodel

import model.ItemSlot
import tornadofx.ItemViewModel

fun ItemSlot.fromViewModel(data: ItemSlotViewModel) {
    name = data.name.value
    capacity = data.capacity.value
}

class ItemSlotViewModel(initialValue: ItemSlot? = null) : ItemViewModel<ItemSlot>(initialValue) {
    val name = bind(ItemSlot::name)
    val capacity = bind(ItemSlot::capacity, defaultValue = 1)
    fun saveData() {
        item?.fromViewModel(this)
    }
}