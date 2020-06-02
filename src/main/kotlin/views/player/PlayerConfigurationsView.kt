package views.player

import controller.PlayerConfigurationController
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.PlayerConfigurationViewModel
import views.MasterView
import views.errorAlert
import views.runWithLoading
import views.ui

class PlayerConfigurationsView : MasterView<PlayerConfigurationViewModel>("Player configurations") {
    private val controller: PlayerConfigurationController by inject()
    private val configurations = observableListOf<PlayerConfigurationViewModel>()

    override fun createDataTable(): TableView<PlayerConfigurationViewModel> =
        tableview(configurations) {
            enableDirtyTracking()
            enableCellEditing()

            column("Name", PlayerConfigurationViewModel::name).makeEditable()
            column("Max skills", PlayerConfigurationViewModel::maxSkills).makeEditable()

            smartResize()
        }

    override val root = createRoot(false)

    fun updateData() {
        runWithLoading { controller.playerConfigurations } ui {
            configurations.clear()
            configurations.addAll(it)
        }
    }

    override fun newItem() {
        find<CreatePlayerConfigurationModal>().openModal(block = true)
        updateData()
    }

    override fun deleteItem() {
        runWithLoading { controller.deleteConfiguration(dataTable.selectionModel.selectedItem) } ui {
            it.peek {
                errorAlert {
                    when (it) {
                        PSQLState.FOREIGN_KEY_VIOLATION -> "Cannot delete this configuration, it is related to other entities"
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
                        PSQLState.UNIQUE_VIOLATION -> "Name is not unique!"
                        else -> null
                    }
                }
            }.onEmpty { dataTable.editModel.commit() }
        }
    }

    override fun onDock() {
        updateData()
    }

}