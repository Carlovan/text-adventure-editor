package views.player

import controller.PlayerConfigurationController
import javafx.beans.property.SimpleObjectProperty
import onEmpty
import org.jetbrains.exposed.sql.transactions.transaction
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailPlayerConfigurationViewModel
import viewmodel.SkillViewModel
import views.errorAlert
import views.runWithLoading
import views.skill.SelectSkillModal
import views.ui

class PlayerConfigurationSkillsMaster : Fragment("Skills") {
    val controller: PlayerConfigurationController by inject()

    private val skills = observableListOf<SkillViewModel>()
    private val selectedSkill = SimpleObjectProperty<SkillViewModel>()

    val configuration: DetailPlayerConfigurationViewModel by param()

    override val root = borderpane {
        paddingAll = 20.0
        center {
            tableview(skills) {
                selectedSkill.bind(selectionModel.selectedItemProperty())

                column("Skill name", SkillViewModel::name)
                column("Type", SkillViewModel::type)

                smartResize()
            }
        }
        left {
            vbox {
                paddingRight = 10.0
                spacing = 10.0
                button("Add") {
                    maxWidth = Double.MAX_VALUE
                    action(::addSkill)
                }
                button("Remove") {
                    enableWhen(selectedSkill.isNotNull)
                    maxWidth = Double.MAX_VALUE
                    action(::removeSkill)
                }
            }
        }
    }

    private fun updateData() {
        runWithLoading { transaction { configuration.skills } } ui {
            skills.clear()
            skills.addAll(it)
        }
    }

    private fun removeSkill() {
        runWithLoading { controller.removeSkill(configuration, selectedSkill.value) } ui {
            it.peek { errorAlert { "Cannot remove this skill" } }
                .onEmpty { updateData() }
        }
    }

    private fun addSkill() {
        val modal = find<SelectSkillModal>()
        modal.openModal(block = true)
        modal.selectedObject.value?.let { newSkill ->
            runWithLoading { controller.addSkill(configuration, newSkill) } ui {
                it.peek { error ->
                    errorAlert {
                        when (error) {
                            PSQLState.UNIQUE_VIOLATION -> "This skill is already set in this configuration"
                            else -> null
                        }
                    }
                }.onEmpty { updateData() }
            }
        }
    }

    override fun onTabSelected() {
        updateData()
    }
}