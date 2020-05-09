package controller

import javafx.collections.ObservableList
import model.Adventure
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.asObservable
import viewmodel.AdventureViewModel

class AdventureController : AbstractController() {
    val adventures : ObservableList<AdventureViewModel> by lazy {
        transaction {
            Adventure.all().map { AdventureViewModel().apply { item = it} }.asObservable()
        }
    }
}