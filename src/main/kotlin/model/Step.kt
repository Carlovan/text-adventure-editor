package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

object Steps : AdventureTable("STEP") {
    val number = integer("number").check { it greaterEq Step.MIN_NUMBER }
    val text = text("text")
    val loot = reference("loot", Loots).nullable()

    init {
        uniqueIndex(adventure, number)
    }
}

class Step(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Step>(Steps) {
        const val MIN_NUMBER = 1
    }

    var number by Steps.number
    var text by Steps.text
    var loot by Loot optionalReferencedOn Steps.loot

    var adventure by Adventure referencedOn Steps.adventure
    val choices by Choice referrersOn Choices.stepFrom
}

object Choices : AdventureTable("CHOICE") {
    val text = text("text")

    val stepTo = reference("step_to", Steps)
    val stepFrom = reference("step_from", Steps)
}

class Choice(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Choice>(Choices)

    var adventure by Adventure referencedOn Choices.adventure
    var text by Choices.text

    var stepFrom by Step referencedOn Choices.stepFrom
    var stepTo by Step referencedOn Choices.stepTo
}
