package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import tornadofx.asObservable

object Enemies : AdventureTable("ENEMY") {
    val name = varchar("name", 32)
    val loot = optReference("loot", Loots)

    init {
        uniqueIndex(adventure, name)
    }
}

class Enemy(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Enemy>(Enemies) {
        fun create(adventure: Adventure, name: String): Enemy {
            return Enemy.new {
                this.adventure = adventure.id
                this.name = name
            }
        }
    }

    var adventure by Enemies.adventure
    var name by Enemies.name
    var statistics by Statistic via EnemiesStatistics
    var loot by Loot optionalReferencedOn Enemies.loot

    fun addStatistic(statistic: Statistic, value: Int) {
        EnemiesStatistics.insert {
            it[this.enemy] = this@Enemy.id
            it[this.statistic] = statistic.id
            it[this.value] = value
        }
    }

    fun getStatisticValuePairs() =
        (EnemiesStatistics innerJoin Statistics)
            .slice(Statistics.id, EnemiesStatistics.value)
            .select {Statistics.id eq EnemiesStatistics.statistic and (EnemiesStatistics.enemy eq id)}
            .map { Statistic[it[Statistics.id]] to it[EnemiesStatistics.value] }
            .toList()
}

object EnemiesStatistics : Table("ENEMY_STATISTIC") {
    val enemy = reference("enemy", Enemies)
    val statistic = reference("statistic", Statistics)
    val value = integer("value").check { it greater 0 }
    override val primaryKey = PrimaryKey(enemy, statistic)
}

object EnemiesSteps : Table("ENEMY_STEP") {
    val enemy = reference("enemy", Enemies)
    val step = reference("step", Steps)
    val quantity = integer("quantity").check { it greater 0 }
    override val primaryKey = PrimaryKey(enemy, step)
}