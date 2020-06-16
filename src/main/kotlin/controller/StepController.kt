package controller

import javafx.collections.ObservableList
import model.EnemiesSteps
import model.Step
import model.Steps
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.safeTransaction
import tornadofx.asObservable
import tornadofx.observableListOf
import viewmodel.DetailStepViewModel
import viewmodel.EnemyStepViewModel
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

    fun createStep(step: StepViewModel) =
        safeTransaction {
            Step.new {
                adventure = contextAdventure!!.item
                fromViewModel(step)
            }
        }

    fun deleteStep(step: StepViewModel) =
        safeTransaction {
            step.item.delete()
        }

    fun getDetail(master: StepViewModel): DetailStepViewModel =
        transaction { DetailStepViewModel(master.item) }

    fun addEnemy(enemyViewModel: EnemyStepViewModel) =
        safeTransaction {
            EnemiesSteps.insert {
                it[step] = enemyViewModel.step.id
                it[enemy] = enemyViewModel.enemyViewModel.value.item.id
                it[quantity] = enemyViewModel.quantity.value
            }
        }

    fun deleteEnemy(enemyViewModel: EnemyStepViewModel) =
        safeTransaction {
            EnemiesSteps.deleteWhere {
                EnemiesSteps.enemy eq enemyViewModel.item.id and(EnemiesSteps.step eq enemyViewModel.step.id)
            }
        }
}