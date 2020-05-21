package views

import tornadofx.*
import views.statistic.StatisticsView

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
            menu("Statistics") {
                item("Statistics") {
                    action {
                        center<StatisticsView>()
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
