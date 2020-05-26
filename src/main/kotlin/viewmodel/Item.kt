package viewmodel

import model.Item
import model.ItemSlot
import tornadofx.select
import tornadofx.property

fun ItemSlot.fromViewModel(data: ItemSlotViewModel) {
    name = data.name.value
    capacity = data.capacity.value
}

class ItemSlotViewModel(initialValue: ItemSlot? = null) : tornadofx.ItemViewModel<ItemSlot>(initialValue) {
    val name = bind(ItemSlot::name)
    val capacity = bind(ItemSlot::capacity, defaultValue = 1)
    fun saveData() {
        item?.fromViewModel(this)
    }
}

fun Item.fromViewModel(vm: viewmodel.ItemViewModel) {
    name = vm.name.value
    itemSlot = vm.itemSlotViewModel.value.item
    isConsumable = vm.isConsumable.value
}

class ItemViewModel(item: Item? = null) : tornadofx.ItemViewModel<Item>(item) {
    val name = bind(Item::name)
    val isConsumable = bind(Item::isConsumable)

    val itemSlotViewModel = property(item?.itemSlot?.let { ItemSlotViewModel(it) }).fxProperty
    val slotName = itemSlotViewModel.select { it.name }

    fun saveData() {
        item?.fromViewModel(this)
    }
}