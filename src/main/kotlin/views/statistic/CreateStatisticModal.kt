package views.statistic

import controller.StatisticController
import javafx.geometry.Pos
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.StatisticViewModel
import views.errorAlert
import views.runWithLoadingAsync

class CreateStatisticModal: Fragment("Create statistic") {
    private val controller: StatisticController by inject()
    private val newStat = StatisticViewModel()

    override val root = form {
        fieldset("Statistic") {
            field("Name") {
                textfield(newStat.name).required()
            }
        }
        hbox {
            button("Create") {
                enableWhen(newStat.valid)
                alignment = Pos.BOTTOM_RIGHT
                action {
                    runWithLoadingAsync {
                        controller.createStatistic(newStat)
                            .peek {
                                errorAlert {
                                    when (it) {
                                        PSQLState.UNIQUE_VIOLATION -> "Statistic name is not unique!"
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