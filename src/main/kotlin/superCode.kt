import kotlinx.browser.window
import kotlinx.css.*
import kotlinx.html.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.*
import styled.*
import kotlin.js.Date
import kotlin.random.Random

external interface SuperCodeProps :RProps {
}

external interface SuperCodeState :RState {
    var secret :Code
    var guess :MutableList<Pin?>
    var evaluation :MutableSet<Point>
    var history :MutableList<HistoryEntry>
    var clean :Boolean
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class SuperCodeGame(props :SuperCodeProps) :RComponent<SuperCodeProps, SuperCodeState>(props) {
    private lateinit var random :Random
    override fun SuperCodeState.init(props :SuperCodeProps) {
        random = Random(Date.now().toLong())
        console.log(random)
        secret = random.nextCode()
        guess = mutableListOf(null, null, null, null)
        evaluation = mutableSetOf(Point.Wrong, Point.Wrong, Point.Wrong, Point.Wrong)
        history = mutableListOf()
        clean = true
    }

    override fun RBuilder.render() {
        styledH1 {
            css {
                +CodeStyles.headline
            }
            +"Super Code"
        }
        if (state.history.isEmpty())
            styledP {
                css { color = Color.green }
                +"""You are to guess a 4 color code.  After each guess I will tell 
                    you how much is right.""".trimMargin()
            }
        else
            history {
                entries = state.history
            }
        p {}
        span {
            +"Your guess: "
        }
        styledInput {
            css {
                +CodeStyles.guessInput
            }
            attrs {
                type = InputType.text
                if (state.clean) {
                    value = ""
                    setState {
                        clean = false
                    }
                }
                onChangeFunction = { event ->
                    val element = event.target as HTMLInputElement
                    processInput(element.value)
                }
            }
        }
        styledSpan {
            css {
                +CodeStyles.pins
            }
            state.guess.map { c ->
                styledSpan {
                    css {
                        color = c?.color ?: Color.white
                    }
                    +if (c!=null) "●" else " "
                }
            }
        }
        button {
            attrs {
                onClickFunction = {
                    check()
                }
                disabled = state.guess.any { c -> c==null }
            }
            +"check"
        }
        p { +"Possible choices:" }
        styledUl {
            css { CodeStyles.choiceStyle }
            Pin.values.map { c ->
                styledLi {
                    css {
                        color = c.color
                    }
                    +c.text
                }
            }
        }
    }

    private fun check() {
        if (state.guess.any { c -> c==null })
            return
        if (state.history.any { e -> e.code.equal(state.guess)}) {
            window.alert("You have already guessed that before.")
            setState {
                clean = true
                guess = mutableListOf(null, null, null, null)
            }
            return
        }

        val ev = evaluateGuess(state.guess, state.secret)
        setState {
            history.add(HistoryEntry(Code(state.guess[0]!!, state.guess[1]!!,
                    state.guess[2]!!, state.guess[3]!!),
                Evaluation(ev[0], ev[1], ev[2], ev[3])))
            clean = true
            guess = mutableListOf(null, null, null, null)
        }
    }

    private fun processInput(input :String) {
        val g0 = if (input.length > 0) Pin.fromChar(input[0]) else null
        val g1 = if (input.length > 1) Pin.fromChar(input[1]) else null
        val g2 = if (input.length > 2) Pin.fromChar(input[2]) else null
        val g3 = if (input.length > 3) Pin.fromChar(input[3]) else null
        setState {
            guess[0] = g0
            guess[1] = g1
            guess[2] = g2
            guess[3] = g3
        }
    }
}

fun Code.equal(guess :List<Pin?>) :Boolean {
    if (guess.any { p -> p==null})
        return false
    return this==Code(guess[0]!!, guess[1]!!, guess[2]!!, guess[3]!!)
}
