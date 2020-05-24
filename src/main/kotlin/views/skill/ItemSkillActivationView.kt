package views.skill

import controller.ItemController
import controller.SkillController
import controller.StatisticController
import ellipses
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TableView
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailSkillViewModel
import viewmodel.ItemSkillActivationViewModel
import viewmodel.ItemViewModel
import viewmodel.StatSkillModifierViewModel
import viewmodel.StatisticViewModel
import views.MasterView
import views.errorAlert
import views.runWithLoading
import views.ui

class ItemSkillActivationView : MasterView<ItemSkillActivationViewModel>("Item skill activations") {
    val controller: SkillController by inject()
    val skill: DetailSkillViewModel by param()

    val itemSkillActivations = observableListOf<ItemSkillActivationViewModel>()

    override val root = vbox {
        label("Item activations")
        add(createRoot())
    }

    override fun createDataTable(): TableView<ItemSkillActivationViewModel> =
        tableview {
            items = itemSkillActivations

            enableCellEditing()
            enableDirtyTracking()

            column("Item", ItemSkillActivationViewModel::itemName)
            column(
                "Quantity required",
                ItemSkillActivationViewModel::quantityRequired
            ).makeEditable()

            smartResize()
        }

    fun initData() {
        itemSkillActivations.clear()
        itemSkillActivations.addAll(skill.itemSkillActivations)
    }

    private fun updateData() {
        runWithLoading { skill.itemSkillActivations } ui {
            itemSkillActivations.clear()
            itemSkillActivations.addAll(it)
        }
    }

    override fun newItem() {
        find<CreateItemSkillActivationModal>(CreateItemSkillActivationModal::skill to skill).openModal(block = true)
        updateData()
    }

    override fun deleteItem() {
        controller.deleteItemActivation(dataTable.selectedItem)
            .peek {
                errorAlert { "Error occurred while deleting!" }
            }.onEmpty { updateData() }
    }

    override fun saveTable() {
        with(dataTable.editModel) {
            controller.saveItemSkillActivations(
                items.asSequence().filter { it.value.isDirty }.map { it.key }
            ).peek {
                errorAlert {
                    when(it) {
                        PSQLState.CHECK_VIOLATION -> "Quantity required must be a positive number!"
                        else -> null
                    }
                }
            }.onEmpty { commit() }
        }
    }
}

class CreateItemSkillActivationModal: Fragment("Create item skill activation") {
    private val controller: SkillController by inject()
    val itemController: ItemController by inject()
    val skill: DetailSkillViewModel by param()

    var itemViewModels: ObservableList<ItemViewModel> = observableListOf()
    var newItemSkillActivation: ItemSkillActivationViewModel by singleAssign()

    init {
        newItemSkillActivation = ItemSkillActivationViewModel(skill.item)
    }

    override val root = form {
        fieldset("New item activation") {
            field("Item required") {
                combobox(property = newItemSkillActivation.itemViewModel) {
                    items = itemViewModels
                    cellFormat { text = "[${it.slotName.value}] ${it.name.value.ellipses(30)}" }
                    required()
                }
            }
            field("Quantity required") {
                textfield(newItemSkillActivation.quantityRequired).required()
            }
        }
        hbox {
            button("Create") {
                enableWhen(newItemSkillActivation.valid)
                alignment = Pos.BOTTOM_RIGHT
                action {
                    runWithLoading { controller.createItemSkillActivation(newItemSkillActivation) } ui {
                        it.peek {
                            errorAlert {
                                when (it) {
                                    PSQLState.UNIQUE_VIOLATION -> "This skill is already activated by this item!"
                                    PSQLState.FOREIGN_KEY_VIOLATION -> "This skill is already activated by this item!"
                                    PSQLState.CHECK_VIOLATION -> "Quantity required must be a positive number!"
                                    else -> null
                                }
                            }
                        }.onEmpty { runLater { close() } }
                    }
                }
            }
        }
    }

    override fun onDock() {
        runWithLoading { itemController.items } ui {
            itemViewModels.clear()
            itemViewModels.addAll(it)
        }
    }
}