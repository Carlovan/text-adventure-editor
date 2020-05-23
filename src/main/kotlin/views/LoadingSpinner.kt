package views

import ExposedExecutor
import javafx.scene.layout.BorderPane
import tornadofx.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

fun <T> UIComponent.runWithLoading(op: () -> T): CompletableFuture<T> {
    val spinner = find<LoadingSpinner>()
    runLater {
        InternalWindow(escapeClosesWindow = false, closeButton = false, movable = false, icon = null, modal = true).apply {
            children.filterIsInstance<BorderPane>().first().top = null // Remove top bar
        }.open(spinner, owner = currentStage?.scene?.root ?: primaryStage.scene.root)
    }
    return CompletableFuture.supplyAsync(Supplier {
        println(Thread.currentThread().name)
        op()
    }, ExposedExecutor).whenComplete { _, _ -> runLater { spinner.close() } }
}

infix fun <T> CompletableFuture<T>.ui(op: (T) -> Unit) {
    this.thenAccept {
        runLater {
            op(it)
        }
    }
}

class LoadingSpinner : Fragment() {
    override val root = pane {
        progressindicator()
    }
}