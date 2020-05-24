package views.skill

import controller.SkillController
import javafx.geometry.Pos
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.SkillViewModel
import views.MasterView
import views.errorAlert
import views.runWithLoading
import views.ui

class SkillsView : MasterView<SkillViewModel>("Skills") {
    private val controller: SkillController by inject()

    var skills = observableListOf<SkillViewModel>()

    override val root = createRoot()

    override fun createDataTable(): TableView<SkillViewModel> =
        tableview {
            items = skills

            enableCellEditing()
            enableDirtyTracking()

            column("Name", SkillViewModel::name).makeEditable()
            column("Type", SkillViewModel::type)
            smartResize()
        }

    private fun updateData() {
        runWithLoading { controller.skills } ui {
            skills.clear()
            skills.addAll(it)
            dataTable.sort()
        }
    }

    override fun newItem() {
        find<CreateSkillModal>().openModal(block = true)
        updateData()
    }

    override fun deleteItem() {
        runWithLoading { controller.deleteSkill(dataTable.selectionModel.selectedItem) } ui {
            it.peek {
                errorAlert {
                    when (it) {
                        PSQLState.FOREIGN_KEY_VIOLATION -> "Cannot delete this Skill, it is related to other entities"
                        else -> null
                    }
                }
            }.onEmpty { updateData() }
        }
    }

    override fun saveTable() {
        runWithLoading {
            with(dataTable.editModel) {
                controller.commit(items
                    .asSequence()
                    .filter { it.value.isDirty }
                    .map { it.key })
            }
        } ui {
            it.peek {
                errorAlert {
                    when (it) {
                        PSQLState.UNIQUE_VIOLATION -> "Skill name is not unique!"
                        else -> null
                    }
                }
            }.onEmpty { dataTable.editModel.commit() }
        }
    }

    override fun openDetail() {
        runWithLoading { controller.getDetail(dataTable.selectedItem!!) } ui {
            replaceWith(find<DetailSkillView>(DetailSkillView::skill to it))
        }
    }

    override fun onDock() {
        updateData()
    }
}

class CreateSkillModal : Fragment("Create skill") {
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
                    runWithLoading { controller.createSkill(newSkill) } ui {
                        it.peek {
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