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
    override val root = borderpane()

    var contentView: View = find(StepsMasterView::class)

    init {
        with(root) {
            top = menubar {
                menu("Adventure") {
                    item("Change adventure") {
                        action {
                            replaceWith<SelectAdventureView>()
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

            center<StepsMasterView>()
        }
    }

    override fun onDock() {
        contentView.onDock()
    }
}
