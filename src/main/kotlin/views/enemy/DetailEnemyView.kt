package views.enemy

import controller.EnemyController
import controller.LootController
import controller.StatisticController
import ellipses
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailEnemyViewModel
import viewmodel.EnemyStatValueViewModel
import viewmodel.LootViewModel
import viewmodel.StatisticViewModel
import views.MasterView
import views.errorAlert
import views.loot.SelectLootModal
import views.runWithLoading
import views.ui

class DetailEnemyView : Fragment() {
    val controller: EnemyController by inject()
    val lootController: LootController by inject()
    val enemy: DetailEnemyViewModel by param()

    val enemyStatistics: ObservableList<EnemyStatValueViewModel> = observableListOf()
    val loots: ObservableList<LootViewModel?> = observableListOf()
    var lootsCombo: ComboBox<LootViewModel> by singleAssign()

    override val root = vbox {
        label("Enemy: ${enemy.name.value}")
        hbox {
            add(EnemyStatValueView())
        }
        hbox {
            spacing = 10.0
            alignment = Pos.CENTER_LEFT
            label("Enemy loot:")
            label(stringBinding(enemy.loot) { enemy.loot.value?.desc?.value ?: "(no loot)" })
            button("Change") {
                action {
                    find<SelectLootModal>(SelectLootModal::selectedObject to enemy.loot).openModal(block = true)
                    runWithLoading { controller.updateLoot(enemy) }
                }
            }
            button("Remove") {
                enableWhen(enemy.loot.isNotNull)
                action {
                    enemy.loot.value = null
                    runWithLoading { controller.updateLoot(enemy) }
                }
            }
        }
    }

    private fun updateData() {
        runWithLoading { enemy.statistics } ui {
            enemyStatistics.clear()
            enemyStatistics.addAll(it)

            runWithLoading { lootController.loots } ui {
                loots.clear()
                loots.add(LootViewModel(null))
                loots.addAll(it)
            }
        }
    }

    override fun onDock() {
        updateData()
    }

    inner class EnemyStatValueView : MasterView<EnemyStatValueViewModel>() {
        override val root = vbox {
            label("Enemy statistics")
            add(createRoot(false))
        }

        override fun createDataTable(): TableView<EnemyStatValueViewModel> =
            tableview(enemyStatistics) {
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
            runWithLoading { controller.deleteEnemyStat(dataTable.selectionModel.selectedItem) } ui {
                it.peek {
                    errorAlert {
                        when (it) {
                            PSQLState.FOREIGN_KEY_VIOLATION -> "Cannot delete this enemy statistic, it is related to other entities"
                            else -> null
                        }
                    }
                }.onEmpty { updateData() }
            }
        }

        override fun saveTable() {
            runWithLoading {
                with(dataTable.editModel) {
                    controller.commitEnemyStats(items
                        .asSequence()
                        .filter { it.value.isDirty }
                        .map { it.key })
                }
            } ui {
                it.peek {
                    errorAlert {
                        when (it) {
                            PSQLState.CHECK_VIOLATION -> "Statistic value must be a positive integer!"
                            else -> null
                        }
                    }
                }.onEmpty { dataTable.editModel.commit() }
            }
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