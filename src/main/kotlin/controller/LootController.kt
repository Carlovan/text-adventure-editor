package controller

import javafx.collections.ObservableList
import model.Loot
import model.Loots
import model.LootsItems
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.safeTransaction
import tornadofx.asObservable
import viewmodel.*

class LootController : ControllerWithContextAdventure() {
    val loots: ObservableList<LootViewModel>
        get() = transaction {
            Loot.find { Loots.adventure eq contextAdventure!!.item.id }
                .map { LootViewModel(it) }
                .asObservable()
        }

    /**
     * Returns a Where filter to be used in queries
     */
    private fun getLootItemFilter(loot: tornadofx.ItemViewModel<Loot>, item: ItemViewModel) =
        Op.build { (LootsItems.loot eq loot.item.id) and (LootsItems.item eq item.item.id) }

    fun createLoot(loot: LootViewModel) =
        safeTransaction {
            Loot.new {
                adventure = contextAdventure!!.item.id
                fromViewModel(loot)
            }
        }

    fun deleteLoot(loot: LootViewModel) =
        safeTransaction {
            loot.item.delete()
        }

    fun commit(changes: kotlin.sequences.Sequence<LootViewModel>) =
        safeTransaction {
            changes.forEach { it.saveData() }
        }

    fun commit(change: DetailLootViewModel) =
        safeTransaction {
            change.saveData()
        }

    fun createDetail(master: LootViewModel) =
        transaction {
            DetailLootViewModel(master.item)
        }

    fun addItemToLoot(lootValue: DetailLootViewModel, lootItem: LootItemViewModel) = safeTransaction {
        LootsItems.insert {
            it[loot] = lootValue.item.id
            it[item] = lootItem.item.value.item.id
            it[quantity] = lootItem.quantity.value
        }
    }

    fun removeItemFromLoot(loot: tornadofx.ItemViewModel<Loot>, item: ItemViewModel) = safeTransaction {
        LootsItems.deleteWhere { getLootItemFilter(loot, item) }
    }

    fun commitItemInLoot(loot: tornadofx.ItemViewModel<Loot>, lootItems: kotlin.sequences.Sequence<LootItemViewModel>) =
        safeTransaction {
            lootItems.forEach { lootItem ->
                LootsItems.update({ getLootItemFilter(loot, lootItem.item.value) }) {
                    it[quantity] = lootItem.quantity.value
                }
            }
        }
}