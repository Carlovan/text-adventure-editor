package viewmodel

import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty
import model.Step
import tornadofx.ItemViewModel
import tornadofx.asObservable
import tornadofx.observableListOf
import tornadofx.property

fun Step.fromViewModel(step: StepViewModel) {
    number = step.number.value
    text = step.text.value
}

fun Step.fromViewModel(step: DetailStepViewModel) {
    text = step.text.value
    loot = step.loot.value.item
}

class StepViewModel(step: Step? = null) : ItemViewModel<Step>(step) {
    val number = bind(Step::number)
    val text = bind(Step::text)

    val stepsTo = SimpleStringProperty(step?.choices?.joinToString { it.stepTo.number.toString() } ?: "") as ReadOnlyStringProperty

    fun saveData() {
        item?.also {
            item.fromViewModel(this)
            rollback()
        }
    }
}

class DetailStepViewModel(step: Step? = null) : ItemViewModel<Step>(step) {
    val number = bind(Step::number)
    val text = bind(Step::text)

    val choices get() = item?.choices?.map { ChoiceViewModel(it) }?.toList()?.asObservable() ?: observableListOf()
    val loot = property(step?.loot?.let { LootViewModel(it) }).fxProperty as ReadOnlyProperty<LootViewModel>

    fun saveData() {
        item?.also {
            item.fromViewModel(this)
            rollback()
        }
    }
}