package views.step

import controller.EnemyController
import controller.StepController
import ellipses
import javafx.collections.ObservableList
import javafx.geometry.Pos
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailStepViewModel
import viewmodel.EnemyStepViewModel
import viewmodel.EnemyViewModel
import views.errorAlert
import views.requiredPositiveInteger
import views.runWithLoading
import views.ui

class AddEnemyStepModal : Fragment("Add enemy to step") {
    val controller: StepController by inject()
    val enemyController: EnemyController by inject()

    val step: DetailStepViewModel by param()

    var enemyViewModels: ObservableList<EnemyViewModel> = observableListOf()
    var newEnemyStepViewModel: EnemyStepViewModel by singleAssign()

    init {
        newEnemyStepViewModel = EnemyStepViewModel(step.item)
    }

    override val root = form {
        fieldset("New enemy in step") {
            field("Enemy") {
                combobox(newEnemyStepViewModel.enemyViewModel, enemyViewModels) {
                    cellFormat { text = it.name.value.ellipses(30) }
                    required()
                }
            }
            field("Quantity") {
                textfield(newEnemyStepViewModel.quantity) {
                    required()
                    requiredPositiveInteger()
                }
            }
        }
        hbox {
            button("Create") {
                enableWhen(newEnemyStepViewModel.valid)
                alignment = Pos.BOTTOM_RIGHT
                action {
                    runWithLoading { controller.addEnemy(newEnemyStepViewModel) } ui {
                        it.peek {
                            errorAlert {
                                when (it) {
                                    PSQLState.UNIQUE_VIOLATION -> "This enemy is already in this step!"
                                    PSQLState.FOREIGN_KEY_VIOLATION -> "This enemy is already in this step!"
                                    PSQLState.CHECK_VIOLATION -> "Quantity must be a positive number"
                                    else -> null
                                }
                            }
                        }.onEmpty { runLater { close() } }
                    }
                }
            }
        }
    }

    override fun onDock() {
        runWithLoading { enemyController.enemies } ui {
            enemyViewModels.clear()
            enemyViewModels.addAll(it)
        }
    }

}