package views

import controller.StepController
import javafx.scene.control.TableView
import tornadofx.*
import viewmodel.StepViewModel

class StepsMasterView : View("Steps") {
    private val controller: StepController by inject()

    var stepsTable: TableView<StepViewModel> by singleAssign()
    var steps = observableListOf<StepViewModel>()

    override val root = borderpane {
        left = vbox {
            spacing = 10.0
            button("Save") {
                action {
                    with(stepsTable.editModel) {
                        controller.commit(items
                                        .asSequence()
                                        .filter { it.value.isDirty }
                                        .map { it.key })
                        commit()
                    }
                }
            }
            button("Cancel") {
                action {
                    stepsTable.editModel.rollback()
                }
            }
            button("New") {
                action {
                    find<CreateStepModal>().openModal(block = true)
                    updateData()
                }
            }
            button("Delete") {
                action {
                    runWithLoading { controller.deleteStep(stepsTable.selectionModel.selectedItem) } ui {
                        updateData()
                    }
                }
            }
        }

        stepsTable = tableview {
            items = steps

            enableCellEditing()
            enableDirtyTracking()

            column("Number", StepViewModel::number).makeEditable()
            column("Description", StepViewModel::text).makeEditable()

            smartResize()
        }
        center = stepsTable
    }

    private fun updateData() {
        runWithLoading { controller.steps } ui {
            steps.clear()
            steps.addAll(it)
        }
    }

    override fun onDock() {
        updateData()
    }
}