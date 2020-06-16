package controller

import model.*
import sqlutils.MaybePSQLError
import sqlutils.safeTransaction
import viewmodel.*

class ChoiceController: ControllerWithContextAdventure() {
    fun createChoice(choice: ChoiceViewModel, fromStep: DetailStepViewModel) =
        safeTransaction {
            Choice.new {
                stepFrom = fromStep.item
                adventure = contextAdventure!!.item
                fromViewModel(choice)
            }
        }

    fun deleteChoice(choice: ChoiceViewModel) =
        safeTransaction {
            choice.item.delete()
        }

    fun addConstraint(choice: ChoiceViewModel, constraint: ConstraintViewModel) : MaybePSQLError{
        return safeTransaction { when(constraint.type) {
            ConstraintType.DICE -> addDiceConstraint(choice, constraint as DiceConstraintViewModel)
            ConstraintType.SKILL -> addSkillConstraint(choice, constraint as SkillConstraintViewModel)
            ConstraintType.STATISTIC -> addStatisticConstraint(choice, constraint as StatisticConstraintViewModel)
            ConstraintType.ITEM -> addItemConstraint(choice, constraint as ItemConstraintViewModel)
        } }
    }

    private fun addDiceConstraint(choice: ChoiceViewModel, constraint: DiceConstraintViewModel) {
        DiceConstraint.new {
            adventure = contextAdventure!!.item.id
            this.choice = choice.item.id
            minValue = if(constraint.minValue.value == 0) null else constraint.minValue.value
            maxValue = if(constraint.maxValue.value == 0) null else constraint.maxValue.value
        }
    }

    private fun addSkillConstraint(choice: ChoiceViewModel, constraint: SkillConstraintViewModel) {
        SkillConstraint.new {
            adventure = contextAdventure!!.item.id
            this.choice = choice.item.id
            skill = constraint.skillViewModel.value.item
        }
    }

    private fun addStatisticConstraint(choice: ChoiceViewModel, constraint: StatisticConstraintViewModel) {
        StatisticConstraint.new {
            adventure = contextAdventure!!.item.id
            this.choice = choice.item.id
            statistic = constraint.statViewModel.value.item
            minValue = if(constraint.minValue.value == 0) null else constraint.minValue.value
            maxValue = if(constraint.maxValue.value == 0) null else constraint.maxValue.value
        }
    }

    private fun addItemConstraint(choice: ChoiceViewModel, constraint: ItemConstraintViewModel) {
        ItemConstraint.new {
            adventure = contextAdventure!!.item.id
            this.choice = choice.item.id
            item = constraint.itemViewModel.value.item
            quantity = constraint.quantity.value
            isConsumed = constraint.isConsumed.value
        }
    }

    fun removeConstraint(constraint: ConstraintViewModel) = safeTransaction { constraint.innerItem.item.delete() }
}