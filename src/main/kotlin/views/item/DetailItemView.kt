package views.item

import controller.ItemController
import javafx.scene.control.TableView
import onEmpty
import org.jetbrains.exposed.sql.transactions.transaction
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.ItemStatisticViewModel
import viewmodel.ItemViewModel
import views.MasterView
import views.errorAlert
import views.runWithLoading
import views.ui

class DetailItemView : Fragment("Edit item") {
    private val controller: ItemController by inject()
    val item: ItemViewModel by param()
    private val itemStatistics = observableListOf<ItemStatisticViewModel>()
    private var itemForm: ItemForm by singleAssign()

    override val root = borderpane {
        paddingAll = 20.0
        center {
            vbox {
                itemForm = find(ItemForm::item to item)
                add(itemForm)
                add(ItemStatisticsMasterView())
            }
        }
        bottom {
            hbox {
                spacing = 10.0
                button("Save") {
                    enableWhen(item.dirty or item.itemSlotViewModel.value.dirty)
                    action(::save)
                }
                button("Back") {
                    action {
                        item.rollback()
                        replaceWith<ItemsView>()
                    }
                }
            }
        }
    }

    private fun updateData() {
        runWithLoading { transaction { item.modifiedStats } } ui {
            itemStatistics.clear()
            itemStatistics.addAll(it)
        }
    }

    private fun save() {
        runWithLoading { controller.commit(item) } ui {
            it.peek {
                errorAlert {
                    when (it) {
                        PSQLState.UNIQUE_VIOLATION -> "Item name is not unique!"
                        else -> null
                    }
                }
            }
        }
    }

    override fun onDock() {
        itemForm.doneLoading.onChange { if (it) updateData() }
    }

    inner class ItemStatisticsMasterView : MasterView<ItemStatisticViewModel>() {
        override val root = createRoot(false)

        override fun createDataTable(): TableView<ItemStatisticViewModel> =
            tableview(itemStatistics) {
                enableDirtyTracking()
                enableCellEditing()

                column<ItemStatisticViewModel, String>("Statistic name") { it.value.statistic.value.name }
                column("Value modifier", ItemStatisticViewModel::value).makeEditable()
            }

        override fun newItem() {
            find<AddItemStatisticModal>(AddItemStatisticModal::item to item).openModal(block = true)
            updateData()
        }

        override fun deleteItem() {
            runWithLoading { controller.removeStatisticModifier(item, dataTable.selectedItem!!.statistic.value) } ui {
                it.peek { errorAlert { "Cannot remove statistic. $it" } }
                    .onEmpty { updateData() }
            }
        }

        override fun saveTable() {
            runWithLoading {
                with(dataTable.editModel) {
                    controller.commitStatisticModifier(item, items.asSequence()
                        .filter { it.value.isDirty }
                        .map { it.key })
                }
            } ui {
                it.peek { error ->
                    errorAlert { "$error" }
                }.onEmpty { dataTable.editModel.commit() }
            }
        }
    }
}