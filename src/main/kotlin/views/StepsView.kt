package views

import controller.StepController
import javafx.collections.ObservableList
import javafx.scene.control.TableView
import tornadofx.*
import viewmodel.StepViewModel

class StepsMasterView : View("Steps") {
    val controller: StepController by inject()

    var stepsTable: TableView<StepViewModel> by singleAssign()
    var stepsTableEditModel: TableViewEditModel<StepViewModel> by singleAssign()
    var steps: ObservableList<StepViewModel> = emptyList<StepViewModel>().asObservable()

    override val root = borderpane {
        steps = controller.steps

        left = vbox {
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
        }

        stepsTable =tableview<StepViewModel> {
            stepsTableEditModel = editModel
            items = steps

            enableCellEditing()
            enableDirtyTracking()

            column("Number", StepViewModel::number).makeEditable()
            column("Description", StepViewModel::text).makeEditable()

            smartResize()
        }

        center = stepsTable
    }

    override fun onDock() {
        steps = controller.steps
        stepsTable.items = steps
    }

    override fun onUndock() {
        println("On undock")
    }
}