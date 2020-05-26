package controller

import javafx.collections.ObservableList
import model.Item
import model.Items
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.MaybePSQLError
import sqlutils.safeTransaction
import tornadofx.asObservable
import viewmodel.ItemViewModel
import viewmodel.fromViewModel

class ItemController : ControllerWithContextAdventure() {
    val items: ObservableList<ItemViewModel> get() =
        transaction {
            Item.find { Items.adventure eq contextAdventure!!.item.id }
                .map { ItemViewModel(it) }
                .asObservable()
        }

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
}