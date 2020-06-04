package views.player

import controller.PlayerConfigurationController
import controller.StatisticController
import ellipses
import javafx.beans.property.SimpleObjectProperty
import onEmpty
import org.jetbrains.exposed.sql.transactions.transaction
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailPlayerConfigurationViewModel
import viewmodel.SimplePlayerConfigurationViewModel
import viewmodel.StatisticViewModel
import views.SelectObjectModal
import views.errorAlert
import views.runWithLoading
import views.ui

class PlayerConfigurationStatisticsMaster : Fragment("Available statistics") {
    val controller: PlayerConfigurationController by inject()

    val configuration: DetailPlayerConfigurationViewModel by param()
    private val statistics = observableListOf<StatisticViewModel>()
    private val selectedStat = SimpleObjectProperty<StatisticViewModel>()

    override val root = borderpane {
        paddingAll = 20.0
        center {
            tableview(statistics) {
                selectedStat.bind(selectionModel.selectedItemProperty())
                column("Name", StatisticViewModel::name)
            }
        }
        left {
            vbox {
                paddingRight = 10.0
                spacing = 10.0
                button("Add") {
                    maxWidth = Double.MAX_VALUE
                    action(::add)
                }
                button("Remove") {
                    enableWhen(selectedStat.isNotNull)
                    maxWidth = Double.MAX_VALUE
                    action(::remove)
                }
            }
        }
    }

    private fun updateData() {
        runWithLoading { transaction { configuration.statistics } } ui {
            statistics.clear()
            statistics.addAll(it)
        }
    }

    private fun add() {
        val modal = find<AddAvailableStatisticModal>()
        modal.openModal(block = true)
        if (modal.selectedObject.isNotNull.value) {
            runWithLoading { controller.addStatistic(configuration, modal.selectedObject.value) } ui {
                it.peek {
                    errorAlert {
                        when (it) {
                            PSQLState.UNIQUE_VIOLATION -> "This statistic is already added to the configuration!"
                            else -> null
                        }
                    }
                }.onEmpty { updateData() }
            }
        }
    }

    private fun remove() {
        runWithLoading { controller.removeStatistic(configuration, selectedStat.value) } ui {
            it.peek { errorAlert { null } }
                .onEmpty { updateData() }
        }
    }

    override fun onTabSelected() {
        updateData()
    }
}

class AddAvailableStatisticModal : SelectObjectModal<StatisticViewModel>("Select statistic") {
    private val controller: StatisticController by inject()

    override fun getData() = controller.statistics
    override fun cellFormatter(obj: StatisticViewModel) = obj.name.value.ellipses(30)
}