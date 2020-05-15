package views

import javafx.concurrent.Task
import javafx.scene.layout.BorderPane
import tornadofx.*

fun <T> UIComponent.runWithLoading(op: FXTask<*>.() -> T): Task<T> {
    val spinner = find<LoadingSpinner>()
    val status = TaskStatus().apply { completed.addListener(ChangeListener { _, _, newValue -> if (newValue) spinner.close() }) }
    runLater {
        InternalWindow(escapeClosesWindow = false, closeButton = false, movable = false, icon = null, modal = true).apply {
            children.filterIsInstance<BorderPane>().first().top = null // Remove top bar
        }.open(spinner, owner = currentStage?.scene?.root ?: primaryStage.scene.root)
    }
    return runAsync(status = status, func = op)
}

class LoadingSpinner : Fragment() {
    override val root = pane {
        progressindicator()
    }
}