package views.loot

import controller.LootController
import ellipses
import viewmodel.LootViewModel
import views.SelectObjectModal

/**
 * Modal used to select a loot to be set somewhere.
 * Use the `selectedObject` property to get the selected loot
 */
class SelectLootModal : SelectObjectModal<LootViewModel>("Select loot", "loot") {
    private val controller: LootController by inject()

    override fun getData() = controller.loots
    override fun cellFormatter(obj: LootViewModel) = obj.desc.value.ellipses(30)
}