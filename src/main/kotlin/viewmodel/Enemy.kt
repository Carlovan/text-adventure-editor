package viewmodel

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import model.Enemy
import model.Statistic
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.asObservable

class EnemyViewModel(enemy: Enemy? = null) : ItemViewModel<Enemy>(enemy) {
    val name = bind(Enemy::name)
    val statisticsDescription = SimpleStringProperty(this, "ref",
        enemy?.getStatisticValuePairs()?.map { StatisticViewModel(it.first) to it.second }
                                       ?.joinToString { "${it.first.name.value} : ${it.second}" }) as ReadOnlyStringProperty
}

class DetailEnemyViewModel(enemy: Enemy) : ItemViewModel<Enemy>(enemy) {
    val name = bind(Enemy::name)

    val statistics get() = transaction {
        item.getStatisticValuePairs()
            .map { EnemyStatValueViewModel(item, it.first, it.second) }
            .asObservable()
    }
}

class EnemyStatValueViewModel(val enemy: Enemy, stat: Statistic? = null, value: Int? = null) : ItemViewModel<Statistic>(stat) {
    val statName = bind(Statistic::name) as ReadOnlyStringProperty
    val value = SimpleIntegerProperty(this, "ref", value ?: 0)

    val statisticViewModel = SimpleObjectProperty(this, "ref2", StatisticViewModel(stat))
}