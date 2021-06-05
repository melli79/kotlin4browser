import kotlinx.browser.window
import kotlinx.css.TextAlign
import kotlinx.css.textAlign
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLSelectElement
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
    var tough :Boolean
}

external interface CrimeSceneState :RState {
    var alibis :List<Scenario>
    var crime :Scenario
    var observations :MutableList<Observation>
    var suspect :String?
    var suspectedAction :String?
    var suspectedKind :String?
    var suspectedWeapon :String?
    var suspectedMotive :String?
    var clean :Boolean
    var player2 :Detective
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
        suspect = props.criminals[0]
        suspectedAction = props.actions[0]
        suspectedKind = props.kinds[0]
        suspectedWeapon = props.weapons[0]
        suspectedMotive = props.motives[0]
        player2 = if (props.tough)
                ToughDetective(alibis = alibis[1], background = props)
            else
                EasyDetective(alibis = alibis[1], background = props)
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
    }

    private fun RBuilder.describeScene() {
        styledP {
            css {
                textAlign = TextAlign.justify
            }
            +"""You are to solve a crime that happened, that is you are to identify the criminal, the action, the kind,
                | the weapon and the motive for which Liz Taylor was killed.  You can do this by formulating
                | hypotheses and interrogating the witnesses whether anyone can provide an alibi against that.
                | When you ask a question, the witnesses will answer one by one until the first can disprove your
                | hypothesis which you will see by being shown the item that disproves your hypothesis.""".trimMargin()
        }
    }

    private fun RBuilder.showInputs() {
        span {
            +"Your inquiry: "
        }
        styledP {
            css { +CrimeStyles.inputStyle }
            span { +"Did " }
            select {
                attrs {
                    onChangeFunction = { event ->
                        val input = event.target as HTMLSelectElement
                        setState {
                            suspect = input.value
                        }
                    }
                }
                for (criminal in props.criminals)
                    option {
                        attrs {
                            value = criminal
                        }
                        +criminal
                    }
            }
            select {
                attrs {
                    onChangeFunction = { event ->
                        val input = event.target as HTMLSelectElement
                        setState {
                            suspectedAction = input.value
                        }
                    }
                }
                for (action in props.actions)
                    option {
                        attrs {
                            value = action
                        }
                        +action
                    }
            }
            span { +" Liz Taylor " }
            select {
                attrs {
                    onChangeFunction = { event ->
                        val input = event.target as HTMLSelectElement
                        setState {
                            suspectedKind = input.value
                        }
                    }
                }
                for (kind in props.kinds)
                    option {
                        attrs {
                            value = kind
                        }
                        +kind
                    }
            }
            span { +" with " }
            select {
                attrs {
                    onChangeFunction = { event ->
                        val input = event.target as HTMLSelectElement
                        setState {
                            suspectedWeapon = input.value
                        }
                    }
                }
                for (weapon in props.weapons)
                    option {
                        attrs {
                            value = weapon
                        }
                        +weapon
                    }
            }
            span { +" out of "}
            select {
                attrs {
                    onChangeFunction = { event ->
                        val input = event.target as HTMLSelectElement
                        setState {
                            suspectedMotive = input.value
                        }
                    }
                }
                for (motive in props.motives)
                    option {
                        attrs {
                            value = motive
                        }
                        +motive
                    }
            }
            span { +"?" }
        }
        button {
            attrs {
                disabled = state.suspect==null || state.suspectedAction==null || state.suspectedKind==null ||
                        state.suspectedWeapon==null || state.suspectedMotive==null
                onClickFunction = {
                    val alibi = inquiry(Scenario(state.suspect!!, state.suspectedAction!!, state.suspectedKind!!, state.suspectedWeapon!!, state.suspectedMotive!!))
                    if (alibi?.witness != null)
                        playOthers(alibi.withoutAlibiItem())
                }
            }
            +"Ask"
        }
    }

    private fun RBuilder.showGameOver() {
        val detective = state.observations.last().detective
        if (detective ==props.name)
            p { +"""You solved the crime! Congratulations!!""" }
        else
            p { +"""The crime was solved by $detective.  Maybe next time you will be faster."""}
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

    private fun inquiry(inquiry :Scenario) :Observation? {
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
