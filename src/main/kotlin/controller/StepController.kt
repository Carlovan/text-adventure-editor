package controller

import javafx.collections.ObservableList
import model.Step
import model.Steps
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.asObservable
import viewmodel.StepViewModel
import viewmodel.fromViewModel

class StepController : ControllerWithContextAdventure() {
    val steps : ObservableList<StepViewModel> by cachedProperty {
        transaction {
            Step.find { Steps.adventure eq contextAdventure!!.item.id }
                .map {
                    StepViewModel().apply {
                        item = it
                    }
                }.asObservable()
        }
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
                fromViewModel(step)
            }
        }
        invalidateProperty(::steps)
    }

    fun deleteStep(step: StepViewModel) {
        transaction {
            step.item.delete()
        }
        invalidateProperty(::steps)
    }
}