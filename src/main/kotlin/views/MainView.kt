package views

import tornadofx.*
import kotlin.properties.Delegates

class Greeting(var name: String, gr: String? = null) {
    var greet: String by Delegates.observable(gr ?: "Hi")
    val final
        get() = toString()

    override fun toString() = "$greet, $name"
}

class MainView: View() {
    override val root = vbox {
        val greetings = observableListOf<Greeting>()
        val ff = textfield()
        val drop = combobox(values = listOf("Hi", "Hello", "Good morning", "Your grace"))
        val outLbl = label()
        button("Hi") {
            action {
                val tmp = Greeting(ff.text, drop.selectedItem)
                outLbl.text = tmp.toString()
                greetings.add(tmp)
            }
        }
        tableview(greetings) {
            readonlyColumn("Greet", Greeting::greet)
            column("Name", Greeting::name).makeEditable()
            readonlyColumn("Final", Greeting::final)
        }
    }
}
