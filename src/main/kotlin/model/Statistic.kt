package model

import org.jetbrains.exposed.sql.Table

object Statistics : AdventureTable("STATISTIC") {
    val name = varchar("name", 32)

    init {
        uniqueIndex(adventure, name)
    }
}

object StatisticsSkills : Table("STATISTIC_SKILL") {
    val statistic = reference("statistic", Statistics)
    val skill = reference("skill", Skills)
    val value = integer("value")
    override val primaryKey = PrimaryKey(statistic, skill)
}

object StatisticsItems : Table("STATISTIC_ITEM") {
    val statistic = reference("statistic", Statistics)
    val item = reference("item", Items)
    val value = integer("value")
    override val primaryKey = PrimaryKey(statistic, item)
}

object StatisticsPlayerConfigurations : Table("STATISTIC_PLAYER_CONFIGURATION") {
    val statistic = reference("statistic", Statistics)
    val playerConf = reference("player_conf", PlayerConfigurations)
    override val primaryKey = PrimaryKey(statistic, playerConf)
}