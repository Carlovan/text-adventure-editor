package views.skill

import controller.SkillController
import viewmodel.SkillViewModel
import views.SelectObjectModal

class SelectSkillModal : SelectObjectModal<SkillViewModel>("Select skill") {
    private val controller: SkillController by inject()

    override fun getData() = controller.skills
    override fun cellFormatter(obj: SkillViewModel): String = obj.name.value
}