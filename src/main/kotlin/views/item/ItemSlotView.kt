package views.item

import controller.ItemSlotController
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.MaybePSQLError
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.ItemSlotViewModel
import views.*

class ItemSlotsView : MasterView<ItemSlotViewModel>("Item slots") {
    private val controller: ItemSlotController by inject()

    private val slots = observableListOf<ItemSlotViewModel>()

    override val root = createRoot()

    override fun createDataTable(): TableView<ItemSlotViewModel> =
        tableview {
            items = slots

            enableCellEditing()
            enableDirtyTracking()

            column("Name", ItemSlotViewModel::name).makeEditable()
            column("Capacity", ItemSlotViewModel::capacity) {
                cellFormat {
                    text = if (it == 0) "∞" else it.toString()
                }
            }
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

    override fun openDetail() {
        find<DetailItemSlotModal>(DetailItemSlotModal::itemSlot to dataTable.selectedItem).openModal(block = true)
        updateData()
    }

    override fun onDock() {
        updateData()
    }
}

abstract class ItemSlotForm(private val isCreate: Boolean) : Fragment() {
    protected val controller: ItemSlotController by inject()
    val itemSlot by param(ItemSlotViewModel())
    private val isInfinite = booleanProperty(itemSlot.capacity.value == 0)

    override val root = form {
        fieldset("Create item slot") {
            field("Slot name") {
                textfield(itemSlot.name).required()
            }
            field("Is infinite") {
                checkbox(property = isInfinite)
            }
            field("Slot capacity") {
                hiddenWhen(isInfinite)
                textfield(itemSlot.capacity) {
                    required()
                    requiredPositiveInteger()
                }
            }
        }
        hbox {
            spacing = 10.0
            button(if (isCreate) "Create" else "Save") {
                enableWhen(itemSlot.valid)
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
        if (isInfinite.value) {
            itemSlot.capacity.value = 0
        }
        runWithLoading { saveAction() } ui {
            it.peek {
                val baseMsg = "Cannot ${if (isCreate) "create" else "save"} the item slot!"
                errorAlert {
                    when (it) {
                        PSQLState.UNIQUE_VIOLATION -> "$baseMsg A slot with the same name exists"
                        else -> "$baseMsg $it"
                    }
                }
            }.onEmpty { runLater { close() } }
        }
    }

    protected abstract fun saveAction(): MaybePSQLError // This action is performed with loading
}

class CreateItemSlotModal : ItemSlotForm(true) {
    override fun saveAction(): MaybePSQLError =
        controller.createSlot(itemSlot)
}

class DetailItemSlotModal : ItemSlotForm(false) {
    override fun saveAction(): MaybePSQLError =
        controller.commit(itemSlot)
}
