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

object Choices : AdventureTable("CHOICE") {
    val text = text("text")

    val stepTo = reference("stepTo", Steps)
    val stepFrom = reference("stepFrom", Steps)
}

object PlayerConfigurations : AdventureTable("PLAYER_CONFIGURATION") {
    val name = varchar("name", 16)
    val max_skills = integer("max_skills")

    init {
        uniqueIndex(adventure, name)
    }
}