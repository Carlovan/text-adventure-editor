package controller

import tornadofx.Controller
import viewmodel.AdventureViewModel

open class ControllerWithContextAdventure : Controller() {
    private val _contextAdventure: AdventureViewModel by inject()

    var contextAdventure: AdventureViewModel?
        get() = if (_contextAdventure.isEmpty) null else _contextAdventure
        set(newAdventure) {
            _contextAdventure.item = newAdventure?.item
        }
}