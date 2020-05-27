package views.loot

import controller.LootController
import ellipses
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import viewmodel.LootViewModel
import views.errorAlert
import views.runWithLoading
import views.ui

/**
 * Modal used to select a loot to be set somewhere.
 * Use the `selectedLoot` property to get the selected loot
 */
class SelectLootModal : Fragment("Select loot") {
    private val controller: LootController by inject()

    /**
     * This is used as return value. Can be set as parameter from TornadoFX or can be accessed from the object of [SelectLootModal] itself.
     */
    val selectedLoot by param(SimpleObjectProperty<LootViewModel>())
    private val oldValue = selectedLoot.value
    private val loots = observableListOf<LootViewModel>()

    override val root = form {
        fieldset("Add items") {
            vbox {
                spacing = 10.0
                label("Select item")
                combobox(selectedLoot, loots) { // TODO initial item is not correctly formatted
                    cellFormat {
                        text = it.desc.value.ellipses(30)
                    }
                }
            }

        }
        hbox {
            spacing = 10.0
            button("Select") {
                enableWhen(selectedLoot.isNotNull)
                action(::selectLoot)
            }
            button("Cancel") {
                action(::cancel)
            }
        }
    }

    private fun updateData() {
        runWithLoading { controller.loots } ui {
            loots.clear()
            loots.addAll(it)
        }
    }

    private fun selectLoot() {
        if (selectedLoot.isNull.value) {
            errorAlert { "There is nothing selected!" }
        } else {
            close()
        }
    }

    private fun cancel() {
        selectedLoot.value = oldValue
        close()
    }

    override fun onDock() {
        runLater {
            updateData()
        }
    }
}