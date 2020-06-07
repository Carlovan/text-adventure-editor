package viewmodel

import model.Item
import model.Loot
import model.LootsItems
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.sum
import tornadofx.ItemViewModel
import tornadofx.ViewModel
import tornadofx.asObservable
import tornadofx.property

fun Loot.fromViewModel(loot: LootViewModel) {
    desc = loot.desc.value
}

class LootViewModel(loot: Loot? = null) : ItemViewModel<Loot>(loot) {
    private val id = bind(Loot::id)
    val desc = bind(Loot::desc)
    val itemsCount = property(loot?.let { loot ->
        LootsItems.quantity.sum().alias("items_count").let { itemsCountAlias ->
            LootsItems
                .slice(itemsCountAlias)
                .select { LootsItems.loot eq loot.id }
                .map { it[itemsCountAlias] }
                .firstOrNull()
        }
    } ?: 0).fxProperty

    fun saveData() {
        item?.apply {
            fromViewModel(this@LootViewModel)
            rollback()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LootViewModel

        if (id.value != other.id.value) return false

        return true
    }

    override fun hashCode(): Int {
        return id.value.hashCode()
    }
}

class DetailLootViewModel(loot: Loot? = null) : ItemViewModel<Loot>(loot) {
    val desc = bind(Loot::desc)

    val items
        get() =
            LootsItems.select { LootsItems.loot eq item.id }
                .map { LootItemViewModel(it[LootsItems.item], it[LootsItems.quantity]) }
                .toList().asObservable()

    fun saveData() {
        item?.let {
            it.desc = desc.value
            rollback()
        }
    }
}

class LootItemViewModel(itemId: EntityID<Int>? = null, quantityVal: Int = 0) : ViewModel() {
    val quantity = property(quantityVal).fxProperty
    val item = property(itemId?.let { viewmodel.ItemViewModel(Item.findById(itemId)) }).fxProperty
}