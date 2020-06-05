package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.omg.CORBA.portable.IDLEntity

abstract class Constraints(name: String) : AdventureTable(name) {
    val choice = reference("choice", Choices)
}

abstract class Constraint(id: EntityID<Int>, table: Constraints) : IntEntity(id) {
    var adventure by table.adventure
    var choice by table.choice
}

object DiceConstraints : Constraints("DICE_CONSTRAINT") {
    val minValue = integer("minValue").nullable().check { it greaterEq 0 }
    val maxValue = integer("maxValue").nullable()

    init {
        check { minValue lessEq maxValue }
    }
}

class DiceConstraint(id: EntityID<Int>) : Constraint(id, DiceConstraints) {
    companion object : IntEntityClass<DiceConstraint>(DiceConstraints)

    var minValue by DiceConstraints.minValue
    var maxValue by  DiceConstraints.maxValue
}

object SkillConstraints : Constraints("SKILL_CONSTRAINT") {
    val skill = reference("skill", Skills)

    init {
        uniqueIndex(this.choice, skill)
    }
}

class SkillConstraint(id: EntityID<Int>) : Constraint(id, SkillConstraints) {
    companion object : IntEntityClass<SkillConstraint>(SkillConstraints)

    var skill by Skill referencedOn SkillConstraints.skill
}

object StatisticConstraints : Constraints("STATISTIC_CONSTRAINT") {
    val statistic = reference("statistic", Statistics)
    val minValue = integer("minValue").nullable()
    val maxValue = integer("maxValue").nullable()

    init {
        uniqueIndex(this.choice, statistic)
    }
}

class StatisticConstraint(id: EntityID<Int>) : Constraint(id, StatisticConstraints) {
    companion object : IntEntityClass<StatisticConstraint>(StatisticConstraints)

    var statistic by Statistic referencedOn StatisticConstraints.statistic
    var minValue by StatisticConstraints.minValue
    var maxValue by  StatisticConstraints.maxValue
}

object ItemConstraints : Constraints("ITEM_CONSTRAINT") {
    val item = reference("item", Items)
    val quantity = integer("quantity").check { it greater 0 }
    val isConsumed = bool("is_consumed").default(false)

    init {
        uniqueIndex(this.choice, item)
    }
}

class ItemConstraint(id: EntityID<Int>) : Constraint(id, ItemConstraints) {
    companion object : IntEntityClass<ItemConstraint>(ItemConstraints)

    var item by Item referencedOn ItemConstraints.item
    var quantity by ItemConstraints.quantity
    var isConsumed by ItemConstraints.isConsumed
}