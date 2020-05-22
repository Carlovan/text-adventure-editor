package views.skill

import controller.SkillController
import javafx.geometry.Pos
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.SkillViewModel
import views.errorAlert
import views.runWithLoadingAsync

class CreateSkillModal: Fragment("Create skill") {
    private val controller: SkillController by inject()
    private val newSkill = SkillViewModel()

    override val root = form {
        fieldset("Skill") {
            field("Name") {
                textfield(newSkill.name).required()
            }
        }
        hbox {
            button("Create") {
                enableWhen(newSkill.valid)
                alignment = Pos.BOTTOM_RIGHT
                action {
                    runWithLoadingAsync {
                        controller.createSkill(newSkill)
                            .peek {
                                errorAlert {
                                    when (it) {
                                        PSQLState.UNIQUE_VIOLATION -> "Skill name is not unique!"
                                        else -> null
                                    }
                                }
                            }.onEmpty { runLater { close() } }
                    }
                }
            }
        }
    }
}