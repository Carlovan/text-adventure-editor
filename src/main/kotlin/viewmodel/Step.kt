package viewmodel

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import model.EnemiesSteps
import model.Enemy
import model.Step
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import tornadofx.ItemViewModel

fun Step.fromViewModel(step: StepViewModel) {
    number = step.number.value
    text = step.text.value
}

fun Step.fromViewModel(step: DetailStepViewModel) {
    text = step.text.value
    loot = step.loot.value?.item
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
    val enemies get() = item?.let {
        EnemiesSteps
            .select { EnemiesSteps.step eq item.id }
            .map { EnemyStepViewModel(item, Enemy.findById(it[EnemiesSteps.enemy]), it[EnemiesSteps.quantity]) }
            .toList().asObservable()
    } ?: observableListOf()

    val loot = bind {
        transaction { item?.observable(Step::loot)?.select { it?.let { LootViewModel(it) }.toProperty() } }
            ?: SimpleObjectProperty()
    } as SimpleObjectProperty<LootViewModel?>

    fun saveData() {
        item?.also {
            item.fromViewModel(this)
            rollback()
        }
    }
}