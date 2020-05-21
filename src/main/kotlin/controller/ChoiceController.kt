package controller

import model.Choice
import sqlutils.safeTransaction
import viewmodel.ChoiceViewModel
import viewmodel.DetailStepViewModel
import viewmodel.fromViewModel

class ChoiceController: ControllerWithContextAdventure() {
    fun createChoice(choice: ChoiceViewModel, fromStep: DetailStepViewModel) =
        safeTransaction {
            Choice.new {
                stepFrom = fromStep.item
                adventure = contextAdventure!!.item
                fromViewModel(choice)
            }
        }

    fun deleteChoice(choice: ChoiceViewModel) =
        safeTransaction {
            choice.item.delete()
        }
}