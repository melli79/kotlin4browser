import kotlinx.browser.document
import kotlinx.css.Position.*
import kotlinx.css.*
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import react.*
import react.dom.*
import styled.*

external interface WelcomeProps :Props {
    var knownRegions :Array<KnownRegion>
    var setPlayerName :(name :String) -> Unit
    var setRegion :(region :KnownRegion) -> Unit
}

data class WelcomeState(var name :String, var region :KnownRegion?) :State {
}

class Welcome(props :WelcomeProps) :RComponent<WelcomeProps, WelcomeState>(props) {

    init {
        state = WelcomeState(getCookie("name") ?: "Player ${10+random.nextInt(90)}",
            null)
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
                    setState {
                        name = (event.target as HTMLInputElement).value
                    }
                }
            }
        }
        br {  }
        span { +"Region to play: " }
        styledSelect {
            css { +PowerGridStyles.selection }
            attrs {
                onChangeFunction = { event ->
                    setState {
                        region = KnownRegion.valueOf((event.target as HTMLSelectElement).value)
                    }
                }
            }
            styledOption {
                attrs { value="" }
                +"--Please choose a Region--"
            }
            for (rgn in props.knownRegions)
                styledOption {
                    attrs {
                        value = rgn.name
                    }
                    +rgn.displayName
                }
        }
        span { +" " }
        button {
            attrs {
                onClickFunction = { _ ->
                    setCookie("name", state.name)
                    props.setPlayerName(state.name)
                    val region = state.region
                    if (region!=null)
                        props.setRegion(region)
                }
                disabled = state.region==null || state.name.isBlank()
            }
            +"Start Game"
        }
    }

    private fun setCookie(name :String, value :String) {
        document.cookie = """$name=$value; path='/'"""
    }
}

fun RBuilder.welcomeComponent(handler :WelcomeProps.() -> Unit) = child(Welcome::class) {
    this.attrs(handler)
}
