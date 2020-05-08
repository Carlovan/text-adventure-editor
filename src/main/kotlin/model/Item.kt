package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object ItemSlots : AdventureTable("ITEM_SLOT") {
    val name = varchar("name", 32)
    val capacity = integer("capacity").check { it greaterEq 0 } // If 0 capacity is infinite

    init {
        uniqueIndex(adventure, name)
    }
}

object PlayerAvailableSlots : Table("PLAYER_AVAILABLE_SLOT") {
    val loot = reference("loot", Loots)
    val item = reference("item", Items)
    val name = varchar("name", 32)
    override val primaryKey = PrimaryKey(loot, item, name)
}

object Items : AdventureTable("ITEM") {
    val name = varchar("name", 64)
    val isConsumable = bool("is_consumable")
    val itemSlot = reference("item_slot", ItemSlots)

    init {
        uniqueIndex(adventure, name)
    }
}

object Loots : AdventureTable("LOOT") {
    val desc = varchar("desc", 64)
}

object LootsItems : Table("LOOT_ITEM") {
    val loot = reference("loot", Loots)
    val item = reference("item", Items)
    val quantity = integer("quantity").check { it greater 0 }
    override val primaryKey = PrimaryKey(loot, item)
}