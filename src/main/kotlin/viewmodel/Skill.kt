package viewmodel

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import model.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import tornadofx.ItemViewModel
import tornadofx.asObservable

fun Skill.fromViewModel(skill: SkillViewModel) {
    name = skill.name.value
}

class SkillViewModel(skill: Skill? = null) : ItemViewModel<Skill>(skill) {
    private val id = bind(Skill::id)
    val name = bind(Skill::name)

    val type = SimpleStringProperty(listOf(if (skill?.isItemSkill == true) "Item" else "", if (skill?.isStatSkill == true) "Statistic" else "")
        .filter { it != "" }.joinToString()) as ReadOnlyStringProperty

    fun saveData() {
        item.name = name.value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SkillViewModel

        if (id.value != other.id.value) return false

        return true
    }

    override fun hashCode(): Int {
        return id.value.hashCode()
    }
}

class DetailSkillViewModel(skill: Skill? = null) : ItemViewModel<Skill>(skill) {
    val name = bind(Skill::name)

    val itemSkillActivations get() = transaction {
        (ItemSkillActivations innerJoin Items)
            .slice(Items.id, ItemSkillActivations.quantity)
            .select { Items.id eq ItemSkillActivations.item and (ItemSkillActivations.skill eq item.id) }
            .map {
                ItemSkillActivationViewModel(item, Item.findById(it[Items.id]), it[ItemSkillActivations.quantity])
            }.asObservable()
    }

    val statSkillModifiers get() = transaction {
        (StatisticsSkills innerJoin Statistics)
            .slice(Statistics.id, StatisticsSkills.value)
            .select { Statistics.id eq StatisticsSkills.statistic and (StatisticsSkills.skill eq item.id) }
            .map {
                StatSkillModifierViewModel(item, Statistic.findById(it[Statistics.id]), it[StatisticsSkills.value])
            }.asObservable()
    }
}

class ItemSkillActivationViewModel(val skill: Skill, item: Item? = null, quantity: Int? = null) : ItemViewModel<Item>(item){
    val itemName = bind(Item::name) as ReadOnlyStringProperty
    val quantityRequired = SimpleIntegerProperty(this, "viewModelProperty", quantity ?: 1)

    val itemViewModel = SimpleObjectProperty<viewmodel.ItemViewModel>(this, "vmp", item?.let { viewmodel.ItemViewModel(it) } )

    fun saveData() {
        ItemSkillActivations.update({ ItemSkillActivations.skill eq skill.id and (ItemSkillActivations.item eq item.id) }) {
            it[ItemSkillActivations.quantity] = quantityRequired.value
        }
    }
}

class StatSkillModifierViewModel(val skill: Skill, stat: Statistic? = null, valueMod: Int? = null) : ItemViewModel<Statistic>(stat) {
    val statName = bind(Statistic::name) as ReadOnlyStringProperty
    val valueMod = SimpleIntegerProperty(this, "viewModelProperty", valueMod ?: 0)

    val statViewModel = SimpleObjectProperty(this, "vmp", StatisticViewModel(stat))

    fun saveData() {
        StatisticsSkills.update({ StatisticsSkills.skill eq skill.id and (StatisticsSkills.statistic eq item.id) }) {
            it[StatisticsSkills.value] = valueMod.value
        }
    }
}