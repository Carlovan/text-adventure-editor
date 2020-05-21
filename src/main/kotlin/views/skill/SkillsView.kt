package views.skill

import controller.SkillController
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.SkillViewModel
import views.anySelected
import views.errorAlert
import views.isDirty
import views.runWithLoading

class SkillsView : View("Skills") {
    private val controller: SkillController by inject()

    var skillsTable: TableView<SkillViewModel> by singleAssign()
    var skills = observableListOf<SkillViewModel>()

    override val root = borderpane {
        skillsTable = tableview {
            items = skills

            enableCellEditing()
            enableDirtyTracking()

            column("Name", SkillViewModel::name).makeEditable()
            column("Type", SkillViewModel::type)
            smartResize()
        }

        left = vbox {
            spacing = 10.0
            paddingAll = 10.0
            button("New") {
                maxWidth = Double.MAX_VALUE
                action {
                    find<CreateSkillModal>().openModal(block = true)
                    updateData()
                }
            }
            button("Delete") {
                maxWidth = Double.MAX_VALUE
                enableWhen { skillsTable.anySelected }
                action {
                    runWithLoading { controller.deleteSkill(skillsTable.selectionModel.selectedItem) } ui {
                        it.peek {errorAlert { when(it) {
                            PSQLState.FOREIGN_KEY_VIOLATION -> "Cannot delete this Skill, it is related to other entities"
                            else -> null
                        } } }.onEmpty { updateData() }
                    }
                }
            }
            separator()
            button("Save") {
                maxWidth = Double.MAX_VALUE
                enableWhen(skillsTable.isDirty)
                action {
                    runWithLoading {
                        with(skillsTable.editModel) {
                            controller.commit(items
                                .asSequence()
                                .filter { it.value.isDirty }
                                .map { it.key })
                        }
                    } ui {
                        it.peek {
                            errorAlert { when(it) {
                                PSQLState.UNIQUE_VIOLATION -> "Skill name is not unique!"
                                else -> null } }
                        }.onEmpty { skillsTable.editModel.commit() }
                    }
                }
            }
            button("Discard") {
                maxWidth = Double.MAX_VALUE
                enableWhen(skillsTable.isDirty)
                action {
                    skillsTable.editModel.rollback()
                }
            }
            button("Detail") {
                maxWidth = Double.MAX_VALUE
                enableWhen(skillsTable.anySelected)
                action {
                    runWithLoading { controller.getDetail(skillsTable.selectedItem!!) } ui {
                        replaceWith(find<DetailSkillView>(DetailSkillView::skill to it))
                    }
                }
            }
        }

        center = skillsTable
    }

    private fun updateData() {
        runWithLoading { controller.skills } ui {
            skills.clear()
            skills.addAll(it)
            skillsTable.sort()
        }
    }

    override fun onDock() {
        updateData()
    }
}