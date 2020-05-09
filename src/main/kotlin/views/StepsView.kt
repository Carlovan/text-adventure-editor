package views

import controller.StepController
import javafx.collections.ObservableList
import tornadofx.*
import viewmodel.StepViewModel

class StepsMasterView : View("Steps") {
    val controller: StepController by inject()

    var stepsTable: TableViewEditModel<StepViewModel> by singleAssign()
    var steps: ObservableList<StepViewModel> by singleAssign()

    override val root = borderpane {
        steps = controller.steps

        left = vbox {
            button("Commit") {
                action {
                    controller.commit(stepsTable.items
                                                .asSequence()
                                                .filter { it.value.isDirty }
                                                .map { it.key })
                    stepsTable.commit()
                }
            }
            button("Cancel") {
                action {
                    stepsTable.rollback()
                }
            }
        }

        center = tableview<StepViewModel> {
            stepsTable = editModel
            items = steps

            enableCellEditing()
            enableDirtyTracking()

            column("Number", StepViewModel::number).makeEditable()
            column("Description", StepViewModel::text).makeEditable()

            smartResize()
        }
    }
}