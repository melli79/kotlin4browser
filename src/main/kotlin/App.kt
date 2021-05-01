import kotlinx.css.Align
import kotlinx.css.alignItems
import kotlinx.html.*
import kotlinx.html.js.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import react.*
import react.dom.*
import styled.css
import styled.styledDiv
import kotlin.js.Date
import kotlin.random.Random

enum class Step {
    Hello, Play
}

external interface AppState :RState {
    var name :String
    var step :Step
}

@JsExport
class App :RComponent<RProps, AppState>() {
    override fun AppState.init() {
        name = "Player${Random(Date.now().toLong()).nextInt(100)}"
        step = Step.Hello
    }

    override fun RBuilder.render() {
        h1 {
            +"Hello, ${state.name}!"
        }
        if (state.step == Step.Hello) {
            p {
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
            button {
                attrs {
                    onClickFunction = {
                        setState {
                            step = Step.Play
                        }
                    }
                }
                +"start Game"
            }
        } else
            styledDiv {
                css {
                    alignItems = Align.center
                }
                board {}
            }
    }
}
