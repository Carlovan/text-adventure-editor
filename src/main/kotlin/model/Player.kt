package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

object PlayerConfigurations : AdventureTable("PLAYER_CONFIGURATION") {
    val name = varchar("name", 16)
    val max_skills = integer("max_skills").check { it greater 0 }

    init {
        uniqueIndex(adventure, name)
    }
}

class PlayerConfiguration(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlayerConfiguration>(PlayerConfigurations)

    var adventure by PlayerConfigurations.adventure

    var name by PlayerConfigurations.name
    var maxSkills by PlayerConfigurations.max_skills

    var statistics by Statistic via StatisticsPlayerConfigurations
    var skills by Skill via SkillsPlayerConfigurations
    var slots by ItemSlot via PlayerAvailableSlots
}