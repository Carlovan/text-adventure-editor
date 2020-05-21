package views

import controller.StepController
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailStepViewModel
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
            button("Details") {
                enableWhen(stepsTable.anySelected)
                action(::openDetails)
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

    private fun newStep() {
        find<CreateStepModal>().openModal(block = true)
        updateData()
    }

    private fun deleteStep() {
        runWithLoading { controller.deleteStep(stepsTable.selectionModel.selectedItem) } ui {
            updateData()
        }
    }

    private fun saveTable() {
        runWithLoading {
            with(stepsTable.editModel) {
                controller.commit(items.asSequence()
                    .filter { it.value.isDirty }
                    .map { it.key })
            }
        } ui {
            it.peek {
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
        runWithLoading { controller.getDetail(stepsTable.selectedItem!!) } ui {
            replaceWith(find<DetailStepView>(DetailStepView::step to it))
        }
    }

    override fun onDock() {
        updateData()
    }
}

class DetailStepView : Fragment() {
    private val controller: StepController by inject()
    val step: DetailStepViewModel by param()
    override val root = borderpane {
        paddingAll = 20.0
        center {
            vbox {
                spacing = 10.0
                label("Step number ${step.number.value}")
                textarea(step.text)
            }
        }
        bottom {
            hbox {
                spacing = 10.0
                button("Save") {
                    enableWhen(step.dirty)
                    action {
                        runWithLoading {
                            controller.commit(step)
                        }
                    }
                }
                button("Back") {
                    action{
                        replaceWith<StepsMasterView>()
                    }
                }
            }
        }
    }
}