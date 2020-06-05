package viewmodel

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import model.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.observableListOf
import tornadofx.property

fun Choice.fromViewModel(choice: ChoiceViewModel) {
    text = choice.text.value
    stepTo = choice.stepTo.value.item
}

class ChoiceViewModel(choice: Choice? = null) : ItemViewModel<Choice>(choice) {
    val text = bind(Choice::text)
    val stepTo = property(choice?.stepTo?.let { StepViewModel(it) }).fxProperty

    val constraints : ObservableValue<ObservableList<ConstraintViewModel>> get() {
        return transaction {
            val dice = DiceConstraint.find { DiceConstraints.choice eq item.id }.map { DiceConstraintViewModel(it) }.asObservable()
            val skill = SkillConstraint.find { SkillConstraints.choice eq item.id }.map { SkillConstraintViewModel(it) }.asObservable()
            val stat = StatisticConstraint.find { StatisticConstraints.choice eq item.id }.map { StatisticConstraintViewModel(it) }.asObservable()
            val item = ItemConstraint.find { ItemConstraints.choice eq item.id }.map { ItemConstraintViewModel(it) }.asObservable()

            val res = observableListOf<ConstraintViewModel>()
            res.addAll(dice)
            res.addAll(skill)
            res.addAll(stat)
            res.addAll(item)
            property(res).fxProperty
        }
    }
}