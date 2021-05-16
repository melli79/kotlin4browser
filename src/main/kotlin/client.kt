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
    var favCriminal :String?
    var favAction :String?
    var favKind :String?
    var favWeapon :String?
    var favMotive :String?
    var tough :Boolean
    var startGame :Boolean
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class App :RComponent<RProps, AppState>() {
    private var cookies :MutableMap<String, String>? = null
    private val defCriminals = listOf("Prof. Moriarty", "Al Capone", "Sweeney Todd", "Jack the Ripper")
    private val defActions = listOf("slaughter", "butcher", "cut", "strangle")
    private val defKinds = listOf("cruelly", "sneakily", "viciously", "coldblooded")
    private val defWeapons = listOf("an axe", "a knife", "a rope", "bare hands")
    private val defMotives = listOf("desperation", "self-defense", "greed", "passion")

    override fun AppState.init() {
        val random = Random(Date.now().toLong())
        name = getCookie("name") ?: "Player${10+random.nextInt(90)}"
        startGame = false
    }

    private fun getCookie(name:String) :String? {
        if (cookies == null) {
            val cookieAssignments = document.cookie.split(";\\s")
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
        document.cookie = """name="$name" ;path=/"""
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
                            tough = false
                            startGame = true
                        }
                    }
                }
                +"Easy game"
            }
            button {
                attrs {
                    onClickFunction = {
                        setNameCookie(state.name)
                        setState {
                            tough = true
                            startGame = true
                        }
                    }
                }
                +"Tough game"
            }
            br {}
            p { +"If you want, enter your " }
            table { tbody {
                tr {
                    td { +"favorite criminal: " }
                    td {
                        input {
                            attrs {
                                onChangeFunction = { event ->
                                    val field = event.target as HTMLInputElement
                                    val input = field.value.trim()
                                    setState {
                                        favCriminal = if (input.isValid()) input
                                        else null
                                    }
                                }
                            }
                        }
                    }
                }
                tr {
                    td { +"favorite crime: " }
                    td {
                        input {
                            attrs {
                                onChangeFunction = { event ->
                                    val field = event.target as HTMLInputElement
                                    val input = field.value.trim()
                                    setState {
                                        favAction = if (input.isValid()) input
                                        else null
                                    }
                                }
                            }
                        }
                    }
                }
                tr {
                    td { +"favorite kind: " }
                    td {
                        input {
                            attrs {
                                onChangeFunction = { event ->
                                    val field = event.target as HTMLInputElement
                                    val input = field.value.trim()
                                    setState {
                                        favKind = if (input.isValid()) input
                                        else null
                                    }
                                }
                            }
                        }
                    }
                }
                tr {
                    td { +"favorite weapon: " }
                    td {
                        input {
                            attrs {
                                onChangeFunction = { event ->
                                    val field = event.target as HTMLInputElement
                                    val input = field.value.trim()
                                    setState {
                                        favWeapon = if (input.isValid()) input
                                        else null
                                    }
                                }
                            }
                        }
                    }
                }
                tr {
                    td { +"favorite motive: " }
                    td {
                        input {
                            attrs {
                                onChangeFunction = { event ->
                                    val field = event.target as HTMLInputElement
                                    val input = field.value.trim()
                                    setState {
                                        favMotive = if (input.isValid()) input
                                        else null
                                    }
                                }
                            }
                        }
                    }
                }
            } }
        } else
            crimeScene {
                criminals = arrayOf(defCriminals[0], defCriminals[1], defCriminals[2], defCriminals[3],
                    state.favCriminal ?: "Axel Springer")
                actions = arrayOf(defActions[0], defActions[1], defActions[2], defActions[3],
                    state.favAction ?: "twist")
                kinds = arrayOf(defKinds[0], defKinds[1], defKinds[2], defKinds[3],
                    state.favKind ?: "angrily")
                weapons = arrayOf(defWeapons[0], defWeapons[1], defWeapons[2], defWeapons[3],
                    state.favWeapon ?: "a super-string")
                motives = arrayOf(defMotives[0], defMotives[1], defMotives[2], defMotives[3],
                    state.favMotive ?: "jealousy")
                name = state.name
                tough = state.tough
            }
    }

    private fun String.isValid() = length>=2 && this !in defCriminals && this !in defActions &&
            this !in defKinds && this !in defWeapons && this !in defMotives
}

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            child(App::class) {}
        }
    }
}
