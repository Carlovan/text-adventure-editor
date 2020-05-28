package controller

import javafx.collections.ObservableList
import model.Item
import model.Items
import model.StatisticsItems
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.MaybePSQLError
import sqlutils.safeTransaction
import tornadofx.asObservable
import viewmodel.ItemStatisticViewModel
import viewmodel.ItemViewModel
import viewmodel.StatisticViewModel
import viewmodel.fromViewModel
import kotlin.sequences.Sequence

class ItemController : ControllerWithContextAdventure() {
    val items: ObservableList<ItemViewModel>
        get() =
            transaction {
                Item.find { Items.adventure eq contextAdventure!!.item.id }
                    .map { ItemViewModel(it) }
                    .asObservable()
            }

    /**
     * Returns a Where filter to be used in queries
     */
    private fun getItemStatisticFilter(item: tornadofx.ItemViewModel<Item>, statistic: StatisticViewModel) =
        Op.build { (StatisticsItems.item eq item.item.id) and (StatisticsItems.statistic eq statistic.item.id) }

    fun createItem(newItem: ItemViewModel): MaybePSQLError =
        safeTransaction {
            Item.new {
                adventure = contextAdventure!!.item.id
                fromViewModel(newItem)
            }
        }

    fun deleteItem(item: ItemViewModel): MaybePSQLError =
        safeTransaction {
            item.item.delete()
        }

    fun commit(changes: Sequence<ItemViewModel>) =
        safeTransaction {
            changes.forEach {
                it.saveData()
                it.rollback()
            }
        }

    fun commit(change: ItemViewModel) =
        commit(sequenceOf(change))

    fun addStatisticModifier(itemValue: ItemViewModel, modifier: ItemStatisticViewModel) = safeTransaction {
        StatisticsItems.insert {
            it[item] = itemValue.item.id
            it[statistic] = modifier.statistic.value.item.id
            it[value] = modifier.value.value
        }
    }

    fun removeStatisticModifier(item: ItemViewModel, statistic: StatisticViewModel) = safeTransaction {
        StatisticsItems.deleteWhere { getItemStatisticFilter(item, statistic) }
    }

    fun commitStatisticModifier(item: ItemViewModel, modifiers: Sequence<ItemStatisticViewModel>) = safeTransaction {
        modifiers.forEach { modifier ->
            StatisticsItems.update({ getItemStatisticFilter(item, modifier.statistic.value) }) {
                it[value] = modifier.value.value
            }
        }
    }
}