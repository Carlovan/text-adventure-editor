package views

import tornadofx.*

class MainView: View() {
    override val root = borderpane {
        top = menubar {
            menu("Adventure") {
                item("Change adventure") {
                    action {
                        replaceWith<SelectAdventureView>()
                    }
                }
                item("Steps") {
                    action {
                        center<StepsMasterView>()
                    }
                }
            }
        }
    }

    override fun onDock() {
        root.center = null;
        root.center<StepsMasterView>()
    }
}
