package viewmodel

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import model.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import tornadofx.ItemViewModel
import tornadofx.asObservable

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

class DetailSkillViewModel(skill: Skill? = null) : ItemViewModel<Skill>(skill) {
    val name = bind(Skill::name)

    val itemSkillActivations get() = (ItemSkillActivations innerJoin Items)
        .slice(Items.id, ItemSkillActivations.quantity)
        .select { Items.id eq ItemSkillActivations.item and (ItemSkillActivations.skill eq item.id) }
        .map {
            ItemSkillActivationViewModel(item, Item.findById(it[Items.id]), it[ItemSkillActivations.quantity])
        }.asObservable()
}

class ItemSkillActivationViewModel(val skill: Skill, item: Item? = null, quantity: Int? = null) : ItemViewModel<Item>(item){
    val itemName = bind(Item::name) as ReadOnlyStringProperty
    val quantityRequired = SimpleIntegerProperty(this, "viewModelProperty", quantity ?: 1)

    val itemViewModel = SimpleObjectProperty(this, "vmp", viewmodel.ItemViewModel(item))

    fun saveData() {
        ItemSkillActivations.update({ ItemSkillActivations.skill eq skill.id and (ItemSkillActivations.item eq item.id) }) {
            it[ItemSkillActivations.quantity] = quantityRequired.value
        }
    }
}