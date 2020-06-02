package views.player

import controller.PlayerConfigurationController
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.PlayerConfigurationViewModel
import views.*

class CreatePlayerConfigurationModal : Fragment("Create player configuration") {
    private val controller: PlayerConfigurationController by inject()
    private val newConfiguration = PlayerConfigurationViewModel()

    override val root = form {
        paddingAll = 20.0
        fieldset {
            field("Name") {
                textfield(newConfiguration.name) {
                    required()
                    maxLength(16)
                }
            }
            field("Max number of skills") {
                textfield(newConfiguration.maxSkills) {
                    required()
                    requiredPositiveInteger()
                }
            }
        }
        hbox {
            spacing = 10.0
            button("Create") {
                enableWhen(newConfiguration.valid)
                action(::create)
            }
            button("Cancel") {
                action { close() }
            }
        }
    }

    fun create() {
        runWithLoading {
            controller.createConfiguration(newConfiguration)
        } ui {
            it.peek {
                errorAlert {
                    when (it) {
                        PSQLState.UNIQUE_VIOLATION -> "A configuration with this name already exists"
                        else -> null
                    }
                }
            }.onEmpty {
                runLater { close() }
            }
        }
    }
}