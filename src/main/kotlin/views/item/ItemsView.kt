package views.item

import controller.ItemController
import controller.ItemSlotController
import ellipses
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.ItemSlotViewModel
import viewmodel.ItemViewModel
import views.MasterView
import views.errorAlert
import views.runWithLoading
import views.ui

class ItemsView : MasterView<ItemViewModel>("Items") {
    val controller: ItemController by inject()

    private val itemsList = observableListOf<ItemViewModel>()

    override fun createDataTable(): TableView<ItemViewModel> =
        tableview {
            items = itemsList

            enableCellEditing()
            enableDirtyTracking()

            column("Name", ItemViewModel::name).makeEditable()
            column("Slot", ItemViewModel::slotName)
            column("Consumable", ItemViewModel::isConsumable).makeEditable()
            column("Modified stats", ItemViewModel::modifiedStatsSummary)

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
        replaceWith(find<DetailItemView>(DetailItemView::item to dataTable.selectedItem))
    }

    override fun onDock() {
        updateData()
    }
}

class ItemForm : Fragment() {
    private val itemSlotsController: ItemSlotController by inject()
    val item by param(ItemViewModel())
    val doneLoading = SimpleBooleanProperty(false)

    private val itemSlotViewModels = observableListOf<ItemSlotViewModel>()

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
    }

    override fun onDock() {
        runWithLoading { itemSlotsController.itemSlots } ui {
            itemSlotViewModels.clear()
            itemSlotViewModels.addAll(it)
            doneLoading.value = true
        }
    }
}