package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Adventures : IntIdTable("ADVENTURE") {
    val name = varchar("name", 64)

    init {
        uniqueIndex(name)
    }
}

class Adventure(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Adventure>(Adventures)

    var name by Adventures.name

    val steps by Step referrersOn Steps.adventure
    val playerConfigurations by PlayerConfiguration referrersOn PlayerConfigurations.adventure

    fun assignFrom(other: AdventureCreate) {
        name = other.name
    }
}

class AdventureCreate(var name: String = "")

abstract class AdventureTable(name: String) : IntIdTable(name) {
    val adventure = reference("adventure", Adventures)
}
