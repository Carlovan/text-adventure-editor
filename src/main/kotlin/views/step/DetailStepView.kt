package views.step

import controller.ChoiceController
import controller.StepController
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import org.jetbrains.exposed.sql.transactions.transaction
import peek
import tornadofx.*
import viewmodel.ChoiceViewModel
import viewmodel.ConstraintViewModel
import viewmodel.DetailStepViewModel
import viewmodel.EnemyStepViewModel
import views.errorAlert
import views.flowgridpane
import views.loot.SelectLootModal
import views.runWithLoading
import views.ui

class DetailStepView : Fragment() {
    private val controller: StepController by inject()
    private val choiceController: ChoiceController by inject()

    val step: DetailStepViewModel by param()
    private val choices = observableListOf<ChoiceViewModel>()
    private val enemies = observableListOf<EnemyStepViewModel>()
    private val selectedChoice = SimpleObjectProperty<ChoiceViewModel>()
    private val selectedEnemy = SimpleObjectProperty<EnemyStepViewModel>()

    init {
        selectedChoice.onChange { updateConstraints() }
    }

    private val constraints = observableListOf<ConstraintViewModel>()
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
                            tableview(constraints) {
                                bindSelected(selectedConstraint)
                                column<ConstraintViewModel, String>("Type") { it.value.type.toString().toProperty() }
                                column("Description", ConstraintViewModel::description)
                            }
                        }
                        left {
                            vbox {
                                paddingRight = 10.0
                                spacing = 10.0
                                button("Add") {
                                    enableWhen(selectedChoice.isNotNull)
                                    action(::addConstraint)
                                }
                                button("Remove") {
                                    enableWhen(selectedConstraint.isNotNull)
                                    action(::removeConstraint)
                                }
                            }
                        }
                    }
                    borderpane {
                        top { label("Enemies:") }
                        center {
                            listview(enemies) {
                                bindSelected(selectedEnemy)
                                placeholder = label("No enemies")
                                cellFormat { text = "${it.enemyName.value} (${it.quantity.value})" }
                            }
                        }
                        left {
                            vbox {
                                paddingRight = 10.0
                                spacing = 10.0
                                button("Add") {
                                    maxWidth = Double.MAX_VALUE
                                    action(::addEnemy)
                                }
                                button("Delete") {
                                    enableWhen(selectedEnemy.isNotNull)
                                    maxWidth = Double.MAX_VALUE
                                    action(::deleteEnemy)
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
                    maxWidth = Double.MAX_VALUE
                    enableWhen(step.dirty)
                    action(::save)
                }
                button("Back") {
                    maxWidth = Double.MAX_VALUE
                    action(::back)
                }
            }
        }
    }

    private fun updateData() {
        runWithLoading { transaction { step.choices } } ui {
            choices.clear()
            choices.addAll(it)

            runWithLoading { transaction { step.enemies } } ui {
                enemies.clear()
                enemies.addAll(it)
            }
        }
    }

    private fun updateConstraints() {
        constraints.clear()
        selectedChoice.value?.let { choice ->
             runWithLoading { choice.constraints } ui {
                 constraints.addAll(it)
             }
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

    private fun addEnemy() {
        find<AddEnemyStepModal>(AddEnemyStepModal::step to step).openModal(block = true)
        updateData()
    }

    private fun deleteEnemy() {
        runWithLoading { controller.deleteEnemy(selectedEnemy.value) } ui {
            it.peek {
                errorAlert { "Cannot delete enemy" }
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

    private fun removeConstraint() {
        runWithLoading { choiceController.removeConstraint(selectedConstraint.value) } ui {
            updateConstraints()
        }
    }

    private fun addConstraint() {
        find<AddConstraintModal>(AddConstraintModal::choice to selectedChoice.value).openModal(block = true)
        updateConstraints()
    }

    override fun onDock() {
        updateData()
    }
}