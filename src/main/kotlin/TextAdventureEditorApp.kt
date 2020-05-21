import javafx.stage.Stage
import tornadofx.App
import views.SelectAdventureView

class TextAdventureEditorApp: App(SelectAdventureView::class) {
    override fun start(stage: Stage) {
        stage.isMaximized = true
        super.start(stage)
    }
}