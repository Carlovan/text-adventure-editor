package viewmodel

import javafx.beans.property.SimpleObjectProperty
import tornadofx.ItemViewModel

/**
 * This is a [SimpleObjectProperty] with the ability to set the value "silently", without firing the Changed event
 */
class SilentiableSimpleObjectProperty<T>(initialValue: T? = null) : SimpleObjectProperty<T>(initialValue) {
    // When this is true, the Changed event is not fired
    private var silent = false

    override fun fireValueChangedEvent() {
        if (!silent)
            super.fireValueChangedEvent()
    }

    /**
     * Set the value without firing the Changed event
     */
    fun setSilent(value: T) {
        val oldSilent = silent
        silent = true
        set(value)
        silent = oldSilent
    }
}

/**
 * This is an [ItemViewModel] with the ability to commit dirty properties to a given existing object.
 */
open class CommittableItemViewModel<T>(initialValue: T? = null) : ItemViewModel<T>(initialValue, SilentiableSimpleObjectProperty(initialValue)) {
    /**
     * Set the underlying item to the given object and commit data to it
     */
    fun commitTo(receiver: T) {
        (itemProperty as SilentiableSimpleObjectProperty).setSilent(receiver)
        commit()
    }
}
