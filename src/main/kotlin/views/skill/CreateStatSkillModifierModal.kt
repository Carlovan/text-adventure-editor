package views.skill

import controller.SkillController
import controller.StatisticController
import ellipses
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailSkillViewModel
import viewmodel.StatSkillModifierViewModel
import viewmodel.StatisticViewModel
import views.errorAlert
import views.runWithLoading
import views.ui

class CreateStatSkillModifierModal: Fragment("Create statistic skill modifier") {
    private val controller: SkillController by inject()
    val statController: StatisticController by inject()
    val skill: DetailSkillViewModel by param()

    var statViewModels: ObservableList<StatisticViewModel> = observableListOf()
    var statsCombobox: ComboBox<StatisticViewModel> by singleAssign()
    var newStatSkillModifier: StatSkillModifierViewModel by singleAssign()

    init {
        newStatSkillModifier = StatSkillModifierViewModel(skill.item)
    }

    override val root = form {
        fieldset("New statistic modifier") {
            field("Statistic") {
                statsCombobox = combobox(property = newStatSkillModifier.statViewModel) {
                    items = statViewModels
                    cellFormat { text =it.name.value.ellipses(30)}
                    required()
                }
            }
            field("Value modifier") {
                textfield(newStatSkillModifier.valueMod).required()
            }
        }
        hbox {
            button("Create") {
                enableWhen(newStatSkillModifier.valid)
                alignment = Pos.BOTTOM_RIGHT
                action {
                    runWithLoading { controller.createStatSkillModifier(newStatSkillModifier) } ui {
                        it.peek {
                            errorAlert {
                                when (it) {
                                    PSQLState.UNIQUE_VIOLATION -> "This statistic is already modified by this skill!"
                                    PSQLState.FOREIGN_KEY_VIOLATION -> "This statistic is already modified by this skill!"
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
        runWithLoading { statController.statistics } ui {
            statViewModels.clear()
            statViewModels.addAll(it)
        }
    }
}