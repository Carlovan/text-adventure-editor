package viewmodel

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import model.Item
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

fun Item.fromViewModel(vm: viewmodel.ItemViewModel) {
    name = vm.name.value
    itemSlot = vm.itemSlotViewModel.value.item
    isConsumable = vm.isConsumable.value
}

class ItemViewModel(item: Item? = null) : ItemViewModel<Item>(item) {
    val name = bind(Item::name)
    val slotName = SimpleStringProperty(this, "viewModelProperty", item?.itemSlot?.name ?: "") as ReadOnlyStringProperty
    val isConsumable = bind(Item::isConsumable)

    val itemSlotViewModel = SimpleObjectProperty(this, "vmp", ItemSlotViewModel(item?.itemSlot))

    fun saveData() {
        item.name = name.value
        item.isConsumable = isConsumable.value
    }
}