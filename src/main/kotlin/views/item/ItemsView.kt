package views.item

import controller.ItemController
import controller.ItemSlotController
import ellipses
import javafx.geometry.Pos
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.MaybePSQLError
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.ItemSlotViewModel
import viewmodel.ItemViewModel
import views.MasterView
import views.errorAlert
import views.runWithLoading
import views.ui

class ItemsView : MasterView<ItemViewModel>("Items") {
    val controller : ItemController by inject()

    private val itemsList = observableListOf<ItemViewModel>()

    override fun createDataTable(): TableView<ItemViewModel> =
        tableview {
            items = itemsList

            enableCellEditing()
            enableDirtyTracking()

            column("Name", ItemViewModel::name).makeEditable()
            column("Slot", ItemViewModel::slotName)
            column("Consumable", ItemViewModel::isConsumable).makeEditable()

            smartResize()
        }

    override val root = createRoot()

    private fun updateData() {
        runWithLoading { controller.items } ui {
            itemsList.clear()
            itemsList.addAll(it)
        }
    }

    override fun newItem() {
        find<CreateItemModal>().openModal(block = true)
        updateData()
    }

    override fun deleteItem() {
        runWithLoading { controller.deleteItem(dataTable.selectionModel.selectedItem) } ui {
            it.peek {
                errorAlert {
                    when (it) {
                        PSQLState.FOREIGN_KEY_VIOLATION -> "Cannot delete this item, it is related to other entities"
                        else -> null
                    }
                }
            }.onEmpty { updateData() }
        }
    }

    override fun saveTable() {
        runWithLoading {
            with(dataTable.editModel) {
                controller.commit(items
                    .asSequence()
                    .filter { it.value.isDirty }
                    .map { it.key })
            }
        } ui {
            it.peek {
                errorAlert {
                    when (it) {
                        PSQLState.UNIQUE_VIOLATION -> "Item name is not unique!"
                        else -> null
                    }
                }
            }.onEmpty { dataTable.editModel.commit() }
        }
    }

    override fun openDetail() {
        find<DetailItemModal>(DetailItemModal::item to dataTable.selectedItem).openModal(block = true)
        updateData()
    }

    override fun onDock() {
        updateData()
    }
}

abstract class ItemForm(private val isCreate: Boolean, title: String) : Fragment(title) {
    protected val controller: ItemController by inject()
    private val itemSlotsController: ItemSlotController by inject()

    private val itemSlotViewModels = observableListOf<ItemSlotViewModel>()
    val item by param(ItemViewModel())

    override val root = form {
        fieldset("Item") {
            field("Name") {
                textfield(item.name).required()
            }
            field("Item slot") {
                combobox(item.itemSlotViewModel, itemSlotViewModels) { // TODO bound property is not formatted propertly
                    cellFormat { text = it.name.value.ellipses(30) }
                    required()
                }
            }
            field("Is consumable") {
                checkbox(property = item.isConsumable)
            }
        }
        hbox {
            button(if(isCreate) "Create" else "Save") {
                enableWhen(item.valid)
                alignment = Pos.BOTTOM_RIGHT
                action {
                    runWithLoading { saveAction() } ui {
                        it.peek {
                            errorAlert {
                                when (it) {
                                    PSQLState.UNIQUE_VIOLATION -> "Item name is not unique!"
                                    else -> null
                                }
                            }
                        }.onEmpty { runLater { close() } }
                    }
                }
            }
        }
    }

    abstract fun saveAction(): MaybePSQLError // This is run with loading

    override fun onDock() {
        runLater {
            runWithLoading { itemSlotsController.itemSlots } ui {
                itemSlotViewModels.clear()
                itemSlotViewModels.addAll(it)
            }
        }
    }
}

class CreateItemModal : ItemForm(true, "Create item") {
    override fun saveAction(): MaybePSQLError =
        controller.createItem(item)
}

class DetailItemModal : ItemForm(false, "Edit item") {
    override fun saveAction(): MaybePSQLError =
        controller.commit(item)
}