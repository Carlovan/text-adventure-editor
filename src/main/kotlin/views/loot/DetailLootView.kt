package views.loot

import controller.LootController
import javafx.scene.control.TableView
import onEmpty
import org.jetbrains.exposed.sql.transactions.transaction
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailLootViewModel
import viewmodel.LootItemViewModel
import views.MasterView
import views.errorAlert
import views.runWithLoading
import views.ui

class DetailLootView : Fragment("Loot") {
    private val controller: LootController by inject()
    val loot: DetailLootViewModel by param()
    private val lootItems = observableListOf<LootItemViewModel>()

    override val root = borderpane {
        paddingAll = 20.0
        center {
            vbox {
                spacing = 10.0
                label("Loot description")
                textarea(property = loot.desc)
                label("Contained items")
                add(LootItemsMasterTable())
            }
        }
        bottom {
            hbox {
                spacing = 10.0
                button("Save") {
                    enableWhen(loot.dirty)
                    action(::saveLoot)
                }
                button("Back") {
                    action {
                        replaceWith<LootsView>()
                    }
                }
            }
        }
    }

    fun updateData() {
        runWithLoading { transaction { loot.items } } ui {
            lootItems.clear()
            lootItems.addAll(it)
        }
    }

    private fun saveLoot() {
        runWithLoading { controller.commit(loot) }
    }

    override fun onDock() {
        updateData()
    }

    inner class LootItemsMasterTable() : MasterView<LootItemViewModel>() {
        override val root = createRoot(false)

        override fun createDataTable(): TableView<LootItemViewModel> =
            tableview(lootItems) {
                enableDirtyTracking()
                enableCellEditing()

                column<LootItemViewModel, String>("Item") { it.value.item.value.name }
                column("Quantity", LootItemViewModel::quantity).makeEditable()
            }

        override fun newItem() {
            find<AddLootItemModal>(AddLootItemModal::loot to loot).openModal(block = true)
            updateData()
        }

        override fun deleteItem() {
            runWithLoading { controller.removeItemFromLoot(loot, dataTable.selectedItem!!.item.value) } ui {
                it.peek { errorAlert { "Cannot remove item. $it" } }
                    .onEmpty { updateData() }
            }
        }

        override fun saveTable() {
            runWithLoading {
                with(dataTable.editModel) {
                    controller.commitItemInLoot(loot, items.asSequence()
                        .filter { it.value.isDirty }
                        .map { it.key })
                }
            } ui {
                it.peek { error ->
                    errorAlert {
                        when (error) {
                            PSQLState.CHECK_VIOLATION -> "Quantity must be a positive integer!"
                            else -> null
                        }
                    }
                }.onEmpty { dataTable.editModel.commit() }
            }
        }
    }
}
