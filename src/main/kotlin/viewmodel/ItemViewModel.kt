package viewmodel

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty
import model.Item
import model.ItemSlots
import tornadofx.ItemViewModel

class ItemViewModel(item: Item? = null) : ItemViewModel<Item>(item) {
    val name = bind(Item::name)
    val slotName = SimpleStringProperty(this, "viewModelProperty", item?.itemSlot?.name ?: "") as ReadOnlyStringProperty
}