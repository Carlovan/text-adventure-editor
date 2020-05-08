package model

import org.jetbrains.exposed.sql.Table

object Enemies : AdventureTable("ENEMY") {
    val name = varchar("name", 32)
    val loot = reference("loot", Loots).nullable()

    init {
        uniqueIndex(adventure, name)
    }
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
    val number = integer("value").check { it greater 0 }
    override val primaryKey = PrimaryKey(enemy, step)
}