package views.step

import controller.ChoiceController
import controller.StepController
import ellipses
import onEmpty
import peek
import tornadofx.*
import viewmodel.ChoiceViewModel
import viewmodel.DetailStepViewModel
import viewmodel.StepViewModel
import views.errorAlert
import views.runWithLoading
import views.ui

class CreateChoiceModal : Fragment() {
    private val controller: ChoiceController by inject()
    private val stepController: StepController by inject()
    val fromStep: DetailStepViewModel by param()
    private val nextSteps = observableListOf<StepViewModel>()
    private val newChoice = ChoiceViewModel()

    override val root = form {
        fieldset("Create choice") {
            field("Choice text") {
                textarea(newChoice.text).required()
            }
            field("Next step") {
                combobox(property = newChoice.stepTo) {
                    items = nextSteps
                    cellFormat { text = "[${it.number.value}] ${it.text.value.ellipses(30)}" }
                    required()
                }
            }
        }
        hbox {
            spacing = 10.0
            button("Create") {
                enableWhen(newChoice.valid)
                action(::save)
            }
            button("Cancel") {
                action {
                    close()
                }
            }
        }
    }

    private fun save() {
        runWithLoading { controller.createChoice(newChoice, fromStep) } ui {
            it.peek {
                errorAlert { "Cannot create the choice" }
            }.onEmpty { runLater { close() } }
        }
    }

    override fun onDock() {
        runLater {
            runWithLoading { stepController.steps } ui {
                nextSteps.addAll(it.sortedBy { step -> step.number.value })
            }
        }
    }
}