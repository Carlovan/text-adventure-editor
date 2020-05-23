package views.skill

import controller.SkillController
import javafx.scene.control.TableView
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import viewmodel.DetailSkillViewModel
import viewmodel.ItemSkillActivationViewModel
import views.anySelected
import views.isDirty
import views.runWithLoading
import views.ui

class DetailSkillView : Fragment() {
    private val controller: SkillController by inject()
    val skill: DetailSkillViewModel by param()
    var itemSkillActivationsTable: TableView<ItemSkillActivationViewModel> by singleAssign()
    val itemSkillActivations = observableListOf<ItemSkillActivationViewModel>()

    override val root = borderpane {
        paddingAll = 20.0
        center {
            vbox {
                spacing = 10.0
                label("Skill ${skill.name.value}")
                borderpane {
                    left = vbox {
                        label("Item activations:")
                        borderpane {
                            itemSkillActivationsTable = tableview {
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
                            left = vbox {
                                spacing = 10.0
                                paddingAll = 10.0
                                button("New") {
                                    maxWidth = Double.MAX_VALUE
                                    action {
                                        find<CreateItemSkillActivationModal>(CreateItemSkillActivationModal::skill to skill).openModal(
                                            block = true
                                        )
                                        updateData()
                                    }
                                }
                                button("Delete") {
                                    maxWidth = Double.MAX_VALUE
                                    enableWhen { itemSkillActivationsTable.anySelected }
                                }
                                separator()
                                button("Save") {
                                    maxWidth = Double.MAX_VALUE
                                    enableWhen(itemSkillActivationsTable.isDirty)
                                }
                                button("Discard") {
                                    maxWidth = Double.MAX_VALUE
                                    enableWhen(itemSkillActivationsTable.isDirty)
                                }
                            }
                            center = itemSkillActivationsTable
                        }
                    }
                }
            }
        }
        bottom {
            hbox {
                button("Back") {
                    action {
                        replaceWith<SkillsView>()
                    }
                }
            }
        }
    }

    private fun updateData() {
        runWithLoading { transaction { skill.itemSkillActivations } } ui {
            itemSkillActivations.clear()
            itemSkillActivations.addAll(it)
        }
    }
//
//    private fun save() {
//        runWithLoading {
//            controller.commit(skill)
//        } ui {
//            it.peek {
//                errorAlert { "An error occurred" }
//            }
//        }
//    }

    private fun back() {
        replaceWith<SkillsView>()
    }

    private fun addItemSkillActivation() {
        find<CreateItemSkillActivationModal>(CreateItemSkillActivationModal::skill to skill).openModal(block = true)
        updateData()
    }

    override fun onDock() {
        updateData()
    }
}