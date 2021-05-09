
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.*
import styled.*
import kotlin.js.Date
import kotlin.random.Random

external interface AppState :RState {
    var name :String
    var start :Boolean
}

@JsExport
class App :RComponent<RProps, AppState>() {
    private lateinit var random :Random
    private var cookies :MutableMap<String, String>? = null
    override fun AppState.init() {
        random = Random(Date.now().toLong())
        name = getCookie("name") ?: "Player${10+random.nextInt(90)}"
        start = false
    }

    private fun getCookie(name :String) :String? {
        if (cookies==null) {
            cookies = document.cookie
                .split(";\\s")
                .map { e -> e.split("=") }
                .filter { e -> e.size==2 }
                .map { e -> Pair(e[0], e[1]) }
                .toMap().toMutableMap()
        }
        return cookies!![name]
    }

    override fun RBuilder.render() {
        styledH1 {
            css {
                +SetStyles.title
            }
            +"Groups Game"
        }
        if (!state.start) {
            span { +"Please enter your name: " }
            styledInput {
                css {
                    +SetStyles.textInput
                }
                attrs {
                    type = InputType.text
                    value = state.name
                    onChangeFunction = { event ->
                        val inputElement = event.target as HTMLInputElement
                        setState {
                            name = inputElement.value
                        }
                    }
                }
            }
            button {
                attrs {
                    if (state.name.trim().length < 2)
                        disabled = true
                    onClickFunction = {
                        setCookie("name", state.name)
                        setState {
                            start = true
                        }
                    }
                }
                +"Start Game"
            }
        } else
            setsComponent {
                name = state.name
            }
    }

    private fun setCookie(name: String, value :String) {
        document.cookie = """$name=$value; path="/""""
    }
}

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            child(App::class) {}
        }
    }
}
