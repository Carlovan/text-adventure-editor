package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select

object Skills : AdventureTable("SKILL") {
    val name = varchar("name", 32)

    init {
        uniqueIndex(adventure, name)
    }
}

class Skill(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Skill>(Skills)

    var adventure by Skills.adventure

    var name by Skills.name
    var itemsActivations by Item via ItemSkillActivations
    var statModifiers by Statistic via StatisticsSkills
    val isItemSkill get() = itemsActivations.count() > 0
    val isStatSkill get() = statModifiers.count() > 0
}

object SkillsPlayerConfigurations : Table("SKILL_PLAYER_CONFIGURATION") {
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