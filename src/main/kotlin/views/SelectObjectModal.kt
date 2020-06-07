package views

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import tornadofx.*

/**
 * Modal used to select something to be set somewhere.
 * Use the `selectedObject` property to get the selected object.
 * To correctly show the selected item in the combobox you should implement `equals` and `hashCode` for `T`
 */
abstract class SelectObjectModal<T>(title: String = "", objectName: String) : Fragment(title) {
    /**
     * This is used as return value. Can be set as parameter from TornadoFX or can be accessed from the object of [views.loot.SelectLootModal] itself.
     */
    val selectedObject by param(SimpleObjectProperty<T>())
    private val oldValue = selectedObject.value
    private val objects = observableListOf<T>()

    abstract fun cellFormatter(obj: T): String
    abstract fun getData(): Collection<T>

    override val root: Parent = form {
        fieldset("Add $objectName") {
            vbox {
                spacing = 10.0
                label("Select $objectName")
                combobox(selectedObject, objects) {
                    items.onChange {
                        val tmp = selectionModel.selectedItem
                        selectionModel.clearSelection()
                        selectionModel.select(tmp)
                    }
                    cellFormat {
                        text = cellFormatter(it)
                    }
                }
            }

        }
        hbox {
            spacing = 10.0
            button("Select") {
                enableWhen(selectedObject.isNotNull)
                action(::selectObject)
            }
            button("Cancel") {
                action(::cancel)
            }
        }
    }

    private fun updateData() {
        runWithLoading { getData() } ui {
            objects.clear()
            objects.addAll(it)
        }
    }

    protected open fun selectObject() {
        if (selectedObject.isNull.value) {
            errorAlert { "There is nothing selected!" }
        } else {
            close()
        }
    }

    protected open fun cancel() {
        selectedObject.value = oldValue
        close()
    }

    override fun onDock() {
        runLater {
            updateData()
        }
    }
}