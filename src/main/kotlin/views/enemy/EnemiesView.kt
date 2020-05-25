package views.enemy

import controller.EnemyController
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.EnemyViewModel
import views.MasterView
import views.errorAlert
import views.runWithLoading
import views.ui

class EnemiesView : MasterView<EnemyViewModel>("Enemies") {
    val controller: EnemyController by inject()

    val enemies: ObservableList<EnemyViewModel> = observableListOf()

    override fun createDataTable(): TableView<EnemyViewModel> =
        tableview {
            items = enemies

            enableCellEditing()
            enableDirtyTracking()

            column("Name", EnemyViewModel::name).makeEditable()
            column("Statistics", EnemyViewModel::statisticsDescription)

            smartResize()
        }

    override val root = createRoot(false)

    private fun updateData() {
        runWithLoading { controller.enemies } ui {
            enemies.clear()
            enemies.addAll(it)
        }
    }

    override fun onDock() {
        updateData()
    }

    override fun newItem() {
        find<CreateEnemyModal>().openModal(block = true)
        updateData()
    }

    override fun deleteItem() {
        runWithLoading { controller.deleteEnemy(dataTable.selectionModel.selectedItem) } ui {
            updateData()
        }
    }

    override fun saveTable() {
        TODO("not implemented")
    }
}

class CreateEnemyModal : Fragment("Create enemy") {
    private val controller: EnemyController by inject()
    private val newEnemy = EnemyViewModel()

    override val root = form {
        fieldset("Enemy") {
            field("Name") {
                textfield(newEnemy.name).required()
            }
        }
        hbox {
            button("Create") {
                enableWhen(newEnemy.valid)
                alignment = Pos.BOTTOM_RIGHT
                action {
                    runWithLoading { controller.createEnemy(newEnemy) } ui {
                        it.peek {
                            errorAlert {
                                when (it) {
                                    PSQLState.UNIQUE_VIOLATION -> "Enemy name is not unique!"
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