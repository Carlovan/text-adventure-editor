package viewmodel

import model.Item
import model.ItemSlot
import model.Statistic
import model.StatisticsItems
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.select
import tornadofx.*
import java.text.DecimalFormat

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

    val modifiedStats
        get() = item?.let {
            StatisticsItems
                .select { StatisticsItems.item eq item.id }
                .map { ItemStatisticViewModel(it[StatisticsItems.statistic], it[StatisticsItems.value]) }
                .toList().asObservable()
        } ?: observableListOf()

    val modifiedStatsSummary =
        modifiedStats.joinToString { "${it.statistic.value.name.value} (${DecimalFormat("+#;-#").format(it.value.value)})" }
            .toProperty()

    fun saveData() {
        item?.fromViewModel(this)
    }
}

class ItemStatisticViewModel(statisticId: EntityID<Int>? = null, value: Int = 0) : ViewModel() {
    val statistic = property(statisticId?.let { StatisticViewModel(Statistic.findById(statisticId)) }).fxProperty
    val value = property(value).fxProperty
}