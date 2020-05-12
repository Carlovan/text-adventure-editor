package views

import tornadofx.*

class CreateStepModal: Fragment() {
    override val root = form {
        fieldset("Create step") {
            field("Step name") {
                textfield {  }
            }
        }
    }
}