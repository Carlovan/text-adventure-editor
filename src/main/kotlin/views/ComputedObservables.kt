package views

import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.beans.value.ObservableValueBase
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.TableView
import tornadofx.booleanProperty
import tornadofx.editModel
import tornadofx.nonNullObjectBinding

fun <T> ObservableValue<out ObservableValue<T>>.flatten(): ObservableValue<T> = object : ObservableValueBase<T>() {
    val handler: (ObservableValue<out T>, T, T) -> Unit = { _, _, _ -> this.internalUpdateHandler() }

    init {
        this@flatten.addListener { _, oldValue, newValue -> this.externalUpdateHandler(oldValue, newValue) }
        this@flatten.value?.addListener(handler)
    }

    private fun externalUpdateHandler(oldValue: ObservableValue<T>, newValue: ObservableValue<T>) {
        oldValue.removeListener(handler)
        newValue.addListener(handler)
        fireValueChangedEvent()
    }

    private fun internalUpdateHandler() {
        fireValueChangedEvent()
    }

    override fun getValue() = this@flatten.value.value
}

/**
 * Check if any item in the [TableView] is dirty
 */
val <T> TableView<T>.isDirty: ObservableValue<Boolean>
    get() {
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

val <T> ListView<T>.anySelected: ObservableBooleanValue
    get() = this.selectionModel.selectedItemProperty().isNotNull