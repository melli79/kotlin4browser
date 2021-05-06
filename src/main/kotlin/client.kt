import react.dom.render
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
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
    var startGame :Boolean
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class App :RComponent<RProps, AppState>() {
    private var cookies :MutableMap<String, String>? = null

    override fun AppState.init() {
        val random = Random(Date.now().toLong())
        name = getCookie("name") ?: "Player${10+random.nextInt(90)}"
        startGame = false
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
        return cookies!![name]
    }

    private fun setNameCookie(name :String) {
        document.cookie = "name=$name;path=/"
    }

    override fun RBuilder.render() {
        styledH1 {
            css { +CrimeStyles.title }
            +"Crime Scene"
        }
        if (!state.startGame) {
            span {
                +"Enter your name: "
            }
            styledInput {
                css {
                    +CrimeStyles.inputStyle
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
                    onClickFunction = {
                        setNameCookie(state.name)
                        setState {
                            startGame = true
                        }
                    }
                }
                +"Start game"
            }
        } else
            crimeScene {
                criminals = arrayOf(state.name, "Al Capone", "Sweeney Todd", "Jack the Ripper", "Axel Springer")
                actions = arrayOf("slaughter", "butcher", "cut", "strangle", "twist")
                kinds = arrayOf("brutally", "sneakily", "viciously", "coldblooded", "angrily")
                weapons = arrayOf("axe", "knife", "rope", "super-string", "bare hands")
                motives = arrayOf("desperation", "self-defense", "greed", "passion", "jealousy")
            }
    }
}

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            child(App::class) {}
        }
    }
}
