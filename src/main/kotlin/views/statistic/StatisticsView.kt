package views.statistic

import controller.StatisticController
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.StatisticViewModel
import views.anySelected
import views.errorAlert
import views.isDirty
import views.runWithLoadingAsync

class StatisticsView : View("Statistics") {
    private val controller: StatisticController by inject()

    var statsTable: TableView<StatisticViewModel> by singleAssign()
    var stats = observableListOf<StatisticViewModel>()

    override val root = borderpane {
        statsTable = tableview {
            items = stats

            enableCellEditing()
            enableDirtyTracking()

            column("Name", StatisticViewModel::name).makeEditable()
            smartResize()
        }

        left = vbox {
            spacing = 10.0
            paddingAll = 10.0
            button("New") {
                maxWidth = Double.MAX_VALUE
                action {
                    find<CreateStatisticModal>().openModal(block = true)
                    updateData()
                }
            }
            button("Delete") {
                maxWidth = Double.MAX_VALUE
                enableWhen { statsTable.anySelected }
                action {
                    runWithLoadingAsync {
                        controller.deleteStatistic(statsTable.selectionModel.selectedItem)
                            .peek {
                                errorAlert {
                                    when (it) {
                                        PSQLState.FOREIGN_KEY_VIOLATION -> "Cannot delete this Statistic, it is related to other entities"
                                        else -> null
                                    }
                                }
                            }.onEmpty { updateData() }
                    }
                }
            }
            separator()
            button("Save") {
                maxWidth = Double.MAX_VALUE
                enableWhen(statsTable.isDirty)
                action {
                    runWithLoadingAsync {
                        with(statsTable.editModel) {
                            controller.commit(items
                                .asSequence()
                                .filter { it.value.isDirty }
                                .map { it.key })
                        }
                            .peek {
                                errorAlert {
                                    when (it) {
                                        PSQLState.UNIQUE_VIOLATION -> "Statistic name is not unique!"
                                        else -> null
                                    }
                                }
                            }.onEmpty { statsTable.editModel.commit() }
                    }
                }
            }
            button("Discard") {
                maxWidth = Double.MAX_VALUE
                enableWhen(statsTable.isDirty)
                action {
                    statsTable.editModel.rollback()
                }
            }
        }

        center = statsTable
    }

    private fun updateData() {
        runWithLoadingAsync {
            stats.clear()
            stats.addAll(controller.statistics)
            statsTable.sort()
        }
    }

    override fun onDock() {
        updateData()
    }
}