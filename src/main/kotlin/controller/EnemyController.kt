package controller

import javafx.collections.ObservableList
import model.Enemies
import model.Enemy
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.MaybePSQLError
import sqlutils.safeTransaction
import tornadofx.asObservable
import viewmodel.EnemyViewModel

class EnemyController : ControllerWithContextAdventure() {
    fun createEnemy(vm: EnemyViewModel) =
        safeTransaction {
            Enemy.create(contextAdventure!!.item, vm.name.value)
        }

    fun deleteEnemy(vm: EnemyViewModel) =
        safeTransaction {
            vm.item.delete()
        }

    val enemies: ObservableList<EnemyViewModel>
        get() = transaction {
            Enemy.find { Enemies.adventure eq contextAdventure!!.item.id }
                 .map { EnemyViewModel(it) }
                 .asObservable()
        }
}