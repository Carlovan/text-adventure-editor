package views

import controller.AdventureController
import javafx.beans.value.ObservableBooleanValue
import javafx.collections.ObservableList
import javafx.scene.control.ComboBox
import tornadofx.*
import viewmodel.AdventureViewModel

val <T> ComboBox<T>.anySelected: ObservableBooleanValue
get() = this.selectionModel.selectedItemProperty().isNotNull

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
                            goToMainView(adventureCombo.selectedItem)
                        }
                    }
                    button("Delete") {
                        enableWhen { adventureCombo.anySelected }
                        action {
                            adventureCombo.selectedItem?.let {
                                controller.deleteAdventure(it)
                                updateData()
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
                            val newAdventureFull = controller.createAdventure(newAdventure)
                            with(newAdventure) {
                                item = null
                                rollback()
                                clearDecorators() // Remove validation
                            }
                            goToMainView(newAdventureFull)
                        }
                    }
                }
            }
        }
    }

    private fun goToMainView(contextAdventure: AdventureViewModel?) {
        if (contextAdventure != null) {
            controller.contextAdventure = contextAdventure
            replaceWith<MainView>()
        }
    }

    private fun updateData() {
        with(adventures) {
            clear()
            addAll(controller.adventures)
        }
        adventureCombo.valueProperty().set(controller.contextAdventure)
    }

    override fun onDock() {
        updateData()
    }
}