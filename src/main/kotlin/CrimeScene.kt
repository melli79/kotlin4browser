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
}

external interface CrimeSceneState :RState {
    var alibies :MutableList<Scenario>
    var crime :Scenario
    var observations :MutableList<Observation>
    var inquiry :String
    var clean :Boolean
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class CrimeScene(props :CrimeSceneProps) :RComponent<CrimeSceneProps, CrimeSceneState>(props) {
    private lateinit var random :Random
    override fun CrimeSceneState.init(props :CrimeSceneProps) {
        random = Random(Date.now().toLong())
        val criminalsPermutation = Random.nextPermutation(props.criminals.size)
        val actionsPermutation = Random.nextPermutation(props.actions.size)
        val kindsPermutation = Random.nextPermutation(props.kinds.size)
        val weaponsPermutation = Random.nextPermutation(props.weapons.size)
        val motivesPermutation = Random.nextPermutation(props.motives.size)
        crime = Scenario(props.criminals[criminalsPermutation[0]],
            props.actions[actionsPermutation[0]],
            props.kinds[kindsPermutation[0]],
            props.weapons[weaponsPermutation[0]],
            props.motives[motivesPermutation[0]])
        alibies = mutableListOf()
        (1 until props.criminals.size).forEach { i ->
            alibies.add(Scenario(props.criminals[criminalsPermutation[i]],
                props.actions[actionsPermutation[i]],
                props.kinds[kindsPermutation[i]],
                props.weapons[weaponsPermutation[i]],
                props.motives[motivesPermutation[i]]))
        }
        observations = mutableListOf()
        clean = true
        inquiry = ""
    }

    override fun RBuilder.render() {
        if (state.observations.isEmpty())
            p {
                +"""You are to solve a crime that hapend, that is you are to identify the criminal, the action, the kind,
                    | the weapon and the motive by which the victim was killed.  You can do this by formulating
                    | hypotheses and interrogating the witnesses whether anyone can provide an alibi against that.""".trimMargin()
            }
        else
            observationsComponent {
                observations = state.observations
            }
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
                disabled = state.inquiry.trim().length<10
                onClickFunction = {
                    inquiry(state.inquiry.trim())
                }
            }
            +"Ask"
        }
        styledP {
            css { +CrimeStyles.alibiStyle }
            +"Your alibies: ${state.alibies[0]}"
        }
        p { +("All suspects: "+props.criminals.joinToString()) }
        p { +("All actions: "+props.actions.joinToString()) }
        p { +("All kinds: "+props.kinds.joinToString()) }
        p { +("All weapons: "+props.weapons.joinToString()) }
        p { +("All motives: "+props.motives.joinToString()) }
    }

    private fun inquiry(question :String) {
        val inquiry = parseScenario(question, props) ?: return
        if (inquiry in state.observations.map { o -> o.scene }) {
            window.alert("The accusation was already disproved! Try another one")
            return
        }
        val alibi = findAlibi(inquiry, state.alibies, 1)
        if (alibi.witness==null)
            window.alert("You solved the crime: ${inquiry.statement()}")
        setState {
            observations.add(alibi)
        }
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
