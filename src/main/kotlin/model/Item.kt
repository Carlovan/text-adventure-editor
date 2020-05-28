package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Table

object ItemSlots : AdventureTable("ITEM_SLOT") {
    val name = varchar("name", 32)
    val capacity = integer("capacity").check { it greaterEq 0 } // If 0 capacity is infinite

    init {
        uniqueIndex(adventure, name)
    }
}

class ItemSlot(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ItemSlot>(ItemSlots)

    var adventure by ItemSlots.adventure

    var name by ItemSlots.name
    var capacity by ItemSlots.capacity
}

object PlayerAvailableSlots : Table("PLAYER_AVAILABLE_SLOT") {
    val playerConf = reference("player_conf", PlayerConfigurations)
    val itemSlot = reference("item_slot", ItemSlots)
    val name = varchar("name", 32)
    override val primaryKey = PrimaryKey(playerConf, itemSlot, name)
}

object Items : AdventureTable("ITEM") {
    val name = varchar("name", 64)
    val isConsumable = bool("is_consumable")
    val itemSlot = reference("item_slot", ItemSlots)

    init {
        uniqueIndex(adventure, name)
    }
}

class Item(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Item>(Items)

    var adventure by Items.adventure

    var name by Items.name
    var isConsumable by Items.isConsumable

    var itemSlot by ItemSlot referencedOn Items.itemSlot
}

object Loots : AdventureTable("LOOT") {
    val desc = varchar("desc", 64)
}

class Loot(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Loot>(Loots)

    var adventure by Loots.adventure

    var desc by Loots.desc
}

object LootsItems : Table("LOOT_ITEM") {
    val loot = reference("loot", Loots)
    val item = reference("item", Items)
    val quantity = integer("quantity").check { it greater 0 }
    override val primaryKey = PrimaryKey(loot, item)
}