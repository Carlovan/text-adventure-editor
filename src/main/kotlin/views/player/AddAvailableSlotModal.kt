package views.player

import controller.ItemSlotController
import controller.PlayerConfigurationController
import tornadofx.*
import viewmodel.ItemSlotViewModel
import viewmodel.PlayerAvailableSlotViewModel
import viewmodel.SimplePlayerConfigurationViewModel
import views.runWithLoading
import views.ui

class AddAvailableSlotModal : Fragment("Select item slot") {
    private val controller: PlayerConfigurationController by inject()
    private val slotsController: ItemSlotController by inject()
    private val newPlayerSlot = PlayerAvailableSlotViewModel()

    private val slots = observableListOf<ItemSlotViewModel>()
    val configuration: SimplePlayerConfigurationViewModel by param()

    override val root = vbox {
        form {
            fieldset {
                field("Name") {
                    textfield(newPlayerSlot.name) {
                        required()
                    }
                }
                field("Slot") {
                    combobox(newPlayerSlot.itemSlot, slots) {
                        required()
                        cellFormat {
                            text = "${it.name.value} [${it.capacity.value}]"
                        }
                    }
                }
            }
        }
        hbox {
            button("Add") {
                enableWhen(newPlayerSlot.valid)
                action(::addSlot)
            }
            button("Cancel") {
                action {
                    close()
                }
            }
        }
    }

    private fun updateData() {
        runWithLoading { slotsController.itemSlots } ui {
            slots.clear()
            slots.addAll(it)
        }
    }

    private fun addSlot() {
        runWithLoading { controller.addSlot(configuration, newPlayerSlot) } ui {
            close()
        }
    }

    override fun onDock() {
        updateData()
    }
}