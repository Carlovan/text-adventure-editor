package views

import controller.AdventureController
import javafx.collections.ObservableList
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import tornadofx.*
import viewmodel.AdventureViewModel
import java.util.*

class SelectAdventureView : View(){
    val controller: AdventureController by inject()

    var adventures: ObservableList<AdventureViewModel> = emptyList<AdventureViewModel>().toObservable()
    var adventureCombo: ComboBox<AdventureViewModel> by singleAssign()
    var newAdventure: AdventureViewModel = AdventureViewModel()
    var newAdventureNameTextField: TextField by singleAssign()

    init {
        setInScope(AdventureViewModel(), FX.defaultScope)
    }

    override val root = borderpane {
        center = form {
            fieldset("Select adventure") {
                field("Adventure") {
                    adventureCombo = combobox<AdventureViewModel> {
                        items = adventures
                    }
                }
                button("Set") {
                    action {
                        controller.contextAdventure = adventureCombo.selectedItem
                        if (controller.contextAdventure != null) goToMainView()
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
                            controller.contextAdventure = newAdventureFull
                        }
                        goToMainView()
                    }
                }
            }
        }
    }

    fun goToMainView() {
        replaceWith<MainView>()
    }

    override fun onDock() {
        adventures.clear()
        adventures.addAll(controller.adventures)
        adventureCombo.selectionModel.select(controller.contextAdventure)
    }
}