package views.step

import controller.ChoiceController
import controller.StepController
import ellipses
import model.Step
import onEmpty
import peek
import tornadofx.*
import viewmodel.ChoiceViewModel
import viewmodel.DetailStepViewModel
import views.errorAlert
import views.runWithLoading

class CreateChoiceModal : Fragment() {
    private val controller: ChoiceController by inject()
    private val stepController: StepController by inject()
    val fromStep: DetailStepViewModel by param()
    private val nextSteps = observableListOf<Step>()
    private val newChoice = ChoiceViewModel()

    override val root = form {
        fieldset("Create choice") {
            field("Choice text") {
                textarea(newChoice.text).required()
            }
            field("Next step") {
                combobox(property = newChoice.stepTo) {
                    items = nextSteps
                    cellFormat { text = "[${it.number}] ${it.text.ellipses(30)}" }
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
        runWithLoading {
            controller.createChoice(newChoice, fromStep)
        } ui {
            it.peek {
                errorAlert { "Cannot create the choice" }
            }.onEmpty { runLater { close() } }
        }
    }

    override fun onDock() {
        runLater {
            runWithLoading { stepController.steps } ui { steps ->
                nextSteps.addAll(steps.map { it.item }.sortedBy { it.number })
            }
        }
    }
}