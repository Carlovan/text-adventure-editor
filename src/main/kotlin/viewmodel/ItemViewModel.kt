package viewmodel

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import model.Item
import model.ItemSlots
import tornadofx.ItemViewModel

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