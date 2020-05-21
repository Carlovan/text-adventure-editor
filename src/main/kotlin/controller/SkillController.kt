package controller

import javafx.collections.ObservableList
import model.Skill
import model.Skills
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.safeTransaction
import tornadofx.asObservable
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
}