package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

abstract class Constraint(name: String) : AdventureTable(name) {
    val choice = reference("choice", Choices)
}

object DiceConstraint : Constraint("DICE_CONSTRAINT") {
    val minValue = integer("minValue").nullable()
    val maxValue = integer("maxValue").nullable()
}