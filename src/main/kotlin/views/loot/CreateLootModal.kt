package views.loot

import controller.LootController
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.LootViewModel
import views.errorAlert
import views.runWithLoading
import views.ui

class CreateLootModal : Fragment("Create loot") {
    private val controller: LootController by inject()
    private val newLoot = LootViewModel()

    override val root = form {
        fieldset("Create loot") {
            field("Loot description") {
                textarea(property = newLoot.desc).required()
            }
        }
        hbox {
            button("Create") {
                enableWhen(newLoot.valid)
                action {
                    runWithLoading { controller.createLoot(newLoot) } ui {
                        it.peek {
                            errorAlert {
                                when (it) {
                                    PSQLState.UNIQUE_VIOLATION -> "Loot description is not unique!"
                                    else -> null
                                }
                            }
                        }.onEmpty { runLater { close() } }
                    }
                }
            }
            button("Cancel") {
                action {
                    close()
                }
            }
        }
    }
}