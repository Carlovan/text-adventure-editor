package views

import javafx.scene.control.TableView
import tornadofx.*

abstract class MasterView<T>(title: String? = null) : View(title) {
    protected val dataTable: TableView<T> by lazy {
        createDataTable()
    }

    protected fun createButtons(withDetail: Boolean = true) =
        vbox {
            spacing = 10.0
            paddingAll = 10.0
            button("New") {
                maxWidth = Double.MAX_VALUE
                action(::newItem)
            }
            button("Delete") {
                maxWidth = Double.MAX_VALUE
                enableWhen { dataTable.anySelected }
                action(::deleteItem)
            }
            separator()
            button("Save") {
                maxWidth = Double.MAX_VALUE
                enableWhen(dataTable.isDirty)
                action(::saveTable)
            }
            button("Discard") {
                maxWidth = Double.MAX_VALUE
                enableWhen(dataTable.isDirty)
                action(::discardTable)
            }
            if (withDetail) {
                separator()
                button("Detail") {
                    maxWidth = Double.MAX_VALUE
                    enableWhen(dataTable.anySelected)
                    action(::openDetail)
                }
            }
        }

    protected abstract fun createDataTable(): TableView<T>

    protected fun createRoot(withDetail: Boolean = true) =
        borderpane {
            center = dataTable
            left = createButtons(withDetail)
        }


    protected abstract fun newItem()
    protected abstract fun deleteItem()
    protected abstract fun saveTable()
    protected open fun discardTable() {
        dataTable.editModel.rollback()
    }
    protected open fun openDetail() { throw NotImplementedError("You should override this") }
}