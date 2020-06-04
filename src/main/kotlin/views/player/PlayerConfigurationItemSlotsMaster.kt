package views.player

import controller.PlayerConfigurationController
import javafx.beans.property.SimpleObjectProperty
import onEmpty
import org.jetbrains.exposed.sql.transactions.transaction
import peek
import tornadofx.*
import viewmodel.DetailPlayerConfigurationViewModel
import viewmodel.ItemSlotViewModel
import viewmodel.PlayerAvailableSlotViewModel
import views.errorAlert
import views.runWithLoading
import views.ui

class PlayerConfigurationItemSlotsMaster : Fragment("Item slots") {
    val controller: PlayerConfigurationController by inject()

    val configuration: DetailPlayerConfigurationViewModel by param()
    private val itemSlots = observableListOf<PlayerAvailableSlotViewModel>()
    private val selectedSlot = SimpleObjectProperty<PlayerAvailableSlotViewModel>()

    override val root = borderpane {
        paddingAll = 20.0
        center {
            tableview(itemSlots) {
                selectedSlot.bind(selectionModel.selectedItemProperty())
                column("Name", PlayerAvailableSlotViewModel::name)
                column<PlayerAvailableSlotViewModel, String>("Slot type") { it.value.itemSlot.value.name }
                column<PlayerAvailableSlotViewModel, Int>("Slot capacity") { it.value.itemSlot.value.capacity }
            }
        }
        left {
            vbox {
                paddingRight = 10.0
                spacing = 10.0
                button("Add") {
                    maxWidth = Double.MAX_VALUE
                    action(::add)
                }
                button("Remove") {
                    enableWhen(selectedSlot.isNotNull)
                    maxWidth = Double.MAX_VALUE
                    action(::remove)
                }
            }
        }
    }

    private fun updateData() {
        runWithLoading { transaction { configuration.slots } } ui {
            itemSlots.clear()
            itemSlots.addAll(it)
        }
    }

    private fun add() {
        find<AddAvailableSlotModal>(AddAvailableSlotModal::configuration to configuration).openModal(block = true)
        updateData()
    }

    private fun remove() {
        runWithLoading { controller.removeSlot(configuration, selectedSlot.value) } ui {
            it.peek { errorAlert { null } }
                .onEmpty { updateData() }
        }
    }

    override fun onDock() {
        updateData()
    }
}