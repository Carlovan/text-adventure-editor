package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Adventures : IntIdTable("ADVENTURE") {
    val name = varchar("name", 64)

    init {
        uniqueIndex(name)
    }
}

class Adventure(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Adventure>(Adventures)

    var name by Adventures.name

    val steps by Step referrersOn Steps.adventure
    val playerConfigurations by PlayerConfiguration referrersOn PlayerConfigurations.adventure
}

abstract class AdventureTable(name: String) : IntIdTable(name) {
    val adventure = reference("adventure", Adventures)
}

object Steps : AdventureTable("STEP") {
    val number = integer("number")
    val text = text("text")
    val loot = reference("loot", Loots).nullable()

    init {
        uniqueIndex(adventure, number)
    }
}

class Step(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Step>(Steps)

    var number by Steps.number
    var text by Steps.text
    var loot by Loot optionalReferencedOn Steps.loot

    var adventure by Adventure referencedOn Steps.adventure
    val choices by Choice referrersOn Choices.stepFrom
}

object Choices : AdventureTable("CHOICE") {
    val text = text("text")

    val stepTo = reference("stepTo", Steps)
    val stepFrom = reference("stepFrom", Steps)
}

class Choice(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Choice>(Choices)

    var text by Choices.text

    var stepTo by Step referencedOn Choices.stepTo
}

object PlayerConfigurations : AdventureTable("PLAYER_CONFIGURATION") {
    val name = varchar("name", 16)
    val max_skills = integer("max_skills")

    init {
        uniqueIndex(adventure, name)
    }
}

class PlayerConfiguration(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlayerConfiguration>(PlayerConfigurations)

    var name by PlayerConfigurations.name
    var max_skills by PlayerConfigurations.max_skills

    var statistics by Statistic via StatisticsPlayerConfigurations
    var skills by Skill via SkillsPlayerConfigurations
    var slots by ItemSlot via PlayerAvailableSlots
}