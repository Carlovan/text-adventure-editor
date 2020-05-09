package views

import controller.AdventureController
import javafx.collections.ObservableList
import javafx.scene.control.ComboBox
import tornadofx.*
import viewmodel.AdventureViewModel
import viewmodel.StepViewModel
import java.util.*
import kotlin.reflect.KClass

class MainView: View() {
    val l = label("Hello!")

    override val root = borderpane()

    var contentView: View = find(SelectAdventureView::class)

    init {
        with(root) {
            top = menubar {
                menu("Adventure") {
                    item("Change adventure") {
                        action {
                            contentView.replaceWith<SelectAdventureView>()
                            contentView = find(SelectAdventureView::class)
                        }
                    }
                    item("Steps") {
                        action {
                            contentView.replaceWith<StepsMasterView>()
                            contentView = find(StepsMasterView::class)
                        }
                    }
                }
            }

            center<SelectAdventureView>()
        }
    }
}

class SelectAdventureView : View(){
    val adventure: AdventureViewModel by inject()

    val controller: AdventureController by inject()
    var adventureCombo: ComboBox<AdventureViewModel> by singleAssign()

    init {
        setInScope(controller.adventures[0], FX.defaultScope)
    }

    override val root = vbox {
        adventureCombo = combobox {
            items = controller.adventures
            selectionModel.select(0)
        }
        button("Set!") {
            action {
                val adventureInner = adventureCombo.selectedItem!!.item
                adventure.item = adventureInner
            }
        }
    }
}
