package views

import javafx.concurrent.Task
import javafx.scene.paint.Paint
import javafx.scene.shape.StrokeLineCap
import javafx.util.Duration
import tornadofx.*

// TODO remove window bar
fun <T> View.runWithLoading(op: FXTask<*>.() -> T): Task<T> {
    val spinner = find<LoadingSpinner>()
    val status = TaskStatus().apply { completed.addListener(ChangeListener { _, _, newValue -> if (newValue) spinner.close() }) }
    runLater { openInternalWindow(spinner, escapeClosesWindow = false, closeButton = false, movable = false) }
    return runAsync(status = status, func = op)
}

class LoadingSpinner : Fragment() {
    override val root = pane {
        val r = 20.0
        val p = 10.0
        prefHeight = 2 * (p + r)
        prefWidth = 2 * (p + r)
        path {
            rotate(Duration.seconds(4.0), 360, play = false).apply {
                setOnFinished { playFromStart() }
                play()
            }
            moveTo(p, p + r)
            arcTo {
                x = p + 2*r
                y = p + r
                isSweepFlag = true
                radiusX = r; radiusY = r
                strokeLineCap = StrokeLineCap.ROUND
                stroke = Paint.valueOf("blue")
                strokeWidth = 5.0
            }
        }
    }
}