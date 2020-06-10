package viewmodel

import model.Statistic
import tornadofx.ItemViewModel

fun Statistic.fromViewModel(stat: StatisticViewModel) {
    name = stat.name.value
}

class StatisticViewModel(stat: Statistic? = null) : ItemViewModel<Statistic>(stat) {
    private val id = bind(Statistic::id)
    val name = bind(Statistic::name)

    fun saveData() {
        item.name = name.value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StatisticViewModel

        if (id.value != other.id.value) return false

        return true
    }

    override fun hashCode(): Int {
        return id.value?.hashCode() ?: super.hashCode()
    }


}