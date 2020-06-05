package viewmodel

import model.*
import tornadofx.*
import tornadofx.ItemViewModel

abstract class ConstraintViewModel<T : Constraint>(constraint: T? = null) : ItemViewModel<T>(constraint) {
    abstract val description : String
}

class DiceConstraintViewModel(constraint: DiceConstraint? = null) : ConstraintViewModel<DiceConstraint>(constraint) {
    val minValue = bind(DiceConstraint::minValue)
    val maxValue = bind(DiceConstraint::maxValue)

    override val description: String
        get() = "Dice constraint (${minValue.value}, ${maxValue.value})"
}

class SkillConstraintViewModel(constraint: SkillConstraint? = null) : ConstraintViewModel<SkillConstraint>(constraint) {
    val skillViewModel = property(constraint?.skill?.let { SkillViewModel(it) }).fxProperty
    val skillName = skillViewModel.select { it.name }

    override val description: String
        get() = "Skill constraint (${skillName.value})"
}

class StatisticConstraintViewModel(constraint: StatisticConstraint? = null) : ConstraintViewModel<StatisticConstraint>(constraint) {
    val statViewModel = property(constraint?.statistic?.let { StatisticViewModel(it) }).fxProperty
    val statName = statViewModel.select { it.name }
    val minValue = bind(StatisticConstraint::minValue)
    val maxValue = bind(StatisticConstraint::maxValue)

    override val description: String
        get() = "Statistic constraint (${statName.value}, ${minValue.value}, ${maxValue.value})"
}

class ItemConstraintViewModel(constraint: ItemConstraint? = null) : ConstraintViewModel<ItemConstraint>(constraint) {
    val itemViewModel = property(constraint?.item?.let { viewmodel.ItemViewModel(it) }).fxProperty
    val itemName = itemViewModel.select { it.name }
    val quantity = bind(ItemConstraint::quantity)
    val isConsumed = bind(ItemConstraint::isConsumed)

    override val description: String
        get() = "Item constraint (${itemName.value}, ${quantity.value}, ${isConsumed.value})"
}

