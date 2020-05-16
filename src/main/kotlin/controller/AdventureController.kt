package controller

import javafx.collections.ObservableList
import model.Adventure
import org.jetbrains.exposed.sql.transactions.transaction
import sqlutils.MaybePSQLError
import sqlutils.safeTransaction
import tornadofx.asObservable
import viewmodel.AdventureViewModel
import viewmodel.fromViewModel

class AdventureController : ControllerWithContextAdventure() {
    val adventures: ObservableList<AdventureViewModel>
        get() = transaction {
                    Adventure.all().map { AdventureViewModel().apply { item = it } }.asObservable()
                }

    fun createAdventure(adventure: AdventureViewModel, setAsContext: Boolean) : MaybePSQLError {
        val res = AdventureViewModel()
        val outcome = safeTransaction {
            Adventure.new {
                fromViewModel(adventure)
            }.also { res.item = it }
        }
        if (setAsContext) {
            this.contextAdventure = res
        }
        return outcome
    }

    fun deleteAdventure(adventure: AdventureViewModel): MaybePSQLError {
        return adventure.item?.let {
            if (contextAdventure?.item == it) {
                contextAdventure = null
            }
            safeTransaction {
                it.delete()
            }
        } ?: MaybePSQLError.empty()
    }
}