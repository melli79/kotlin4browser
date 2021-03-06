import kotlinx.browser.document
import kotlinx.css.*
import kotlinx.html.*
import kotlinx.html.js.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import react.*
import react.dom.*
import styled.*
import kotlin.js.Date
import kotlin.random.Random

enum class Step {
    Hello, Play
}

external interface AppState :RState {
    var name :String
    var step :Step
    var size :Int
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class App :RComponent<RProps, AppState>() {
    private var cookies :MutableMap<String, String>? = null

    override fun AppState.init() {
        name = getCookie("name") ?: "Player${10+Random(Date.now().toLong()).nextInt(90)}"
        step = Step.Hello
        size = 3
    }

    private fun getCookie(name:String) :String? {
        if (cookies == null) {
            val cookieAssignments = document.cookie.split(";\\s")
            console.log(cookieAssignments.joinToString("; "))
            cookies = cookieAssignments.map { a ->
                val keyValue = a.split("=")
                if (keyValue[0].isNotBlank() && keyValue.size>1 && keyValue[1].isNotBlank())
                    Pair(keyValue[0].trim(), keyValue[1].trim())
                else
                    null
            }.filterNotNull().toMap().toMutableMap()
            console.log("Found ${cookies!!.size} cookies.")
        }
        return cookies!![name]?.replace("\"","")
    }

    private fun setNameCookie(name :String) {
        document.cookie = "name=$name;path=/"
    }

    override fun RBuilder.render() {
        h1 {
            +"Hello, ${state.name}!"
        }
        if (state.step == Step.Hello) {
            span {
                +"Enter your name: "
            }
            input {
                attrs {
                    type = InputType.text
                    value = state.name
                    onChangeFunction = { event ->
                        setState {
                            name = (event.target as HTMLInputElement).value
                        }
                    }
                    onKeyUpFunction = { event -> // why is the argument no KeyboardEvent??
                        if (event is KeyboardEvent && event.keyCode == 13)
                            setState {
                                step = Step.Play
                            }
                    }
                }
            }
            styledP {
                css {
                    height = 10.px
                }
            }
            button {
                attrs {
                    onClickFunction = {
                        setState {
                            size = 3
                            step = Step.Play
                        }
                    }
                }
                +"3x3 Game"
            }
            span { +" "}
            button {
                attrs {
                    onClickFunction = {
                        setState {
                            size = 4
                            step = Step.Play
                        }
                    }
                }
                +"4x4 Game"
            }
            span { +" "}
            button {
                attrs {
                    onClickFunction = {
                        setState {
                            size = 5
                            step = Step.Play
                        }
                    }
                }
                +"5x5 Game"
            }
        } else
            styledDiv {
                css {
                    alignItems = Align.center
                }
                board {
                    attrs {
                        size = state.size
                    }
                }
            }
    }
}
