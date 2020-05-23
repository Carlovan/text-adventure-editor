package views.statistic

import controller.StatisticController
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.StatisticViewModel
import views.MasterView
import views.errorAlert
import views.runWithLoading
import views.ui

class StatisticsView : MasterView<StatisticViewModel>("Statistics") {
    private val controller: StatisticController by inject()

    var stats = observableListOf<StatisticViewModel>()

    override val root = createRoot(false)

    override fun createDataTable(): TableView<StatisticViewModel> =
        tableview {
            items = stats

            enableCellEditing()
            enableDirtyTracking()

            column("Name", StatisticViewModel::name).makeEditable()
            smartResize()
        }

    private fun updateData() {
        runWithLoading { controller.statistics } ui {
            stats.clear()
            stats.addAll(it)
            dataTable.sort()
        }
    }

    override fun newItem() {
        find<CreateStatisticModal>().openModal(block = true)
        updateData()
    }

    override fun deleteItem() {
        runWithLoading { controller.deleteStatistic(dataTable.selectionModel.selectedItem) } ui {
            it.peek {
                errorAlert {
                    when (it) {
                        PSQLState.FOREIGN_KEY_VIOLATION -> "Cannot delete this Statistic, it is related to other entities"
                        else -> null
                    }
                }
            }.onEmpty { updateData() }
        }
    }

    override fun saveTable() {
        runWithLoading {
            with(dataTable.editModel) {
                controller.commit(items
                    .asSequence()
                    .filter { it.value.isDirty }
                    .map { it.key })
            }
        } ui {
            it.peek {
                errorAlert {
                    when (it) {
                        PSQLState.UNIQUE_VIOLATION -> "Statistic name is not unique!"
                        else -> null
                    }
                }
            }.onEmpty { dataTable.editModel.commit() }
        }
    }

    override fun onDock() {
        updateData()
    }
}