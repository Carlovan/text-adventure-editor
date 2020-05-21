package viewmodel

import model.Choice
import tornadofx.ItemViewModel
import tornadofx.property

fun Choice.fromViewModel(choice: ChoiceViewModel) {
    text = choice.text.value
    stepTo = choice.stepTo.value.item
}

class ChoiceViewModel(choice: Choice? = null) : ItemViewModel<Choice>(choice) {
    val text = bind(Choice::text)
    val stepTo = property(choice?.stepTo?.let { StepViewModel(it) }).fxProperty
}