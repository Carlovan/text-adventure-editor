package views

import controller.AdventureController
import javafx.collections.ObservableList
import javafx.scene.control.ComboBox
import tornadofx.*
import viewmodel.AdventureViewModel
import viewmodel.StepViewModel
import java.util.*

class MainView: View() {
    val l = label("Hello!")

    override val root = borderpane()

    init {
        with(root) {
            top = menubar {
                menu("Adventure") {
                    item("Change adventure") {
                        action {
                            root.center<SelectAdventureView>()
                        }
                    }
                    item("Steps") {
                        action {
                            root.center<StepsMasterView>()
                        }
                    }
                }
            }

            center<SelectAdventureView>()
        }
    }
}

class SelectAdventureView : View(){
    val controller: AdventureController by inject()
    var adventureCombo: ComboBox<AdventureViewModel> by singleAssign()

    override val root = vbox {
        adventureCombo = combobox {
            items = controller.adventures
        }
        button("Set!") {
            action {
                val adventure = adventureCombo.selectedItem ?: AdventureViewModel()
                setInScope(adventure, FX.defaultScope)
            }
        }
    }
}
