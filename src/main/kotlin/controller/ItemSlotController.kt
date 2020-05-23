package controller

import javafx.collections.ObservableList
import model.ItemSlot
import model.ItemSlots
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.safeTransaction
import tornadofx.asObservable
import viewmodel.ItemSlotViewModel
import viewmodel.fromViewModel

class ItemSlotController : ControllerWithContextAdventure() {
    val itemSlots: ObservableList<ItemSlotViewModel>
        get() = transaction {
            ItemSlot.find { ItemSlots.adventure eq contextAdventure!!.item.id }
                .map { ItemSlotViewModel(it) }
        }.asObservable()

    fun createSlot(slot: ItemSlotViewModel) =
        safeTransaction {
            ItemSlot.new {
                adventure = contextAdventure!!.item.id
                fromViewModel(slot)
            }
        }

    fun commit(changes: Sequence<ItemSlotViewModel>) =
        safeTransaction {
            changes.forEach { it.saveData() }
        }

    fun deleteSlot(slot: ItemSlotViewModel) =
        safeTransaction { slot.item.delete() }
}