package views.loot

import controller.ItemController
import controller.LootController
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailLootViewModel
import viewmodel.LootItemViewModel
import views.errorAlert
import views.runWithLoading
import views.ui

class AddLootItemModal : Fragment("Add item") {
    private val itemController: ItemController by inject()
    private val controller: LootController by inject()

    val loot: DetailLootViewModel by param()
    private val newLootItem = LootItemViewModel()

    private val items = observableListOf<viewmodel.ItemViewModel>()

    override val root = form {
        fieldset("Add items") {
            field("Select item") {
                combobox(newLootItem.item, items) {
                    required()
                    cellFormat {
                        text = it.name.value
                    }
                }
            }
            field("Quantity") {
                textfield(newLootItem.quantity) {
                    required()
                    validator { text ->
                        if (text != null && text.toIntOrNull() ?: 0 <= 0) {
                            error("Quantity must be a positive integer")
                        } else {
                            null
                        }
                    }
                    filterInput { it.controlNewText.isInt() }
                }
            }
        }
        hbox {
            spacing = 10.0
            button("Add") {
                enableWhen(newLootItem.valid)
                action(::addLootItem)
            }
            button("Close") {
                action {
                    close()
                }
            }
        }
    }

    private fun updateData() {
        runWithLoading { itemController.items } ui {
            items.clear()
            items.addAll(it)
        }
    }

    private fun addLootItem() {
        runWithLoading { controller.addItemToLoot(loot, newLootItem) } ui {
            it.peek { error ->
                errorAlert {
                    when (error) {
                        PSQLState.UNIQUE_VIOLATION -> "This item is already in the loot, try changing the quantity"
                        else -> null
                    }
                }
            }.onEmpty { runLater { close() } }
        }
    }

    override fun onDock() {
        runLater {
            updateData()
        }
    }
}