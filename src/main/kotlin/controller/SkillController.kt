package controller

import javafx.collections.ObservableList
import model.ItemSkillActivations
import model.Skill
import model.Skills
import model.StatisticsSkills
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.MaybePSQLError
import sqlutils.safeTransaction
import tornadofx.asObservable
import viewmodel.*
import java.util.*

class SkillController : ControllerWithContextAdventure() {
    val skills : ObservableList<SkillViewModel> get() =
        transaction {
            Skill.find { Skills.adventure eq contextAdventure!!.item.id }
                 .map { SkillViewModel(it) }
                 .asObservable()
        }

    fun commit(changes: Sequence<SkillViewModel>) =
        safeTransaction {
            changes.forEach {
                it.saveData()
                it.rollback()
            }
        }

    fun createSkill(newSkill: SkillViewModel) =
        safeTransaction {
            Skill.new {
                adventure = contextAdventure!!.item.id
                fromViewModel(newSkill)
            }
        }

    fun deleteSkill(skill: SkillViewModel) =
        safeTransaction {
            skill.item.delete()
        }

    fun getDetail(master: SkillViewModel) = transaction { DetailSkillViewModel(master.item) }

    fun createItemSkillActivation(newItemSkillActivation: ItemSkillActivationViewModel) =
        safeTransaction {
            ItemSkillActivations.insert {
                it[skill] = newItemSkillActivation.skill.id
                it[item] = newItemSkillActivation.itemViewModel.value.item.id
                it[quantity] = newItemSkillActivation.quantityRequired.value
            }
        }

    fun createStatSkillModifier(newStatiSkillModifier: StatSkillModifierViewModel) =
        safeTransaction {
            StatisticsSkills.insert {
                it[skill] = newStatiSkillModifier.skill.id
                it[statistic] = newStatiSkillModifier.statViewModel.value.item.id
                it[value] = newStatiSkillModifier.valueMod.value
            }
        }

    fun deleteStatModifier(statSkillModifierViewModel: StatSkillModifierViewModel?): MaybePSQLError {
        if (statSkillModifierViewModel != null) {
            return safeTransaction {
                StatisticsSkills.deleteWhere {
                    StatisticsSkills.skill eq statSkillModifierViewModel.skill.id and (StatisticsSkills.statistic eq statSkillModifierViewModel.statViewModel.value.item.id)
                }
            }
        }
        return MaybePSQLError.empty()
    }

    fun deleteItemActivation(itemSkillActivationViewModel: ItemSkillActivationViewModel?): MaybePSQLError {
        if (itemSkillActivationViewModel != null) {
            return safeTransaction {
                ItemSkillActivations.deleteWhere {
                    ItemSkillActivations.skill eq itemSkillActivationViewModel.skill.id and (ItemSkillActivations.item eq itemSkillActivationViewModel.itemViewModel.value.item.id)
                }
            }
        }
        return MaybePSQLError.empty()
    }
}