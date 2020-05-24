package views.skill

import controller.ItemController
import controller.SkillController
import ellipses
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import onEmpty
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailSkillViewModel
import viewmodel.ItemSkillActivationViewModel
import viewmodel.ItemViewModel
import views.errorAlert
import views.runWithLoading
import views.ui

class CreateItemSkillActivationModal: Fragment("Create item skill activation") {
    private val controller: SkillController by inject()
    val itemController: ItemController by inject()
    val skill: DetailSkillViewModel by param()

    var itemViewModels: ObservableList<ItemViewModel> = observableListOf()
    var itemsCombobox: ComboBox<ItemViewModel> by singleAssign()
    var newItemSkillActivation: ItemSkillActivationViewModel by singleAssign()

    init {
        newItemSkillActivation = ItemSkillActivationViewModel(skill.item)
    }

    override val root = form {
        fieldset("New item activation") {
            field("Item required") {
                itemsCombobox = combobox(property = newItemSkillActivation.itemViewModel) {
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