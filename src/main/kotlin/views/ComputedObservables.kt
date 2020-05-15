package views

import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.beans.value.ObservableValueBase
import javafx.scene.control.ComboBox
import javafx.scene.control.TableView
import tornadofx.booleanProperty
import tornadofx.editModel
import tornadofx.nonNullObjectBinding

fun <T> ObservableValue<out ObservableValue<T>>.flatten(): ObservableValue<T> = object: ObservableValueBase<T>() {
        init {
            this@flatten.addListener(this::externalUpdateHandler)
            this@flatten.value?.addListener(this::internalUpdateHandler)
        }

        private fun externalUpdateHandler(observable: ObservableValue<out ObservableValue<T>>?, oldValue: ObservableValue<T>, newValue: ObservableValue<T>) {
            oldValue.removeListener(this::internalUpdateHandler)
            newValue.addListener(this::internalUpdateHandler)
            fireValueChangedEvent()
        }

        private fun internalUpdateHandler(observable: ObservableValue<out T>?, oldValue: T, newValue: T) {
            fireValueChangedEvent()
        }

        override fun getValue() = this@flatten.value.value
    }

/**
 * Check if any item in the [TableView] is dirty
 */
val <T> TableView<T>.isDirty: ObservableValue<Boolean> get() {
    return nonNullObjectBinding(this, editModel.items) {
        with(editModel.items.map { it.value.dirty }) {
            if (this.isNotEmpty())
                reduce { acc, value -> acc.or(value) }
            else
                 booleanProperty(false)
        }
    }.flatten()
}

val <T> TableView<T>.anySelected: ObservableBooleanValue
get() = this.selectionModel.selectedItemProperty().isNotNull

val <T> ComboBox<T>.anySelected: ObservableBooleanValue
get() = this.selectionModel.selectedItemProperty().isNotNull