package views.step

import controller.ChoiceController
import controller.StepController
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import org.jetbrains.exposed.sql.transactions.transaction
import peek
import tornadofx.*
import viewmodel.ChoiceViewModel
import viewmodel.DetailStepViewModel
import views.errorAlert
import views.flowgridpane
import views.loot.SelectLootModal
import views.runWithLoading
import views.ui

typealias ConstraintViewModel = String

class DetailStepView : Fragment() {
    private val controller: StepController by inject()
    private val choiceController: ChoiceController by inject()

    val step: DetailStepViewModel by param()
    private val choices = observableListOf<ChoiceViewModel>()
    private val selectedChoice = SimpleObjectProperty<ChoiceViewModel>()

    private val selectedConstraint = SimpleObjectProperty<ConstraintViewModel>()

    override val root = borderpane {
        paddingAll = 20.0
        center {
            vbox {
                spacing = 10.0
                label("Step number ${step.number.value}")
                textarea(step.text)
                hbox {
                    spacing = 10.0
                    alignment = Pos.CENTER_LEFT
                    label("Contained loot:")
                    label(stringBinding(step.loot) { step.loot.value?.desc?.value ?: "(no loot)" })
                    button("Change") {
                        action(::selectLoot)
                    }
                    button("Remove") {
                        enableWhen(step.loot.isNotNull)
                        action(::removeLoot)
                    }
                }
                flowgridpane(1, 2) {
                    hgap = 20.0
                    paddingAll = 10.0
                    borderpane {
                        top { label("Choices:") }
                        center {
                            listview(choices) {
                                bindSelected(selectedChoice)
                                placeholder = label("No choices")
                                cellFormat { text = "${it.text.value} (to step ${it.stepTo.value.number.value})" }
                            }
                        }
                        left {
                            vbox {
                                paddingRight = 10.0
                                spacing = 10.0
                                button("Add") {
                                    maxWidth = Double.MAX_VALUE
                                    action(::addChoice)
                                }
                                button("Delete") {
                                    enableWhen(selectedChoice.isNotNull)
                                    maxWidth = Double.MAX_VALUE
                                    action(::deleteChoice)
                                }
                            }
                        }
                    }
                    borderpane {
                        top {
                            label("Choice Constraints:")
                        }
                        center {
                            tableview(selectedChoice.select { it.constraints }) {
                                bindSelected(selectedConstraint)
                                column<String, String>("Temp") { SimpleStringProperty(it.value) } // TODO
                            }
                        }
                        left {
                            vbox {
                                paddingRight = 10.0
                                spacing = 10.0
                                button("Add") {
                                    action {
                                        TODO()
                                    }
                                }
                                button("Remove") {
                                    enableWhen(selectedConstraint.isNotNull)
                                    action{
                                        TODO()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        bottom {
            hbox {
                spacing = 10.0
                button("Save") {
                    enableWhen(step.dirty)
                    action(::save)
                }
                button("Back") {
                    action(::back)
                }
            }
        }
    }

    private fun updateData() {
        runWithLoading { transaction { step.choices } } ui {
            choices.clear()
            choices.addAll(it)
        }
    }

    private fun save() {
        runWithLoading { controller.commit(step) } ui {
            it.peek {
                errorAlert { "An error occurred" }
            }
        }
    }

    private fun back() {
        replaceWith<StepsMasterView>()
    }

    private fun addChoice() {
        find<CreateChoiceModal>(CreateChoiceModal::fromStep to step).openModal(block = true)
        updateData()
    }

    private fun deleteChoice() {
        runWithLoading { choiceController.deleteChoice(selectedChoice.value) } ui {
            it.peek {
                errorAlert { "Cannot delete choice" }
            }
            updateData()
        }
    }

    private fun selectLoot() {
        find<SelectLootModal>(SelectLootModal::selectedObject to step.loot).openModal(block = true)
    }

    private fun removeLoot() {
        step.loot.value = null
    }

    override fun onDock() {
        updateData()
    }
}