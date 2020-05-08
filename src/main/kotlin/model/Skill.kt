package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Table

object Skills : AdventureTable("SKILL") {
    val name = varchar("name", 32)
    val isItemSkill = bool("is_item_skill")
    val isStatSkill = bool("is_stat_skill")

    init {
        uniqueIndex(adventure, name)
    }
}

class Skill(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Skill>(Skills)

    var name by Skills.name
    var isItemSkill by Skills.isItemSkill
    var isStatSkill by Skills.isStatSkill
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