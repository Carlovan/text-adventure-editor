package model

import org.jetbrains.exposed.sql.Table

object Skills : AdventureTable("SKILL") {
    val name = varchar("name", 32)
    val isItemSkill = bool("is_item_skill")
    val isStatSkill = bool("is_stat_skill")

    init {
        uniqueIndex(adventure, name)
    }
}

object PlayerConfigurationsSkills : Table("PLAYER_CONFIGURATION_SKILL") {
    val playerConf = reference("player_conf", PlayerConfigurations)
    val skill = reference("skill", Skills)
    override val primaryKey = PrimaryKey(playerConf, skill)
}

object ItemSkillActivations : Table("ITEM_SKILL_ACTIVATION") {
    val item = reference("item", Items)
    val skill = reference("skill", Skills)
    val quantity = integer("quantity").check { it greater 0 }
    override val primaryKey = PrimaryKey(item, skill)
}