package views

import controller.AdventureController
import javafx.collections.ObservableList
import javafx.scene.control.ComboBox
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.AdventureViewModel

class SelectAdventureView : View(){
    private val controller: AdventureController by inject()

    private val adventures: ObservableList<AdventureViewModel> = emptyList<AdventureViewModel>().toObservable()
    private var adventureCombo: ComboBox<AdventureViewModel> by singleAssign()
    private var newAdventure = AdventureViewModel()

    init {
        setInScope(AdventureViewModel(), FX.defaultScope) // So that it can be accessed anywhere by injection
    }

    override val root = borderpane {
        center = form {
            fieldset("Select adventure") {
                field("Adventure") {
                    adventureCombo = combobox {
                        items = adventures
                    }
                }
                hbox {
                    spacing = 10.0
                    button("Set") {
                        enableWhen { adventureCombo.anySelected }
                        action {
                            controller.contextAdventure = adventureCombo.selectedItem
                            goToMainView()
                        }
                    }
                    button("Delete") {
                        enableWhen { adventureCombo.anySelected }
                        action {
                            adventureCombo.selectedItem?.let {
                                runWithLoading { controller.deleteAdventure(it) } ui {
                                    it.peek {errorAlert { when(it) {
                                            PSQLState.FOREIGN_KEY_VIOLATION -> "Cannot delete this Adventure, it is related to other entities"
                                            else -> null
                                        } } }
                                        .onEmpty { updateData() }
                                }
                            }
                        }
                    }
                }
            }
            fieldset("Create new adventure") {
                field("Name") {
                    textfield(newAdventure.name).required()
                }
                button("Create") {
                    enableWhen(newAdventure.valid)
                    action {
                        newAdventure.commit {
                            runWithLoading {
                                controller.createAdventure(newAdventure, true)
                                    .peek {
                                        errorAlert {
                                            when (it) {
                                                PSQLState.UNIQUE_VIOLATION -> "An adventure with this name already exists"
                                                else -> null
                                            }
                                        }
                                    }
                                    .onEmpty {
                                        with(newAdventure) {
                                            item = null
                                            rollback()
                                            clearDecorators() // Remove validation
                                        }
                                        goToMainView()
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun goToMainView() {
        replaceWith<MainView>()
    }

    private fun updateData() {
        runWithLoading { controller.adventures } ui {
            with(adventures) {
                clear()
                addAll(it)
            }
            adventureCombo.valueProperty().set(controller.contextAdventure)
        }
    }

    override fun onDock() {
        updateData()
    }
}