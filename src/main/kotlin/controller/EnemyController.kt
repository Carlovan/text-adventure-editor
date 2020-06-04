package controller

import javafx.collections.ObservableList
import model.Enemies
import model.EnemiesStatistics
import model.Enemy
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.MaybePSQLError
import sqlutils.safeTransaction
import tornadofx.asObservable
import viewmodel.DetailEnemyViewModel
import viewmodel.EnemyStatValueViewModel
import viewmodel.EnemyViewModel

class EnemyController : ControllerWithContextAdventure() {
    val enemies: ObservableList<EnemyViewModel>
        get() = transaction {
            Enemy.find { Enemies.adventure eq contextAdventure!!.item.id }
                 .map { EnemyViewModel(it) }
                 .asObservable()
        }

    fun createEnemy(vm: EnemyViewModel) =
        safeTransaction {
            Enemy.create(contextAdventure!!.item, vm.name.value)
        }

    fun deleteEnemy(vm: EnemyViewModel) =
        safeTransaction {
            vm.item.delete()
        }

    fun commit(changes: Sequence<EnemyViewModel>) =
        safeTransaction {
            changes.forEach {
                it.saveData()
                it.rollback()
            }
        }

    fun getDetail(master: EnemyViewModel) : DetailEnemyViewModel {
        return transaction { DetailEnemyViewModel(master.item) }
    }

    fun addEnemyStatistic(newEnemyStatValue: EnemyStatValueViewModel) =
        safeTransaction {
            newEnemyStatValue.enemy.addStatistic(newEnemyStatValue.statisticViewModel.value.item, newEnemyStatValue.value.value)
        }

    fun updateLoot(enemy: DetailEnemyViewModel) =
        safeTransaction {
            enemy.item.loot = enemy.loot.value?.item
        }

    fun commitEnemyStats(changes: Sequence<EnemyStatValueViewModel>) =
        safeTransaction {
            changes.forEach {
                it.saveData()
                it.rollback()
            }
        }

    fun deleteEnemyStat(enemyStat: EnemyStatValueViewModel) =
        safeTransaction {
            EnemiesStatistics.deleteWhere { EnemiesStatistics.enemy eq enemyStat.enemy.id and (EnemiesStatistics.statistic eq enemyStat.item.id) }
        }
}