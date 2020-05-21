package viewmodel

import model.Choice
import tornadofx.ItemViewModel

fun Choice.fromViewModel(choice: ChoiceViewModel) {
    text = choice.text.value
    stepTo = choice.stepTo.value
}

class ChoiceViewModel(choice: Choice? = null) : ItemViewModel<Choice>(choice) {
    val text = bind(Choice::text)
    val stepTo = bind(Choice::stepTo)
}