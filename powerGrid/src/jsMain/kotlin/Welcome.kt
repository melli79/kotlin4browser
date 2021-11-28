import kotlinx.browser.document
import kotlinx.css.Position.*
import kotlinx.css.*
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.*
import styled.*

external interface WelcomeProps :Props {
    var knownMaps :Array<KnownMaps>
}

data class WelcomeState(val name :String) :State {
    fun withName(name :String) = WelcomeState(name)
}

class Welcome(props :WelcomeProps) :RComponent<WelcomeProps, WelcomeState>(props) {

    init {
        state = WelcomeState(getCookie("name") ?: "Player ${10+random.nextInt(90)}")
    }

    private fun getCookie(name :String) :String? {
        val cookies = document.cookie
            .splitToSequence(";\\s*")
            .map { c ->
                val keyValue = c.split("=")
                if (keyValue.size==2)
                    Pair(keyValue[0], keyValue[1])
                else
                    null
            }.filterNotNull()
        return cookies
            .firstOrNull { e -> e.first==name }
            ?.second
            ?.removeSurrounding("\"")
    }

    override fun RBuilder.render() {
        styledImg {
            css {
                position = absolute
                opacity = 0.25
                zIndex = -1
            }
            attrs {
                src = "/static/cover.jpg"
                width = "100%"
            }
        }
        styledH1 {
            css { +PowerGridStyles.title }
            +"Power Grid"
        }
        h2 {
            +"Hello, ${state.name}!"
        }
        span { +"Your name: " }
        styledInput {
            css { +PowerGridStyles.textInput }
            attrs {
                type = InputType.text
                value = state.name
                onChangeFunction = { event ->
                    setState(WelcomeState(
                        (event.target as HTMLInputElement).value
                    ))
                }
            }
        }
        button {
            attrs {
                onClickFunction = { event ->
                    setCookie("name", state.name)
                }
            }
            +"Start Game"
        }
    }

    private fun setCookie(name :String, value :String) {
        document.cookie = """$name=$value; path='/'"""
    }
}
