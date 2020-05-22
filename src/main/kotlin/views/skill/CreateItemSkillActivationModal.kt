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
import views.runWithLoadingAsync

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
                    runWithLoadingAsync {
                        controller.createItemSkillActivation(newItemSkillActivation)
                        .peek {
                            errorAlert {
                                when (it) {
                                    PSQLState.UNIQUE_VIOLATION -> "Another item is associated with this skill!"
                                    PSQLState.FOREIGN_KEY_VIOLATION -> "Another item is associated with this skill!"
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
        runWithLoadingAsync {
            itemViewModels.clear()
            itemViewModels.addAll(itemController.items)
        }
    }
}