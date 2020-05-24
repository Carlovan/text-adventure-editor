package views.skill

import controller.SkillController
import javafx.scene.control.TableView
import onEmpty
import org.jetbrains.exposed.sql.transactions.transaction
import peek
import sqlutils.PSQLState
import tornadofx.*
import viewmodel.DetailSkillViewModel
import viewmodel.ItemSkillActivationViewModel
import viewmodel.StatSkillModifierViewModel
import views.*

class DetailSkillView : Fragment() {
    private val controller: SkillController by inject()
    val skill: DetailSkillViewModel by param()
    var itemSkillActivationsTable: TableView<ItemSkillActivationViewModel> by singleAssign()
    val itemSkillActivations = observableListOf<ItemSkillActivationViewModel>()

    var statSkillModifiersTable: TableView<StatSkillModifierViewModel> by singleAssign()
    val statSkillModifiers = observableListOf<StatSkillModifierViewModel>()

    override val root = borderpane {
        paddingAll = 20.0
        center {
            vbox {
                spacing = 10.0
                label("Skill ${skill.name.value}")
                hbox {
                    val isa = find<ItemSkillActivationView>(ItemSkillActivationView::skill to skill)
                    isa.initData()
                    add(isa)
                    val ssm = find<StatSkillModifiersView>(StatSkillModifiersView::skill to skill)
                    ssm.initData()
                    add(ssm)
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

    private fun back() {
        replaceWith<SkillsView>()
    }

    override fun onDock() {
        updateData()
    }
}