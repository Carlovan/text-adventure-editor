package viewmodel

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import model.EnemiesStatistics
import model.EnemiesSteps
import tornadofx.ItemViewModel
import model.Enemy
import model.Statistic
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import tornadofx.asObservable

class EnemyViewModel(enemy: Enemy? = null) : ItemViewModel<Enemy>(enemy) {
    val name = bind(Enemy::name)
    val statisticsDescription = SimpleStringProperty(this, "ref",
        enemy?.getStatisticValuePairs()?.map { StatisticViewModel(it.first) to it.second }
                                       ?.joinToString { "${it.first.name.value} : ${it.second}" }) as ReadOnlyStringProperty

    fun saveData(){
        item.name = name.value
    }
}

class DetailEnemyViewModel(enemy: Enemy) : ItemViewModel<Enemy>(enemy) {
    val name = bind(Enemy::name)

    val statistics get() = transaction {
        item.getStatisticValuePairs()
            .map { EnemyStatValueViewModel(item, it.first, it.second) }
            .asObservable()
    }

    val loot = SimpleObjectProperty(this, "ref3", enemy.loot?.let { LootViewModel(it) })
}

class EnemyStatValueViewModel(val enemy: Enemy, stat: Statistic? = null, value: Int? = null) : ItemViewModel<Statistic>(stat) {
    val statName = bind(Statistic::name) as ReadOnlyStringProperty
    val value = SimpleIntegerProperty(this, "ref", value ?: 0)

    val statisticViewModel = SimpleObjectProperty<StatisticViewModel>(this, "ref2", stat?.let { StatisticViewModel(it) } )

    fun saveData(){
        EnemiesStatistics.update({ EnemiesStatistics.enemy eq enemy.id and (EnemiesStatistics.statistic eq item.id) }) {
            it[EnemiesStatistics.value] = this@EnemyStatValueViewModel.value.value
        }
    }
}

class EnemyStepViewModel(val step: model.Step, enemy: Enemy? = null, quantity: Int? = null) : ItemViewModel<Enemy>(enemy) {
    val enemyName = bind(Enemy::name)
    val quantity = SimpleIntegerProperty(this, "ref", quantity ?: 1)

    val enemyViewModel = SimpleObjectProperty<EnemyViewModel>(this, "ref2", enemy?.let { EnemyViewModel(it) } )

    fun saveData(){
        EnemiesSteps.update({EnemiesSteps.enemy eq item.id and (EnemiesSteps.step eq step.id)}) {
            it[EnemiesSteps.quantity] = this@EnemyStepViewModel.quantity.value
        }
    }
}