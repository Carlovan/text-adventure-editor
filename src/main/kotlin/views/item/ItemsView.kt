package views.item

import controller.ItemController
import controller.ItemSlotController
import ellipses
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.TableView
import javafx.scene.control.ToggleGroup
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
    val controller : ItemController by inject()

    val itemsList = observableListOf<ItemViewModel>()

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
        TODO("not implemented")
    }

    override fun saveTable() {
        TODO("not implemented")
    }

    override fun onDock() {
        updateData()
    }
}

class CreateItemModal : Fragment("Create item") {
    private val controller: ItemController by inject()
    private val itemSlotsController: ItemSlotController by inject()

    private val itemSlotViewModels = observableListOf<ItemSlotViewModel>()
    private val newItem = ItemViewModel()

    override val root = form {
        fieldset("Item") {
            field("Name") {
                textfield(newItem.name).required()
            }
            field("Item slot") {
                combobox(property = newItem.itemSlotViewModel) {
                    items = itemSlotViewModels
                    cellFormat { text = it.name.value.ellipses(30) }
                    required()
                }
            }
            val isConsGroup = ToggleGroup()
            isConsGroup.bind(newItem.isConsumable)
            field("Is consumable") {
                radiobutton("True", isConsGroup, value = true)
                radiobutton("False", isConsGroup, value = false).isSelected = true
            }
        }
        hbox {
            button("Create") {
                enableWhen(newItem.valid)
                alignment = Pos.BOTTOM_RIGHT
                action {
                    runWithLoading { controller.createItem(newItem) } ui {
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

    override fun onDock() {
        runWithLoading { itemSlotsController.itemSlots } ui {
            itemSlotViewModels.clear()
            itemSlotViewModels.addAll(it)
        }
    }
}