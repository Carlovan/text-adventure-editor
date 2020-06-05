package viewmodel

import javafx.beans.property.Property
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty
import model.*
import tornadofx.*
import tornadofx.ItemViewModel

enum class ConstraintType {
    DICE, SKILL, STATISTIC, ITEM
}

interface ConstraintViewModel {
    val description : ReadOnlyStringProperty
    val innerItem : ItemViewModel<out Constraint>
    val type : ConstraintType
}

class DiceConstraintViewModel(constraint: DiceConstraint? = null) : ItemViewModel<DiceConstraint>(constraint), ConstraintViewModel {
    val minValue = bind(DiceConstraint::minValue)
    val maxValue = bind(DiceConstraint::maxValue)

    override val description: ReadOnlyStringProperty
        get() = SimpleStringProperty("Min val = ${minValue.value}, Max val = ${maxValue.value})")

    override val innerItem = this
    override val type = ConstraintType.DICE
}

class SkillConstraintViewModel(constraint: SkillConstraint? = null) : ItemViewModel<SkillConstraint>(constraint), ConstraintViewModel {
    val skillViewModel = property(constraint?.skill?.let { SkillViewModel(it) }).fxProperty
    val skillName = skillViewModel.select { it.name }

    override val description: ReadOnlyStringProperty
        get() = SimpleStringProperty("Skill = ${skillName.value})")

    override val innerItem = this
    override val type = ConstraintType.SKILL
}

class StatisticConstraintViewModel(constraint: StatisticConstraint? = null) : ItemViewModel<StatisticConstraint>(constraint), ConstraintViewModel {
    val statViewModel = property(constraint?.statistic?.let { StatisticViewModel(it) }).fxProperty
    val statName = statViewModel.select { it.name }
    val minValue = bind(StatisticConstraint::minValue)
    val maxValue = bind(StatisticConstraint::maxValue)

    override val description: ReadOnlyStringProperty
        get() = SimpleStringProperty("Statistic = ${statName.value}, Min val = ${minValue.value}, Max val = ${maxValue.value}")

    override val innerItem = this
    override val type = ConstraintType.STATISTIC
}

class ItemConstraintViewModel(constraint: ItemConstraint? = null) : ItemViewModel<ItemConstraint>(constraint), ConstraintViewModel {
    val itemViewModel = property(constraint?.item?.let { viewmodel.ItemViewModel(it) }).fxProperty
    val itemName = itemViewModel.select { it.name }
    val quantity = bind(ItemConstraint::quantity)
    val isConsumed = bind(ItemConstraint::isConsumed)

    override val description: ReadOnlyStringProperty
        get() = SimpleStringProperty("Item = (${itemName.value}, Quantity = ${quantity.value}, Is consumed = ${isConsumed.value}")

    override val innerItem = this
    override val type = ConstraintType.ITEM
}

