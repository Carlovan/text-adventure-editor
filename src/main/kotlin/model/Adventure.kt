package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Adventures : IntIdTable("ADVENTURE") {
    val name = varchar("name", 60)
}

abstract class AdventureTable(name: String) : IntIdTable(name) {
    val adventure = reference("adventure", Adventures)
}

object Steps : AdventureTable("STEP") {
    val number = integer("number")
    val text = text("text")

    init {
        uniqueIndex(adventure, number)
    }
}

object Choices : AdventureTable("CHOICE") {
    val text = text("text")

    val stepTo = reference("stepTo", Steps)
    val stepFrom = reference("stepFrom", Steps)
}