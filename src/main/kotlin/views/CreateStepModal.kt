package views

import controller.StepController
import javafx.geometry.Pos
import model.Step
import tornadofx.*
import viewmodel.StepViewModel

class CreateStepModal: Fragment() {
    private val controller: StepController by inject()
    private val newStep = StepViewModel()
    private val existingNumbers = controller.steps.map { it.number.value }.toSortedSet()

    private fun getMissingNumbers(max: Int = existingNumbers.lastOrNull() ?: 0): Collection<Int> {
        return (1 until max)
            .filterNot(existingNumbers::contains)
    }

    override val root = form {
        fieldset("Create step") {
            field("Step number") {
                textfield(newStep.number) {
                    validator { text ->
                        val num = text?.toIntOrNull() ?: 0
                        val missing = getMissingNumbers(num)
                        when {
                            num < Step.MIN_NUMBER -> error("Step number must be greater or equal to ${Step.MIN_NUMBER}")
                            existingNumbers.contains(num) -> error("This step number already exists")
                            missing.isNotEmpty() -> info("Be careful, steps ${missing.joinToString(", ")} ${if (missing.size == 1) "is" else "are"} missing")
                            else -> null
                        }
                    }
                    filterInput { it.controlNewText.isInt() }
                    promptText = "Step number"
                    text = (getMissingNumbers().lastOrNull() ?: existingNumbers.size + 1).toString()
                }
            }
            field("Step text") {
                textarea(newStep.text).required()
            }
        }
        hbox {
            button("Create") {
                enableWhen(newStep.valid)
                alignment = Pos.BOTTOM_RIGHT
                action {
                    controller.createStep(newStep)
                    close()
                }
            }
        }
    }
}