package controller

import javafx.collections.ObservableList
import model.Adventure
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.Controller
import tornadofx.asObservable
import viewmodel.AdventureViewModel

class AdventureController : ControllerWithContextAdventure() {
    val adventures: ObservableList<AdventureViewModel>
        get() = transaction {
                    Adventure.all().map { AdventureViewModel().apply { item = it } }.asObservable()
                }

    fun createAdventure(adventure: AdventureViewModel) : AdventureViewModel {
        val res = AdventureViewModel()
        transaction {
            Adventure.new {
                name = adventure.name.value
            }.also { res.item = it }
        }
        return res
    }
}