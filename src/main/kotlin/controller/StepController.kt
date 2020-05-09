package controller

import javafx.collections.ObservableList
import model.Step
import model.Steps
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.Controller
import tornadofx.asObservable
import tornadofx.observable
import viewmodel.AdventureViewModel
import viewmodel.StepViewModel
import java.util.*

class StepController : AbstractController() {
    val adventure: AdventureViewModel by inject()

    val steps : ObservableList<StepViewModel> by lazy {
        transaction {
            Step.find { Steps.adventure eq adventure.item.id }.map {
                StepViewModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    fun commit(changes: Sequence<StepViewModel>) {
        transaction {
            changes.forEach{ it.commit() }
        }
    }
}