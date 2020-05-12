package controller

import javafx.collections.ObservableList
import model.Step
import model.Steps
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.asObservable
import viewmodel.StepViewModel

class StepController : ControllerWithContextAdventure() {
    val steps : ObservableList<StepViewModel>
        get() = transaction {
                    Step.find { Steps.adventure eq contextAdventure!!.item.id }.sortedBy { it.number }
                        .map {
                            StepViewModel().apply {
                                item = it
                            }
                        }.asObservable()
                }

    fun commit(changes: Sequence<StepViewModel>) {
        transaction {
            changes.forEach{ it.commit() }
        }
    }

    fun createStep(step: StepViewModel) {
        transaction {
            Step.new {
                adventure = contextAdventure!!.item
                step.commitTo(this)
            }
        }
    }
}