package controller

import javafx.collections.ObservableList
import model.ItemSkillActivations
import model.Skill
import model.Skills
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.safeTransaction
import tornadofx.asObservable
import viewmodel.DetailSkillViewModel
import viewmodel.ItemSkillActivationViewModel
import viewmodel.SkillViewModel
import viewmodel.fromViewModel

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
}