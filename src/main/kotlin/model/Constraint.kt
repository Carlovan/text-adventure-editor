package model

abstract class Constraints(name: String) : AdventureTable(name) {
    val choice = reference("choice", Choices)
}

object DiceConstraints : Constraints("DICE_CONSTRAINT") {
    val minValue = integer("minValue").nullable().check { it greaterEq 0 }
    val maxValue = integer("maxValue").nullable()

    init {
        check { minValue lessEq maxValue }
    }
}

object SkillConstraints : Constraints("SKILL_CONSTRAINT") {
    val skill = reference("skill", Skills)
}

object StatisticContraints : Constraints("STATISTIC_CONSTRAINT") {
    val statistic = reference("statistic", Statistics)
    val minValue = integer("minValue").nullable()
    val maxValue = integer("maxValue").nullable()
}

object ItemConstraints : Constraints("ITEM_CONSTRAINT") {
    val item = reference("item", Items)
    val quantity = integer("quantity").check { it greater 0 }
    val isConsumed = bool("is_consumed").default(false)
}