package views

import controller.AdventureController
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import tornadofx.*
import viewmodel.AdventureViewModel

class SelectAdventureView : View(){
    val controller: AdventureController by inject()

    var adventureCombo: ComboBox<AdventureViewModel> by singleAssign()
    var newAdventure: AdventureViewModel = AdventureViewModel()

    init {
        setInScope(AdventureViewModel(), FX.defaultScope)
    }

    override val root = borderpane {
        center = form {
            fieldset("Select adventure") {
                field("Adventure") {
                    adventureCombo = combobox<AdventureViewModel> {
                        items = controller.adventures
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
                            adventureCombo.items.add(newAdventureFull)
                            adventureCombo.selectionModel.select(newAdventureFull)
                            newAdventure = AdventureViewModel()
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
}