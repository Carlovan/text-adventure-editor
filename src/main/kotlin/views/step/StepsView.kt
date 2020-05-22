package views.step

import controller.StepController
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.StepViewModel
import views.anySelected
import views.errorAlert
import views.isDirty
import views.runWithLoadingAsync

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
            column("Text", StepViewModel::text)
            column("Steps to", StepViewModel::stepsTo)

            sortOrder.add(numberColumn)

            smartResize()
        }

        left = vbox {
            spacing = 10.0
            paddingAll = 10.0
            button("New") {
                maxWidth = Double.MAX_VALUE
                action(::newStep)
            }
            button("Delete") {
                maxWidth = Double.MAX_VALUE
                enableWhen { stepsTable.anySelected }
                action(::deleteStep)
            }
            separator()
            button("Save") {
                maxWidth = Double.MAX_VALUE
                enableWhen(stepsTable.isDirty)
                action(::saveTable)
            }
            button("Discard") {
                maxWidth = Double.MAX_VALUE
                enableWhen(stepsTable.isDirty)
                action(::discardTable)
            }
            separator()
            button("Detail") {
                maxWidth = Double.MAX_VALUE
                enableWhen(stepsTable.anySelected)
                action(::openDetails)
            }
        }

        center = stepsTable
    }

    private fun updateData() {
        runWithLoadingAsync {
            steps.clear()
            steps.addAll(controller.steps)
            stepsTable.sort()
        }
    }

    private fun newStep() {
        find<CreateStepModal>().openModal(block = true)
        updateData()
    }

    private fun deleteStep() {
        runWithLoadingAsync {
            controller.deleteStep(stepsTable.selectionModel.selectedItem)
                .peek {
                    errorAlert {
                        when (it) {
                            PSQLState.FOREIGN_KEY_VIOLATION -> "Cannot delete this step, it is related to other entities"
                            else -> null
                        }
                    }
                }.onEmpty {
                    updateData()
                }
        }
    }

    private fun saveTable() {
        runWithLoadingAsync {
            with(stepsTable.editModel) {
                controller.commit(items.asSequence()
                    .filter { it.value.isDirty }
                    .map { it.key })
            }
                .peek {
                    errorAlert {
                        when (it) {
                            PSQLState.UNIQUE_VIOLATION -> "Step number is not unique!"
                            else -> null
                        }
                    }
                }.onEmpty { stepsTable.editModel.commit() }
        }
    }

    private fun discardTable() {
        stepsTable.editModel.rollback()
    }

    private fun openDetails() {
        runWithLoadingAsync {
            replaceWith(find<DetailStepView>(DetailStepView::step to controller.getDetail(stepsTable.selectedItem!!)))
        }
    }

    override fun onDock() {
        updateData()
    }
}