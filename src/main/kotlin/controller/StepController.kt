package controller

import javafx.collections.ObservableList
import model.Step
import model.Steps
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.safeTransaction
import tornadofx.asObservable
import tornadofx.observableListOf
import viewmodel.DetailStepViewModel
import viewmodel.StepViewModel
import viewmodel.fromViewModel

class StepController : ControllerWithContextAdventure() {
    var cachedSteps = observableListOf<StepViewModel>()
        private set

    val steps : ObservableList<StepViewModel> get() =
        transaction {
            Step.find { Steps.adventure eq contextAdventure!!.item.id }
                .map {
                    StepViewModel(it)
                }.asObservable().also { cachedSteps = it }
        }

    fun commit(changes: Sequence<StepViewModel>) =
        safeTransaction {
            changes.forEach { it.saveData() }
        }

    fun commit(change: DetailStepViewModel) =
        safeTransaction {
            change.saveData()
        }

    fun createStep(step: StepViewModel) {
        transaction {
            Step.new {
                adventure = contextAdventure!!.item
                fromViewModel(step)
            }
        }
    }

    fun deleteStep(step: StepViewModel) {
        transaction {
            step.item.delete()
        }
    }

    fun getDetail(master: StepViewModel): DetailStepViewModel {
        return transaction { DetailStepViewModel(master.item) }
    }
}