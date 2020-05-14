package viewmodel

import model.Step
import tornadofx.ItemViewModel

fun Step.fromViewModel(step: StepViewModel) {
    number = step.number.value
    text   = step.text.value
}

class StepViewModel : ItemViewModel<Step>() {
    val number = bind(Step::number)
    val text = bind(Step::text)
}