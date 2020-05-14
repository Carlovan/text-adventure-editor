package controller

import javafx.collections.ObservableList
import model.Adventure
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.asObservable
import viewmodel.AdventureViewModel
import viewmodel.fromViewModel

class AdventureController : ControllerWithContextAdventure() {
    val adventures: ObservableList<AdventureViewModel>
        get() = transaction {
                    Adventure.all().map { AdventureViewModel().apply { item = it } }.asObservable()
                }

    fun createAdventure(adventure: AdventureViewModel) : AdventureViewModel {
        val res = AdventureViewModel()
        transaction {
            Adventure.new {
                fromViewModel(adventure)
            }.also { res.item = it }
        }
        return res
    }

    fun deleteAdventure(adventure: AdventureViewModel) {
        adventure.item?.let {
            if (contextAdventure?.item == it) {
                contextAdventure = null
            }
            transaction {
                it.delete()
            }
        }
    }
}