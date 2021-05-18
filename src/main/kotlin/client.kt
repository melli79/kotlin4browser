
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
    var setSize :Int
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
        return cookies!![name]?.replace("\"", "")
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
            span { +" " }
            button {
                attrs {
                    if (state.name.trim().length < 2)
                        disabled = true
                    onClickFunction = {
                        setCookie("name", state.name)
                        setState {
                            start = true
                            setSize = 3
                        }
                    }
                }
                +"Training"
            }
            span { +" " }
            button {
                attrs {
                    if (state.name.trim().length < 2)
                        disabled = true
                    onClickFunction = {
                        setCookie("name", state.name)
                        setState {
                            start = true
                            setSize = 4
                        }
                    }
                }
                +"Easy"
            }
            span { +" " }
            button {
                attrs {
                    if (state.name.trim().length < 2)
                        disabled = true
                    onClickFunction = {
                        setCookie("name", state.name)
                        setState {
                            start = true
                            setSize = 5
                        }
                    }
                }
                +"Medium"
            }
            span { +" " }
            button {
                attrs {
                    if (state.name.trim().length < 2)
                        disabled = true
                    onClickFunction = {
                        setCookie("name", state.name)
                        setState {
                            start = true
                            setSize = 6
                        }
                    }
                }
                +"Hard"
            }
        } else
            setsComponent {
                name = state.name
                setSize = state.setSize
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
