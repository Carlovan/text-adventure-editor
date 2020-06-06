package viewmodel

import javafx.collections.ObservableList
import model.*
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.property

fun Choice.fromViewModel(choice: ChoiceViewModel) {
    text = choice.text.value
    stepTo = choice.stepTo.value.item
}

class ChoiceViewModel(choice: Choice? = null) : ItemViewModel<Choice>(choice) {
    val text = bind(Choice::text)
    val stepTo = property(choice?.stepTo?.let { StepViewModel(it) }).fxProperty

    val constraints : ObservableList<ConstraintViewModel> get() {
        return transaction {
            val dice: List<ConstraintViewModel> = DiceConstraint.find { DiceConstraints.choice eq item.id }.map { DiceConstraintViewModel(it) }
            val skill: List<ConstraintViewModel> = SkillConstraint.find { SkillConstraints.choice eq item.id }.map { SkillConstraintViewModel(it) }
            val stat: List<ConstraintViewModel> = StatisticConstraint.find { StatisticConstraints.choice eq item.id }.map { StatisticConstraintViewModel(it) }
            val item: List<ConstraintViewModel> = ItemConstraint.find { ItemConstraints.choice eq item.id }.map { ItemConstraintViewModel(it) }

            (dice + skill + stat + item).asObservable()
        }
    }
}