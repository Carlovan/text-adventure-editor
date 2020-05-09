package viewmodel

import model.Step
import tornadofx.*

class StepViewModel : ItemViewModel<Step>() {
    val number = bind(Step::number)
    val text = bind(Step::text)
}