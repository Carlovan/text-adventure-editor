package views.enemy

import controller.EnemyController
import controller.StatisticController
import ellipses
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailEnemyViewModel
import viewmodel.EnemyStatValueViewModel
import viewmodel.StatisticViewModel
import views.MasterView
import views.errorAlert
import views.runWithLoading
import views.ui

class DetailEnemyView : Fragment() {
    val controller: EnemyController by inject()
    val enemy: DetailEnemyViewModel by param()

    override val root = vbox {
        label("Enemy: ${enemy.name.value}")
        val esv = find<EnemyStatValueView>(EnemyStatValueView::enemy to enemy)
        esv.initData()
        hbox {
            add(esv)
        }
    }
}

class EnemyStatValueView : MasterView<EnemyStatValueViewModel>() {
    val controller: EnemyController by inject()
    val enemy: DetailEnemyViewModel by param()

    val enemyStatistics: ObservableList<EnemyStatValueViewModel> = observableListOf()

    override val root = vbox {
        label("Enemy statistics")
        add(createRoot(false))
    }

    override fun createDataTable(): TableView<EnemyStatValueViewModel> =
        tableview {
            items = enemyStatistics

            enableCellEditing()
            enableDirtyTracking()

            column("Statistic name", EnemyStatValueViewModel::statName)
            column("Value", EnemyStatValueViewModel::value).makeEditable()

            smartResize()
        }

    override fun newItem() {
        find<AddEnemyStatModal>(AddEnemyStatModal::enemy to enemy).openModal(block = true)
        updateData()
    }

    override fun deleteItem() {
        TODO("not implemented")
    }

    override fun saveTable() {
        TODO("not implemented")
    }

    fun initData() {
        enemyStatistics.clear()
        enemyStatistics.addAll(enemy.statistics)
    }

    private fun updateData() {
        runWithLoading { enemy.statistics } ui {
            enemyStatistics.clear()
            enemyStatistics.addAll(it)
        }
    }
}

class AddEnemyStatModal : Fragment("Add enemy statistic") {
    private val controller: EnemyController by inject()
    private val statController: StatisticController by inject()
    val enemy: DetailEnemyViewModel by param()
    private var newEnemyStatValue: EnemyStatValueViewModel by singleAssign()

    init {
        newEnemyStatValue = EnemyStatValueViewModel(enemy.item)
    }

    override val root = form {
        fieldset("Enemy statistic") {
            field("Statistic") {
                combobox<StatisticViewModel>(property = newEnemyStatValue.statisticViewModel) {
                    items = statController.statistics
                    cellFormat { text = it.name.value.ellipses(30) }
                    required()
                }
            }
            field("Value") {
                textfield(newEnemyStatValue.value).required()
            }
        }
        hbox {
            button("Add") {
                enableWhen(newEnemyStatValue.valid)
                alignment = Pos.BOTTOM_RIGHT
                action {
                    runWithLoading { controller.addEnemyStatistic(newEnemyStatValue) } ui {
                        it.peek {
                            errorAlert {
                                when (it) {
                                    PSQLState.UNIQUE_VIOLATION -> "This statistic is already assigned to this enemy!"
                                    PSQLState.CHECK_VIOLATION -> "Value must be a positive integer!"
                                    else -> null
                                }
                            }
                        }.onEmpty { runLater { close() } }
                    }
                }
            }
        }
    }
}