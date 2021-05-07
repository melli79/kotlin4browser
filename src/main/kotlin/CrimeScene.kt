import kotlinx.browser.window
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.*
import styled.*
import kotlin.js.Date
import kotlin.random.Random

external interface CrimeSceneProps :RProps {
    var criminals :Array<String>
    var actions :Array<String>
    var kinds :Array<String>
    var weapons :Array<String>
    var motives :Array<String>
    var name :String
}

external interface CrimeSceneState :RState {
    var alibis :List<Scenario>
    var crime :Scenario
    var observations :MutableList<Observation>
    var inquiry :String
    var clean :Boolean
    var player2 :AutoDetective
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class CrimeScene(props :CrimeSceneProps) :RComponent<CrimeSceneProps, CrimeSceneState>(props) {
    private lateinit var random :Random
    override fun CrimeSceneState.init(props :CrimeSceneProps) {
        random = Random(Date.now().toLong())
        reset()
    }

    private fun CrimeSceneState.reset() {
        val criminalsPermutation = Random.nextPermutation(props.criminals.size)
        val actionsPermutation = Random.nextPermutation(props.actions.size)
        val kindsPermutation = Random.nextPermutation(props.kinds.size)
        val weaponsPermutation = Random.nextPermutation(props.weapons.size)
        val motivesPermutation = Random.nextPermutation(props.motives.size)
        crime = Scenario(
            props.criminals[criminalsPermutation[0]],
            props.actions[actionsPermutation[0]],
            props.kinds[kindsPermutation[0]],
            props.weapons[weaponsPermutation[0]],
            props.motives[motivesPermutation[0]]
        )
        alibis = (1 until props.criminals.size).map { i ->
            Scenario(
                props.criminals[criminalsPermutation[i]],
                props.actions[actionsPermutation[i]],
                props.kinds[kindsPermutation[i]],
                props.weapons[weaponsPermutation[i]],
                props.motives[motivesPermutation[i]]
            )
        }
        observations = mutableListOf()
        clean = true
        inquiry = ""
        player2 = AutoDetective(alibis = alibis[1], background = props)
    }

    override fun RBuilder.render() {
        if (state.observations.isEmpty())
            describeScene()
        else
            observationsComponent {
                observations = state.observations
            }
        if (state.observations.isEmpty() || state.observations.last().witness!=null) {
            showInputs()
        } else
            showGameOver()
        styledP {
            css { +CrimeStyles.alibiStyle }
            +"Your alibies: ${state.alibis[0]}"
        }
        describeSuspects()
    }

    private fun RBuilder.describeScene() {
        p {
            +"""You are to solve a crime that happened, that is you are to identify the criminal, the action, the kind,
                        | the weapon and the motive by which the victim was killed.  You can do this by formulating
                        | hypotheses and interrogating the witnesses whether anyone can provide an alibi against that.""".trimMargin()
        }
    }

    private fun RBuilder.showInputs() {
        span {
            +"Your inquiry: "
        }
        styledInput {
            css { +CrimeStyles.inputStyle }
            attrs {
                if (state.clean)
                    value = state.inquiry
                onChangeFunction = { event ->
                    val inputField = event.target as HTMLInputElement
                    setState {
                        inquiry = inputField.value
                    }
                }
            }
        }
        button {
            attrs {
                disabled = state.inquiry.trim().length < 10
                onClickFunction = {
                    val alibi = inquiry(state.inquiry.trim())
                    if (alibi?.witness != null)
                        playOthers(alibi.withoutAlibiItem())
                }
            }
            +"Ask"
        }
    }

    private fun RBuilder.describeSuspects() {
        p { +("All suspects: " + props.criminals.joinToString()) }
        p { +("All actions: " + props.actions.joinToString()) }
        p { +("All kinds: " + props.kinds.joinToString()) }
        p { +("All weapons: " + props.weapons.joinToString()) }
        p { +("All motives: " + props.motives.joinToString()) }
    }

    private fun RBuilder.showGameOver() {
        p { +"""You solved the crime! Congratulations!!""" }
        button {
            attrs {
                onClickFunction = {
                    setState {
                        reset()
                    }
                }
            }
            +"Play again?"
        }
    }

    private fun playOthers(oldAlibi :Observation?) {
        val player = state.player2
        if (oldAlibi != null)
            player.tellObservation(oldAlibi)
        val inquiry = player.createInquiry()
        if (inquiry!=null && inquiry !in state.observations.map { o -> o.scene }) {
            val alibi = findAlibi(player.name, inquiry, state.alibis, 1)
            if (alibi.witness==null) {
                window.alert("${player.name} solved the crime: ${inquiry.statement()}")
                setState {
                    observations.add(alibi.withoutAlibiItem())
                }
                return
            }
            player.tellObservation(alibi)
            setState {
                observations.add(alibi.withoutAlibiItem())
            }
        } else
            console.log("${player.name} is stuck at the last question.")
    }

    private fun inquiry(question :String) :Observation? {
        val inquiry = parseScenario(question, props) ?: return null
        if (inquiry in state.observations.map { o -> o.scene }) {
            window.alert("The accusation was already disproved! Try another one")
            return null
        }
        val alibi = findAlibi(props.name, inquiry, state.alibis, 1)
        if (alibi.witness==null)
            window.alert("You solved the crime: ${inquiry.statement()}")
        setState {
            observations.add(alibi)
        }
        return alibi
    }
}

fun RBuilder.crimeScene(handler :CrimeSceneProps.() -> Unit) = child(CrimeScene::class) {
    this.attrs(handler)
}

fun Random.nextPermutation(n :Int) :List<Int> {
    val result = (0 until n).toMutableList()
    for (k in 0..n) {
        val i = nextInt(n)
        val j = nextInt(n)
        val tmp = result[i]
        result[i] = result[j]
        result[j] = tmp
    }
    return result
}
