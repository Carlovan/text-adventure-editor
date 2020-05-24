package views.skill

import controller.SkillController
import javafx.scene.control.TableView
import onEmpty
import org.jetbrains.exposed.sql.transactions.transaction
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailSkillViewModel
import viewmodel.ItemSkillActivationViewModel
import viewmodel.StatSkillModifierViewModel
import views.*

class DetailSkillView : Fragment() {
    private val controller: SkillController by inject()
    val skill: DetailSkillViewModel by param()
    var itemSkillActivationsTable: TableView<ItemSkillActivationViewModel> by singleAssign()
    val itemSkillActivations = observableListOf<ItemSkillActivationViewModel>()

    var statSkillModifiersTable: TableView<StatSkillModifierViewModel> by singleAssign()
    val statSkillModifiers = observableListOf<StatSkillModifierViewModel>()

    override val root = borderpane {
        paddingAll = 20.0
        center {
            vbox {
                spacing = 10.0
                label("Skill ${skill.name.value}")
                hbox {
                    vbox {
                        label("Item activations:")
                        borderpane {
                            itemSkillActivationsTable = tableview {
                                items = itemSkillActivations

                                enableCellEditing()
                                enableDirtyTracking()

                                column("Item", ItemSkillActivationViewModel::itemName)
                                column(
                                    "Quantity required",
                                    ItemSkillActivationViewModel::quantityRequired
                                ).makeEditable()

                                smartResize()
                            }
                            left = vbox {
                                spacing = 10.0
                                paddingAll = 10.0
                                button("New") {
                                    maxWidth = Double.MAX_VALUE
                                    action {
                                        addItemSkillActivation()
                                    }
                                }
                                button("Delete") {
                                    maxWidth = Double.MAX_VALUE
                                    enableWhen { itemSkillActivationsTable.anySelected }
                                    action {
                                        controller.deleteItemActivation(itemSkillActivationsTable.selectedItem)
                                            .peek {
                                                errorAlert { "Error occoured while deleting!" }
                                            }.onEmpty { updateData() }
                                    }
                                }
                                separator()
                                button("Save") {
                                    maxWidth = Double.MAX_VALUE
                                    enableWhen(itemSkillActivationsTable.isDirty)
                                    action {
                                        with(itemSkillActivationsTable.editModel) {
                                            controller.saveItemSkillActivations(
                                                items.asSequence().filter { it.value.isDirty }.map { it.key }
                                            ).peek {
                                                errorAlert {
                                                    when(it) {
                                                        PSQLState.CHECK_VIOLATION -> "Quantity required must be a positive number!"
                                                        else -> null
                                                    }
                                                }
                                            }.onEmpty { commit() }
                                        }
                                    }
                                }
                                button("Discard") {
                                    maxWidth = Double.MAX_VALUE
                                    enableWhen(itemSkillActivationsTable.isDirty)
                                    action {
                                        itemSkillActivationsTable.editModel.rollback()
                                    }
                                }
                            }
                            center = itemSkillActivationsTable
                        }
                    }
                    vbox {
                        label("Statistic modifiers:")
                        borderpane {
                            statSkillModifiersTable = tableview {
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
                            left = vbox {
                                spacing = 10.0
                                paddingAll = 10.0
                                button("New") {
                                    maxWidth = Double.MAX_VALUE
                                    action {
                                        addStatSkillModifier()
                                    }
                                }
                                button("Delete") {
                                    maxWidth = Double.MAX_VALUE
                                    enableWhen { statSkillModifiersTable.anySelected }
                                    action {
                                        controller.deleteStatModifier(statSkillModifiersTable.selectedItem)
                                                  .peek {
                                                      errorAlert { "Error occoured while deleting!" }
                                                  }.onEmpty { updateData() }
                                    }
                                }
                                separator()
                                button("Save") {
                                    maxWidth = Double.MAX_VALUE
                                    enableWhen(statSkillModifiersTable.isDirty)
                                    action {
                                        with(statSkillModifiersTable.editModel) {
                                            controller.saveStatSkillModifiers(
                                                items.asSequence().filter { it.value.isDirty }.map { it.key }
                                            ).peek {
                                                errorAlert { null }
                                            }.onEmpty { commit() }
                                        }
                                    }

                                }
                                button("Discard") {
                                    maxWidth = Double.MAX_VALUE
                                    enableWhen(statSkillModifiersTable.isDirty)
                                    action {
                                        statSkillModifiersTable.editModel.rollback()
                                    }
                                }
                            }
                            center = statSkillModifiersTable
                        }
                    }
                }
            }
        }
        bottom {
            hbox {
                button("Back") {
                    action {
                        replaceWith<SkillsView>()
                    }
                }
            }
        }
    }

    private fun updateData() {
        runWithLoading { transaction { skill.itemSkillActivations } } ui {
            itemSkillActivations.clear()
            itemSkillActivations.addAll(it)

            runWithLoading { transaction { skill.statSkillModifiers } } ui {
                statSkillModifiers.clear()
                statSkillModifiers.addAll(it)
            }
        }
    }

    private fun back() {
        replaceWith<SkillsView>()
    }

    private fun addItemSkillActivation() {
        find<CreateItemSkillActivationModal>(CreateItemSkillActivationModal::skill to skill).openModal(block = true)
        updateData()
    }

    private fun addStatSkillModifier() {
        find<CreateStatSkillModifierModal>(CreateStatSkillModifierModal::skill to skill).openModal(block = true)
        updateData()
    }

    override fun onDock() {
        updateData()
    }
}