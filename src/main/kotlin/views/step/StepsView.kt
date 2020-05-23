package views.step

import controller.StepController
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.StepViewModel
import views.*

class StepsMasterView : MasterView<StepViewModel>("Steps") {
    private val controller: StepController by inject()

    var steps = observableListOf<StepViewModel>()

    override val root = createRoot()

    override fun createDataTable(): TableView<StepViewModel> =
        tableview {
            items = steps

            enableCellEditing()
            enableDirtyTracking()

            val numberColumn = column("Number", StepViewModel::number).makeEditable()
            column("Text", StepViewModel::text)
            column("Steps to", StepViewModel::stepsTo)

            sortOrder.add(numberColumn)

            smartResize()
        }


    private fun updateData() {
        runWithLoading { controller.steps } ui {
            steps.clear()
            steps.addAll(it)
            dataTable.sort()
        }
    }

    override fun newItem() {
        find<CreateStepModal>().openModal(block = true)
        updateData()
    }

    override fun deleteItem() {
        runWithLoading { controller.deleteStep(dataTable.selectionModel.selectedItem) } ui {
            it.peek {
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

    override fun saveTable() {
        runWithLoading {
            with(dataTable.editModel) {
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
            }.onEmpty { dataTable.editModel.commit() }
        }
    }

    override fun openDetail() {
        runWithLoading { controller.getDetail(dataTable.selectedItem!!) } ui {
            replaceWith(find<DetailStepView>(DetailStepView::step to it))
        }
    }

    override fun onDock() {
        updateData()
    }
}