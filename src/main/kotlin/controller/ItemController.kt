package controller

import javafx.collections.ObservableList
import model.Item
import model.Items
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.asObservable
import viewmodel.ItemViewModel

class ItemController : ControllerWithContextAdventure() {
    val items: ObservableList<ItemViewModel> get() =
        transaction {
            Item.find { Items.adventure eq contextAdventure!!.item.id }
                .map { ItemViewModel(it) }
                .asObservable()
        }
}