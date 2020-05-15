package views

import controller.StepController
import javafx.event.EventHandler
import tornadofx.*
import viewmodel.StepViewModel

class StepsMasterView : View("Steps") {
    private val controller: StepController by inject()

    var stepsTableEditModel: TableViewEditModel<StepViewModel> by singleAssign()
    var steps = observableListOf<StepViewModel>()

    override val root = borderpane {
        left = vbox {
            spacing = 10.0
            button("Save") {
                action {
                    controller.commit(stepsTableEditModel.items
                                                .asSequence()
                                                .filter { it.value.isDirty }
                                                .map { it.key })
                    stepsTableEditModel.commit()
                }
            }
            button("Cancel") {
                action {
                    stepsTableEditModel.rollback()
                }
            }
            button("New") {
                action {
                    find<CreateStepModal>().openModal(block = true)
                    updateData()
                }
            }
        }

        center = tableview<StepViewModel> {
            stepsTableEditModel = editModel
            items = steps

            enableCellEditing()
            enableDirtyTracking()

            column("Number", StepViewModel::number).makeEditable()
            column("Description", StepViewModel::text).makeEditable()

            smartResize()
        }
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