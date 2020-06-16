package viewmodel

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import model.Item
import model.ItemSlot
import model.Statistic
import model.StatisticsItems
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import toStringWithSign
import tornadofx.*

fun ItemSlot.fromViewModel(data: ItemSlotViewModel) {
    name = data.name.value
    capacity = if(data.capacity.value == 0) null else data.capacity.value
}

class ItemSlotViewModel(initialValue: ItemSlot? = null) : tornadofx.ItemViewModel<ItemSlot>(initialValue) {
    private val id = bind(ItemSlot::id)
    val name = bind(ItemSlot::name)
    val capacity = bind(ItemSlot::capacity)
    fun saveData() {
        item?.fromViewModel(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemSlotViewModel

        if (id.value != other.id.value) return false

        return true
    }

    override fun hashCode(): Int {
        return id.value.hashCode()
    }
}

fun Item.fromViewModel(vm: viewmodel.ItemViewModel) {
    name = vm.name.value
    itemSlot = vm.itemSlotViewModel.value.item
    isConsumable = vm.isConsumable.value
}

class ItemViewModel(item: Item? = null) : tornadofx.ItemViewModel<Item>(item) {
    private val id = bind(Item::id)
    val name = bind(Item::name)
    val isConsumable = bind(Item::isConsumable)

    val itemSlotViewModel = bind { property(transaction { item?.itemSlot?.let { ItemSlotViewModel(it) } }).fxProperty }
    val slotName = itemSlotViewModel.select { it.name }

    val modifiedStats
        get() = item?.let {
            StatisticsItems
                .select { StatisticsItems.item eq item.id }
                .map { ItemStatisticViewModel(it[StatisticsItems.statistic], it[StatisticsItems.value]) }
                .toList().asObservable()
        } ?: observableListOf()

    val modifiedStatsSummary =
        modifiedStats.joinToString { "${it.statistic.value.name.value} (${it.value.value.toStringWithSign()})" }
            .toProperty()

    fun saveData() {
        item?.fromViewModel(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemViewModel

        if (id.value != other.id.value) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

class ItemStatisticViewModel(statisticId: EntityID<Int>? = null, value: Int = 0) : ViewModel() {
    val statistic = property(statisticId?.let { StatisticViewModel(Statistic.findById(statisticId)) }).fxProperty
    val value = property(value).fxProperty
}