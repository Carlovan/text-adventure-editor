package viewmodel

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty
import model.*
import tornadofx.*
import tornadofx.ItemViewModel

interface ConstraintViewModel {
    val description : ReadOnlyStringProperty
}

class DiceConstraintViewModel(constraint: DiceConstraint? = null) : ItemViewModel<DiceConstraint>(constraint), ConstraintViewModel {
    val minValue = bind(DiceConstraint::minValue)
    val maxValue = bind(DiceConstraint::maxValue)

    override val description: ReadOnlyStringProperty
        get() = SimpleStringProperty("Dice constraint (${minValue.value}, ${maxValue.value})")
}

class SkillConstraintViewModel(constraint: SkillConstraint? = null) : ItemViewModel<SkillConstraint>(constraint), ConstraintViewModel {
    val skillViewModel = property(constraint?.skill?.let { SkillViewModel(it) }).fxProperty
    val skillName = skillViewModel.select { it.name }

    override val description: ReadOnlyStringProperty
        get() = SimpleStringProperty("Skill constraint (${skillName.value})")
}

class StatisticConstraintViewModel(constraint: StatisticConstraint? = null) : ItemViewModel<StatisticConstraint>(constraint), ConstraintViewModel {
    val statViewModel = property(constraint?.statistic?.let { StatisticViewModel(it) }).fxProperty
    val statName = statViewModel.select { it.name }
    val minValue = bind(StatisticConstraint::minValue)
    val maxValue = bind(StatisticConstraint::maxValue)

    override val description: ReadOnlyStringProperty
        get() = SimpleStringProperty("Statistic constraint (${statName.value}, ${minValue.value}, ${maxValue.value})")
}

class ItemConstraintViewModel(constraint: ItemConstraint? = null) : ItemViewModel<ItemConstraint>(constraint), ConstraintViewModel {
    val itemViewModel = property(constraint?.item?.let { viewmodel.ItemViewModel(it) }).fxProperty
    val itemName = itemViewModel.select { it.name }
    val quantity = bind(ItemConstraint::quantity)
    val isConsumed = bind(ItemConstraint::isConsumed)

    override val description: ReadOnlyStringProperty
        get() = SimpleStringProperty("Item constraint (${itemName.value}, ${quantity.value}, ${isConsumed.value})")
}

