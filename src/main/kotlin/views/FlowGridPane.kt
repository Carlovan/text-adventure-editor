package views

import javafx.event.EventTarget
import tornadofx.opcr

fun EventTarget.flowgridpane(rows: Int, cols: Int, op: FlowGridPane.() -> Unit = {}) =
    opcr(this, FlowGridPane(cols, rows), op)