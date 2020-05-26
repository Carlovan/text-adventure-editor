package views.loot

import controller.LootController
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.LootViewModel
import views.MasterView
import views.errorAlert
import views.runWithLoading
import views.ui

class LootsView : MasterView<LootViewModel>("Loots") {
    private val controller: LootController by inject()
    private val loots = observableListOf<LootViewModel>()

    override val root = createRoot()

    override fun createDataTable(): TableView<LootViewModel> = tableview {
        items = loots

        enableCellEditing()
        enableDirtyTracking()

        column("Description", LootViewModel::desc).makeEditable()
        column("Total items", LootViewModel::itemsCount)

        smartResize()
    }

    private fun updateData() {
        runWithLoading { controller.loots } ui {
            loots.clear()
            loots.addAll(it)
        }
    }

    override fun newItem() {
        find<CreateLootModal>().openModal(block = true)
        updateData()
    }

    override fun deleteItem() {
        runWithLoading { controller.deleteLoot(dataTable.selectionModel.selectedItem) } ui {
            it.peek {
                errorAlert {
                    when (it) {
                        PSQLState.FOREIGN_KEY_VIOLATION -> "Cannot delete this loot, it is related to other entities"
                        else -> null
                    }
                }
            }.onEmpty {
                updateData()
            }
        }
    }

    override fun saveTable() {
        runWithLoading {
            with(dataTable.editModel) {
                controller.commit(items.asSequence()
                    .filter { it.value.isDirty }
                    .map { it.key })
            }
        } ui {
            it.peek {
                errorAlert {
                    when (it) {
                        PSQLState.UNIQUE_VIOLATION -> "Loot description is not unique!"
                        else -> null
                    }
                }
            }.onEmpty { dataTable.editModel.commit() }
        }
    }

    override fun openDetail() {
        runWithLoading { controller.createDetail(dataTable.selectedItem!!) } ui {
            replaceWith(find<DetailLootView>(DetailLootView::loot to it))
        }
    }

    override fun onDock() {
        updateData()
    }
}