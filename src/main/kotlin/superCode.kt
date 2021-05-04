import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
    var hint :Code?
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class SuperCodeGame(props :SuperCodeProps) :RComponent<SuperCodeProps, SuperCodeState>(props) {
    private lateinit var random :Random
    override fun SuperCodeState.init(props :SuperCodeProps) {
        random = Random(Date.now().toLong())
        reset()
    }

    private fun SuperCodeState.reset() {
        secret = random.nextCode()
        guess = mutableListOf(null, null, null, null)
        evaluation = mutableSetOf(Point.Wrong, Point.Wrong, Point.Wrong, Point.Wrong)
        history = mutableListOf()
        clean = true
        hint = null
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
        if (state.history.isEmpty() || state.history.last().totalPoints()<8) {
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
                        if (state.hint != null) {
                            value =
                                "${state.hint!!.first.abbr}${state.hint!!.second.abbr}${state.hint!!.third.abbr}${state.hint!!.fourth.abbr}"
                            setState {
                                state.guess = state.hint!!.toList().toMutableList()
                                clean = false
                                hint = null
                            }
                        } else {
                            value = ""
                            setState {
                                clean = false
                            }
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
                        +if (c != null) "●" else "?"
                    }
                }
            }
            button {
                attrs {
                    onClickFunction = {
                        if (state.guess.all { c -> c!=null })
                            check()
                        else
                            cheat()
                    }
                    disabled = state.guess.any { c -> c == null } && state.guess.any { c -> c != null}
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
        } else {
            p {
                +"You guessed it!! Congratulations!"
            }
            button {
                attrs {
                    onClickFunction = {
                        setState {
                            reset()
                        }
                    }
                }
                +"Play again"
            }
        }
    }

    private fun cheat() {
        val mainScope = MainScope()
        mainScope.launch {
            val cheat = computeCheat(state.history)
            console.log(cheat)
            setState {
                hint = cheat
                clean = true
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

        val ev = state.secret.evaluateGuess(state.guess)
        setState {
            history.add(HistoryEntry(Code(state.guess[0]!!, state.guess[1]!!,
                    state.guess[2]!!, state.guess[3]!!),
                ev))
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

suspend fun computeCheat(history :List<HistoryEntry>) :Code? = coroutineScope {
    var result :Code? = null
    outer@for (p0 in Pin.values)  for (p1 in Pin.values)  for (p2 in Pin.values) for (p3 in Pin.values) {
        val code = Code(p0, p1, p2, p3)
        if (history.all { h -> code.evaluateGuess(h.code.toList())==h.evaluation }) {
            result = code
            break@outer
        }
    }
    result
}
