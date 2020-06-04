package views.player

import controller.PlayerConfigurationController
import javafx.scene.control.TabPane
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailPlayerConfigurationViewModel
import views.errorAlert
import views.requiredPositiveInteger
import views.runWithLoading
import views.ui

class DetailPlayerConfigurationView : Fragment("Edit player configuration") {
    val controller: PlayerConfigurationController by inject()
    val configuration: DetailPlayerConfigurationViewModel by param()

    override val root = borderpane {
        paddingAll = 20.0
        heading = "Player configuration \"$configuration.name\""

        center {
            vbox {
                spacing = 10.0
                form {
                    fieldset {
                        field("Name") {
                            textfield(configuration.name) {
                                required()
                            }
                        }
                        field("Max skills number") {
                            textfield(configuration.maxSkills) {
                                required()
                                requiredPositiveInteger()
                            }
                        }
                    }
                }
                tabpane {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                    tab(find<PlayerConfigurationItemSlotsMaster>(
                        PlayerConfigurationItemSlotsMaster::configuration to configuration
                    ))
                    tab(find<PlayerConfigurationSkillsMaster>(
                        PlayerConfigurationSkillsMaster::configuration to configuration
                    )) {

                    }
                    tab(find<PlayerConfigurationStatisticsMaster>(
                        PlayerConfigurationItemSlotsMaster::configuration to configuration
                    ))
                }
            }
        }
        bottom {
            hbox {
                spacing = 10.0
                button("Save") {
                    enableWhen(configuration.valid and configuration.dirty)
                    action(::save)
                }
                button("Back") {
                    action {
                        replaceWith<PlayerConfigurationsView>()
                    }
                }
            }
        }
    }

    private fun save() {
        runWithLoading { controller.commit(configuration) } ui {
            it.peek { error ->
                errorAlert {
                    when (error) {
                        PSQLState.UNIQUE_VIOLATION -> "The name must be unique"
                        PSQLState.CHECK_VIOLATION -> "The max number of skills must be positive"
                        else -> null
                    }
                }
            }
        }
    }
}