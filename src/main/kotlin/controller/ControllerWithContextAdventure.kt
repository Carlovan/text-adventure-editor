package controller

import model.Adventure
import tornadofx.Controller
import viewmodel.AdventureViewModel
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

open class ControllerWithContextAdventure() : Controller() {
    private val _contextAdventure: AdventureViewModel by inject()

    var contextAdventure: AdventureViewModel?
        get() = if (_contextAdventure.isEmpty) null else _contextAdventure
        set(newAdventure) {
            _contextAdventure.item = newAdventure?.item
        }

    protected inner class CachedProperty<T>(val getter: () -> T) {
        var value: T? = null
        var lastContextAdventure: Adventure? = null

        operator fun getValue(thisRef: ControllerWithContextAdventure, property: KProperty<*>): T {
            if (value == null || lastContextAdventure != thisRef.contextAdventure?.item) {
                lastContextAdventure = thisRef.contextAdventure?.item
                value = getter()
            }
            return value!!
        }

        fun invalidateData() {
            value = null
        }
    }

    protected fun <T> cachedProperty(getter: () -> T) = CachedProperty(getter)

    protected fun invalidateProperty(prop: KProperty0<*>) {
        prop.isAccessible = true
        (prop.getDelegate() as? CachedProperty<*>)?.invalidateData()
    }
}