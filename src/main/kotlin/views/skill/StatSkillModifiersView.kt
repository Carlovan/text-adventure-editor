package views.skill

import controller.SkillController
import controller.StatisticController
import ellipses
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ComboBox
import javafx.scene.control.TableView
import onEmpty
import org.jetbrains.exposed.sql.transactions.transaction
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailSkillViewModel
import viewmodel.StatSkillModifierViewModel
import viewmodel.StatisticViewModel
import views.MasterView
import views.errorAlert
import views.runWithLoading
import views.ui

class StatSkillModifiersView : MasterView<StatSkillModifierViewModel>("Statistic modifiers") {
    val controller: SkillController by inject()
    val skill: DetailSkillViewModel by param()

    val statSkillModifiers = observableListOf<StatSkillModifierViewModel>()

    override val root = vbox {
        label("Statistic modifiers")
        add(createRoot(false))
    }

    override fun createDataTable(): TableView<StatSkillModifierViewModel> =
        tableview {
            items = statSkillModifiers

            enableCellEditing()
            enableDirtyTracking()

            column("Statistic", StatSkillModifierViewModel::statName)
            column(
                "Value modifier",
                StatSkillModifierViewModel::valueMod
            ).makeEditable()

            smartResize()
        }

    fun initData() {
        statSkillModifiers.clear()
        statSkillModifiers.addAll(skill.statSkillModifiers)
    }

    private fun updateData() {
        runWithLoading { skill.statSkillModifiers } ui {
            statSkillModifiers.clear()
            statSkillModifiers.addAll(it)
        }
    }

    override fun newItem() {
        find<CreateStatSkillModifierModal>(CreateStatSkillModifierModal::skill to skill).openModal(block = true)
        updateData()
    }

    override fun deleteItem() {
        controller.deleteStatModifier(dataTable.selectedItem)
            .peek {
                errorAlert { "Error occurred while deleting!" }
            }.onEmpty { updateData() }
    }

    override fun saveTable() {
        with(dataTable.editModel) {
            controller.saveStatSkillModifiers(
                items.asSequence().filter { it.value.isDirty }.map { it.key }
            ).peek {
                errorAlert { "Error occurred while saving!" }
            }.onEmpty { commit() }
        }
    }
}

class CreateStatSkillModifierModal : Fragment("Create statistic skill modifier") {
    private val controller: SkillController by inject()
    val statController: StatisticController by inject()
    val skill: DetailSkillViewModel by param()

    var statViewModels: ObservableList<StatisticViewModel> = observableListOf()
    var newStatSkillModifier: StatSkillModifierViewModel by singleAssign()

    init {
        newStatSkillModifier = StatSkillModifierViewModel(skill.item)
    }

    override val root = form {
        fieldset("New statistic modifier") {
            field("Statistic") {
                combobox(property = newStatSkillModifier.statViewModel) {
                    items = statViewModels
                    cellFormat { text = it.name.value.ellipses(30) }
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