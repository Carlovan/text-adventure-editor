package views.skill

import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import viewmodel.DetailSkillViewModel
import viewmodel.ItemSkillActivationViewModel
import views.runWithLoading
import views.ui

class DetailSkillView : Fragment() {
    val skill: DetailSkillViewModel by param()
    private val itemSkillActivations = observableListOf<ItemSkillActivationViewModel>()

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
                    action(::back)
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