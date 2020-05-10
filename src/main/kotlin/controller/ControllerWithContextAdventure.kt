package controller

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import tornadofx.Controller
import viewmodel.AdventureViewModel
import java.sql.Connection

open class ControllerWithContextAdventure() : Controller() {
    private val _contextAdventure: AdventureViewModel by inject()

    var contextAdventure: AdventureViewModel?
        get() = if (_contextAdventure.isEmpty) null else _contextAdventure
        set(newAdventure: AdventureViewModel?) {
            _contextAdventure.item = newAdventure?.item
        }
}