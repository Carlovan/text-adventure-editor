package views.item

import controller.ItemSlotController
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.ItemSlotViewModel
import views.MasterView
import views.errorAlert
import views.runWithLoading
import views.ui

class ItemSlotsView : MasterView<ItemSlotViewModel>("Item slots") {
    private val controller: ItemSlotController by inject()

    private val slots = observableListOf<ItemSlotViewModel>()

    override val root = createRoot(false)

    override fun createDataTable(): TableView<ItemSlotViewModel> =
        tableview {
            items = slots

            enableCellEditing()
            enableDirtyTracking()

            column("Name", ItemSlotViewModel::name).makeEditable()
            column("Capacity", ItemSlotViewModel::capacity).makeEditable()
        }

    private fun updateData() {
        runWithLoading { controller.itemSlots } ui {
            slots.clear()
            slots.addAll(it)
        }
    }

    override fun newItem() {
        find<CreateItemSlotModal>().openModal(block = true)
        updateData()
    }

    override fun deleteItem() {
        runWithLoading { controller.deleteSlot(dataTable.selectedItem!!) } ui {
            it.peek { error ->
                errorAlert {
                    when (error) {
                        PSQLState.FOREIGN_KEY_VIOLATION -> "Cannot delete: this slot is used somewhere else"
                        else -> null
                    }
                }
            }.onEmpty { updateData() }
        }
    }

    override fun saveTable() {
        runWithLoading {
            with(dataTable.editModel) {
                controller.commit(items.asSequence()
                    .filter { it.value.isDirty }
                    .map { it.key })
            }
        } ui {
            it.peek {
                errorAlert {
                    when (it) {
                        PSQLState.UNIQUE_VIOLATION -> "Slot name is not unique!"
                        else -> null
                    }
                }
            }.onEmpty { dataTable.editModel.commit() }
        }
    }

    override fun onDock() {
        updateData()
    }
}

class CreateItemSlotModal : Fragment() {
    private val controller: ItemSlotController by inject()
    private val newSlot = ItemSlotViewModel()

    override val root = form {
        fieldset("Create item slot") {
            field("Slot name") {
                textfield(newSlot.name).required()
            }
            field("Slot capacity") {
                textfield(newSlot.capacity) {
                    required()
                    validator { text ->
                        if (text?.toIntOrNull() ?: -1 < 0) {
                            error("Only positive integers are allowed")
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
            button("Create") {
                enableWhen(newSlot.valid)
                action(::save)
            }
            button("Cancel") {
                action {
                    close()
                }
            }
        }
    }

    private fun save() {
        runWithLoading { controller.createSlot(newSlot) } ui {
            it.peek {
                errorAlert { "Cannot create the item slot! $it" }
            }.onEmpty { runLater { close() } }
        }
    }
}
