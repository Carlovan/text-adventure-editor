package viewmodel

import model.Step

class StepViewModel : CommittableItemViewModel<Step>() {
    val number = bind(Step::number)
    val text = bind(Step::text)
}