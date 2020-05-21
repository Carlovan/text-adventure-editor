package views.step

import controller.ChoiceController
import controller.StepController
import javafx.beans.property.SimpleObjectProperty
import org.jetbrains.exposed.sql.transactions.transaction
import peek
import tornadofx.*
import viewmodel.ChoiceViewModel
import viewmodel.DetailStepViewModel
import views.anySelected
import views.errorAlert
import views.runWithLoading

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
                                maxWidth = Double.MAX_VALUE
                                action(::addChoice)
                            }
                            button("Delete") {
                                enableWhen(choiceList.anySelected)
                                maxWidth = Double.MAX_VALUE
                                action(::deleteChoice)
                            }
                            button("Detail") {
                                enableWhen(choiceList.anySelected)
                                maxWidth = Double.MAX_VALUE
                                action(::openDetail)
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

    private fun openDetail() {

    }

    override fun onDock() {
        updateData()
    }
}