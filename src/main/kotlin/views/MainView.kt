package views

import tornadofx.*
import views.skill.SkillsView
import views.statistic.StatisticsView

class MainView: View("Text adventure editor") {
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
            menu("Player") {
                item("Statistics") {
                    action {
                        center<StatisticsView>()
                    }
                }
                item("Skills") {
                    action {
                        center<SkillsView>()
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
