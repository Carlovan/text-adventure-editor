package views

import ExposedScope
import javafx.scene.layout.BorderPane
import kotlinx.coroutines.*
import tornadofx.*


fun <T> UIComponent.runWithLoadingAsync(op: suspend () -> T) {
    val spinner = find<LoadingSpinner>()
    runLater {
        InternalWindow(escapeClosesWindow = false, closeButton = false, movable = false, icon = null, modal = true).apply {
            children.filterIsInstance<BorderPane>().first().top = null // Remove top bar
        }.open(spinner, owner = currentStage?.scene?.root ?: primaryStage.scene.root)
    }
    ExposedScope.launch { println(Thread.currentThread().name); op() }.invokeOnCompletion { runLater { spinner.close() } }
}

class LoadingSpinner : Fragment() {
    override val root = pane {
        progressindicator()
    }
}