package views.step

import controller.StepController
import javafx.geometry.Pos
import model.Step
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.StepViewModel
import views.errorAlert
import views.runWithLoadingAsync

class CreateStepModal: Fragment() {
    private val controller: StepController by inject()
    private val newStep = StepViewModel()
    private val existingNumbers = controller.cachedSteps.map { it.number.value }.toSortedSet()

    private fun getMissingNumbers(max: Int = existingNumbers.lastOrNull() ?: 0): Collection<Int> {
        return (Step.MIN_NUMBER until max)
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
                    text = (getMissingNumbers().firstOrNull() ?: existingNumbers.size + 1).toString()
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
                    runWithLoadingAsync {
                        controller.createStep(newStep)
                            .peek {
                                errorAlert {
                                    when (it) {
                                        PSQLState.UNIQUE_VIOLATION -> "Step number is not unique!"
                                        else -> null
                                    }
                                }
                            }.onEmpty {
                                runLater { close() } // I don't know why runLater is required...
                            }
                    }
                }
            }
        }
    }
}