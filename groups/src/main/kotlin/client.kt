
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.InputType
import org.w3c.dom.*
import react.*
import react.dom.*
import react.dom.events.ChangeEvent
import styled.*
import kotlin.js.Date
import kotlin.random.Random

external interface AppState :State {
    var name :String
    var start :Boolean
    var setSize :Int
}

class App :RComponent<Props, AppState>() {
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
                +GroupsStyles.title
            }
            +"Groups Game"
        }
        if (!state.start) {
            span { +"Please enter your name: " }
            styledInput {
                css {
                    +GroupsStyles.textInput
                }
                attrs {
                    type = InputType.text
                    value = state.name
                    onChange = { event :ChangeEvent<*> ->
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
                    onClick = {
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
                    onClick = {
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
                    onClick = {
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
                    onClick = {
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
        render(document.getElementById("root")!!) {
            child(App::class) {}
        }
    }
}
