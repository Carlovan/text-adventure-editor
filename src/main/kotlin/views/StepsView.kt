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
        stepsTable = tableview {
            items = steps

            enableCellEditing()
            enableDirtyTracking()

            val numberColumn = column("Number", StepViewModel::number).makeEditable()
            column("Description", StepViewModel::text).makeEditable()

            sortOrder.add(numberColumn)

            smartResize()
        }

        left = vbox {
            spacing = 10.0
            paddingAll = 10.0
            button("New") {
                maxWidth = Double.MAX_VALUE
                action {
                    find<CreateStepModal>().openModal(block = true)
                    updateData()
                }
            }
            button("Delete") {
                maxWidth = Double.MAX_VALUE
                enableWhen { stepsTable.anySelected }
                action {
                    runWithLoading { controller.deleteStep(stepsTable.selectionModel.selectedItem) } ui {
                        updateData()
                    }
                }
            }
            separator()
            button("Save") {
                maxWidth = Double.MAX_VALUE
                enableWhen(stepsTable.isDirty)
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
            button("Discard") {
                maxWidth = Double.MAX_VALUE
                enableWhen(stepsTable.isDirty)
                action {
                    stepsTable.editModel.rollback()
                }
            }
        }

        center = stepsTable
    }

    private fun updateData() {
        runWithLoading { controller.steps } ui {
            steps.clear()
            steps.addAll(it)
            stepsTable.sort()
        }
    }

    override fun onDock() {
        updateData()
    }
}