package controller

import javafx.collections.ObservableList
import model.Statistic
import model.Statistics
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.safeTransaction
import tornadofx.asObservable
import viewmodel.StatisticViewModel
import viewmodel.fromViewModel

class StatisticController : ControllerWithContextAdventure() {
    val statistics : ObservableList<StatisticViewModel>
        get() = transaction {
            Statistic.find { Statistics.adventure eq contextAdventure!!.item.id }
                .map {
                    StatisticViewModel(it)
                }.asObservable()
        }

    fun commit(changes: Sequence<StatisticViewModel>) =
        safeTransaction {
            changes.forEach {
                it.saveData()
                it.rollback()
            }
        }

    fun createStatistic(newStatistic: StatisticViewModel) =
        safeTransaction {
            Statistic.new {
                adventure = contextAdventure!!.item.id
                fromViewModel(newStatistic)
            }
        }

    fun deleteStatistic(stat: StatisticViewModel) =
        safeTransaction {
            stat.item.delete()
        }
}