package views.item

import controller.ItemController
import controller.StatisticController
import javafx.scene.control.TextFormatter
import javafx.util.converter.IntegerStringConverter
import javafx.util.converter.NumberStringConverter
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.ItemStatisticViewModel
import viewmodel.ItemViewModel
import viewmodel.StatisticViewModel
import views.IntegerStringConverterWithDefault
import views.errorAlert
import views.runWithLoading
import views.ui

class AddItemStatisticModal : Fragment("Add statistic modifier") {
    private val statisticController: StatisticController by inject()
    private val controller: ItemController by inject()

    val item: ItemViewModel by param()
    private val newStatisticModifier = ItemStatisticViewModel()

    private val statistics = observableListOf<StatisticViewModel>()

    override val root = form {
        fieldset("Add statistic modifier") {
            field("Select statistic") {
                combobox(newStatisticModifier.statistic, statistics) {
                    required()
                    cellFormat {
                        text = it.name.value
                    }
                }
            }
            field("Value") {
                textfield(newStatisticModifier.value, IntegerStringConverterWithDefault(0)) {
                    required()
                    validator {
                        if (it != null && it.isNotEmpty() && !it.isInt()) {
                            error("An integer value is required")
                        } else {
                            null
                        }
                    }
                }
            }
        }
        hbox {
            spacing = 10.0
            button("Add") {
                enableWhen(newStatisticModifier.valid)
                action(::addStatisticModifier)
            }
            button("Close") {
                action {
                    close()
                }
            }
        }
    }

    private fun updateData() {
        runWithLoading { statisticController.statistics } ui {
            statistics.clear()
            statistics.addAll(it)
        }
    }

    private fun addStatisticModifier() {
        runWithLoading { controller.addStatisticModifier(item, newStatisticModifier) } ui {
            it.peek { error ->
                errorAlert {
                    when (error) {
                        PSQLState.UNIQUE_VIOLATION -> "This statistic is already modified by this item, change the value in the table"
                        else -> null
                    }
                }
            }.onEmpty { runLater { close() } }
        }
    }

    override fun onDock() {
        runLater {
            updateData()
        }
    }
}