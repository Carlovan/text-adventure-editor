package views.step

import controller.ChoiceController
import controller.StepController
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TableView
import onEmpty
import org.jetbrains.exposed.sql.transactions.transaction
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.ChoiceViewModel
import viewmodel.DetailStepViewModel
import viewmodel.StepViewModel
import views.anySelected
import views.errorAlert
import views.isDirty
import views.runWithLoading

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
        runWithLoading { controller.deleteStep(stepsTable.selectionModel.selectedItem) } ui {error ->
            error.peek {
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
    private val choiceController: ChoiceController by inject()

    val step: DetailStepViewModel by param()
    private val choices = observableListOf<ChoiceViewModel>()
    private val selectedChoice = SimpleObjectProperty<ChoiceViewModel>()

    override val root = borderpane {
        paddingAll = 20.0
        center {
            vbox {
                spacing = 10.0
                label("Step number ${step.number.value}")
                textarea(step.text)
                label("Choices:")
                borderpane {
                    val choiceList = listview(choices) {
                            bindSelected(selectedChoice)
                            placeholder = label("No choices")
                            cellFormat { text = "${it.text.value} (to step ${it.stepTo.value.number})" }
                        }
                    center = choiceList
                    left {
                        vbox {
                            paddingRight = 10.0
                            spacing = 10.0
                            button("Add") {
                                action(::addChoice)
                            }
                            button("Delete") {
                                enableWhen(choiceList.anySelected)
                                action(::deleteChoice)
                            }
                        }
                    }
                }
            }
        }
        bottom {
            hbox {
                spacing = 10.0
                button("Save") {
                    enableWhen(step.dirty)
                    action(::save)
                }
                button("Back") {
                    action(::back)
                }
            }
        }
    }

    private fun updateData() {
        runWithLoading { transaction { step.choices } } ui {
            choices.clear()
            choices.addAll(it)
        }
    }

    private fun save() {
        runWithLoading {
            controller.commit(step)
        } ui {
            it.peek {
                errorAlert { "An error occurred" }
            }
        }
    }

    private fun back() {
        replaceWith<StepsMasterView>()
    }

    private fun addChoice() {
        find<CreateChoiceModal>(CreateChoiceModal::fromStep to step).openModal(block = true)
        updateData()
    }

    private fun deleteChoice() {
        runWithLoading { choiceController.deleteChoice(selectedChoice.value) } ui {
            it.peek {
                errorAlert { "Cannot delete choice" }
            }
            updateData()
        }
    }

    override fun onDock() {
        updateData()
    }
}