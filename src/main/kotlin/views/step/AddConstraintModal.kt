package views.step

import controller.ChoiceController
import controller.ItemController
import controller.SkillController
import controller.StatisticController
import ellipses
import javafx.beans.property.SimpleObjectProperty
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.*
import viewmodel.ItemViewModel
import views.*

abstract class ConstraintForm : Fragment() {
    abstract val newConstraint: ConstraintViewModel

    open fun updateData(op: () -> Unit) {}
}

class DiceConstraintForm : ConstraintForm() {
    private val newDiceConstraint = DiceConstraintViewModel()
    override val newConstraint = newDiceConstraint

    override val root = form {
        fieldset {
            field("Minimum value") {
                textfield(newDiceConstraint.minValue) {
                    requiredPositiveInteger()
                    text = "0"
                }
            }
            field("Maximum value") {
                textfield(newDiceConstraint.maxValue) {
                    requiredPositiveInteger()
                    text = "6"
                }
            }
        }
    }
}

class SkillConstraintForm : ConstraintForm() {
    private val skillController: SkillController by inject()
    private val newSkillConstraint = SkillConstraintViewModel()
    override val newConstraint = newSkillConstraint

    private val skills = observableListOf<SkillViewModel>()

    override val root = form {
        fieldset {
            field("Linked skill") {
                combobox(newSkillConstraint.skillViewModel, skills) {
                    required()
                    cellFormat {
                        text = it.name.value.ellipses(30)
                    }
                }
            }
        }
    }

    override fun updateData(op: () -> Unit) {
        runWithLoading { skillController.skills } ui {
            skills.clear()
            skills.addAll(it)
            op()
        }
    }
}

class StatisticConstraintForm : ConstraintForm() {
    private val statisticController: StatisticController by inject()
    private val newStatisticConstraint = StatisticConstraintViewModel()
    override val newConstraint = newStatisticConstraint

    private val statistics = observableListOf<StatisticViewModel>()

    override val root = form {
        fieldset {
            field("Linked statistic") {
                combobox(newStatisticConstraint.statViewModel, statistics) {
                    required()
                    cellFormat {
                        text = it.name.value.ellipses(30)
                    }
                }

            }
            field("Minimum value") {
                textfield(newStatisticConstraint.minValue) {
                    requiredInteger()
                    text = "0"
                }
            }
            field("Maximum value") {
                textfield(newStatisticConstraint.maxValue) {
                    requiredInteger()
                    text = "10"
                }
            }
        }
    }

    override fun updateData(op: () -> Unit) {
        runWithLoading { statisticController.statistics } ui {
            statistics.clear()
            statistics.addAll(it)
            op()
        }
    }
}

class ItemConstraintForm : ConstraintForm() {
    private val itemController: ItemController by inject()
    private val newItemConstraint = ItemConstraintViewModel()
    override val newConstraint = newItemConstraint

    private val items = observableListOf<ItemViewModel>()

    override val root = form {
        fieldset {
            field("Linked item") {
                combobox(newItemConstraint.itemViewModel, items) {
                    required()
                    cellFormat {
                        text = it.name.value.ellipses(30)
                    }
                }

            }
            field("Required quantity") {
                textfield(newItemConstraint.quantity) {
                    required()
                    requiredPositiveInteger()
                    text = "1"
                }
            }
            field("Is consumed") {
                checkbox(property = newItemConstraint.isConsumed) {
                    isSelected = false
                }
            }
        }
    }

    override fun updateData(op: () -> Unit) {
        runWithLoading { itemController.items } ui {
            items.clear()
            items.addAll(it)
            op()
        }
    }
}

class AddConstraintModal : Fragment("Add constraint") {
    private val controller: ChoiceController by inject()
    val choice: ChoiceViewModel by param()

    private val selectedType = SimpleObjectProperty<ConstraintType>()
    private val currentForm = SimpleObjectProperty<ConstraintForm>()

    init {
        selectedType.onChange {
            currentForm.value = when (it!!) {
                ConstraintType.DICE -> DiceConstraintForm()
                ConstraintType.SKILL -> SkillConstraintForm()
                ConstraintType.STATISTIC -> StatisticConstraintForm()
                ConstraintType.ITEM -> ItemConstraintForm()
            }
            currentForm.value.updateData {
                modalStage?.sizeToScene()
            }
        }
    }

    override val root = borderpane {
        top {
            vbox {
                spacing = 10.0
                paddingAll = 20.0
                label("Constraint type:")
                combobox(selectedType, ConstraintType.values().toList()) {
                    selectionModel.selectFirst()
                    selectedType.onChange { this@borderpane.requestFocus() }
                }
            }
        }
        centerProperty().bind(currentForm.select { it.root.toProperty() })
        bottom {
            hbox {
                spacing = 10.0
                paddingLeft = 20.0
                paddingBottom = 20.0
                button("Add") {
                    enableWhen(currentForm.select { it.newConstraint.innerItem.valid })
                    action(::addConstraint)
                }
                button("Cancel") {
                    action {
                        close()
                    }
                }
            }
        }
    }

    private fun addConstraint() {
        runWithLoading { controller.addConstraint(choice, currentForm.value.newConstraint) } ui {
            it.peek { error ->
                errorAlert {
                    when (error) {
                        PSQLState.CHECK_VIOLATION -> "Invalid parameters"
                        PSQLState.UNIQUE_VIOLATION -> "This constraint is already present"
                        else -> null
                    }
                }
            }.onEmpty { close() }
        }
    }
}