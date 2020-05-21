package views

import javafx.event.EventHandler
import javafx.scene.control.Alert
import javafx.scene.layout.Region
import javafx.stage.Modality
import tornadofx.UIComponent
import tornadofx.View
import tornadofx.runLater

fun UIComponent.errorAlert(contentSupplier: () -> String?) {
    Alert(Alert.AlertType.ERROR).apply {
        headerText = "An error occurred"
        contentText = contentSupplier()
        dialogPane.minHeight = Region.USE_PREF_SIZE
        initModality(Modality.APPLICATION_MODAL)
        initOwner(primaryStage)
        isResizable = true
        onShown = EventHandler { // Workaround https://stackoverflow.com/questions/55190380/javafx-creates-alert-dialog-which-is-too-small
            runLater { isResizable = false }
        }
    }.show()
}