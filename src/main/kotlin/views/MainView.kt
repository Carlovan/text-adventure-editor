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
        with(root) {
            center = null // So that child onDock is called
            center<StepsMasterView>()
        }
    }
}
