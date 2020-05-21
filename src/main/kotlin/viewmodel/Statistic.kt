package viewmodel

import model.Statistic
import tornadofx.ItemViewModel

fun Statistic.fromViewModel(stat: StatisticViewModel) {
    name = stat.name.value
}

class StatisticViewModel(stat: Statistic? = null) : ItemViewModel<Statistic>(stat) {
    val name = bind(Statistic::name)

    fun saveData() {
        item.name = name.value
    }
}