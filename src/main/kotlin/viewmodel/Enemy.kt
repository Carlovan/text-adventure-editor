package viewmodel

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import model.Enemy

class EnemyViewModel(enemy: Enemy? = null) : ItemViewModel<Enemy>(enemy) {
    val name = bind(Enemy::name)
    val statisticsDescription = SimpleStringProperty(this, "ref",
        enemy?.getStatisticValuePairs()?.map { StatisticViewModel(it.first) to it.second }
                                       ?.joinToString { "${it.first.name.value} : ${it.second}" }) as ReadOnlyStringProperty
}