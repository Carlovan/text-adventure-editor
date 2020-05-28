package views.item

import controller.ItemController
import javafx.geometry.Pos
import onEmpty
import peek
import sqlutils.MaybePSQLError
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.ItemViewModel
import views.errorAlert
import views.runWithLoading
import views.ui

class CreateItemModal : Fragment("Create item") {
    private val controller: ItemController by inject()
    val item by param(ItemViewModel())

    override val root = borderpane {
        center {
            add<ItemForm>(ItemForm::item to item)
        }
        bottom {
            hbox {
                button("Create") {
                    enableWhen(item.valid)
                    alignment = Pos.BOTTOM_RIGHT
                    action {
                        runWithLoading { saveAction() } ui {
                            it.peek {
                                errorAlert {
                                    when (it) {
                                        PSQLState.UNIQUE_VIOLATION -> "Item name is not unique!"
                                        else -> null
                                    }
                                }
                            }.onEmpty { runLater { close() } }
                        }
                    }
                }
            }
        }
    }

    private fun saveAction(): MaybePSQLError =
        controller.createItem(item)
}