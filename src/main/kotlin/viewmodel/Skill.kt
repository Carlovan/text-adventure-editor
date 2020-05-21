package viewmodel

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty
import model.Skill
import tornadofx.ItemViewModel

fun Skill.fromViewModel(skill: SkillViewModel) {
    name = skill.name.value
}

class SkillViewModel(skill: Skill? = null) : ItemViewModel<Skill>(skill) {
    val name = bind(Skill::name)

    val type = SimpleStringProperty(listOf(if (skill?.isItemSkill == true) "Item" else "", if (skill?.isStatSkill == true) "Statistic" else "")
        .filter { it != "" }.joinToString()) as ReadOnlyStringProperty

    fun saveData() {
        item.name = name.value
    }
}