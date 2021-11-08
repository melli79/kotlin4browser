
import kotlinx.browser.window
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*
import styled.*
import kotlin.js.Date
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.random.Random

external interface GroupProps :RProps {
    var name :String
    var setSize :Int
}

external interface GroupState :RState {
    var remainingCards :MutableList<Card>
    var openSet :List<Card>
    var groups :MutableList<Triple<Card, Card, Card>>
    var points :Int
    var revealTime :Double
    var round :Int
}

@JsExport
class GroupComponent(props :GroupProps) :RComponent<GroupProps, GroupState>(props) {
    private lateinit var random :Random
    val numRounds = 10

    override fun GroupState.init(props :GroupProps) {
        random = Random(Date.now().toLong())
        reset()
    }

    private fun GroupState.reset() {
        groups = mutableListOf()
        points = 0
        openSet = emptyList()
        remainingCards = allCards().toMutableList()
        round = 0
    }

    private fun pickSetWith1Group(remainingDeck :MutableList<Card>) :Pair<List<Card>, MutableList<Card>> {
        val result = mutableListOf<Card>()
        val refused = mutableListOf<Card>()
        while (remainingDeck.isNotEmpty()) {
            val card = remainingDeck.removeAt(random.nextInt(remainingDeck.size))
            val numGroups = result.numGroupsWith(card)
            if (numGroups==1) {
                result.add(card)
                fillSet(result, remainingDeck, refused)
                break
            } else if (numGroups==0 && result.size+1<props.setSize)
                result.add(card)
            else
                refused.add(card)
        }
        console.log("Set has ${result.size} cards.")
        if (result.size>=props.setSize) {
            remainingDeck.addAll(refused)
            return Pair(result, remainingDeck)
        }
        return Pair(emptyList(), remainingDeck)
    }

    private fun pickSetWithoutGroup(remainingDeck :MutableList<Card>) :Pair<List<Card>, MutableList<Card>> {
        val result = mutableListOf<Card>()
        val refused = mutableListOf<Card>()
        fillSet(result, remainingDeck, refused)
        console.log("Set has ${result.size} cards.")
        if (result.size>=props.setSize) {
            remainingDeck.addAll(refused)
            return Pair(result, remainingDeck)
        }
        return Pair(emptyList(), remainingDeck)
    }

    private fun fillSet(
        result :MutableList<Card>,
        remainingDeck :MutableList<Card>,
        refused :MutableList<Card>
    ) {
        while (remainingDeck.isNotEmpty() && result.size<props.setSize) {
            val card = remainingDeck.removeAt(random.nextInt(remainingDeck.size))
            if (result.numGroupsWith(card) < 1)
                result.add(card)
            else
                refused.add(card)
        }
    }

    override fun RBuilder.render() {
        styledP {
            css {
                textAlign = TextAlign.justify
            }
            +"""Watch out for groups, i.e. triples of cards that cannot be split along one feature (shape, number, color, shade)
                | into 2 subgroups.  If you see a group, press the Group button.""".trimMargin()
        }
        p {
            span { +"The open cards: " }
            for (c in state.openSet) {
                card {
                    card = c
                }
            }
        }
        if (state.round < numRounds) {
            button {
                attrs {
                    onClickFunction = {
                        revealNextSet()
                    }
                }
                + if (state.openSet.isEmpty()) "Start" else "Next"
            }
            button {
                attrs {
                    onClickFunction = {
                        claimSet()
                        revealNextSet()
                    }
                    +"Has Group"
                }
            }
        } else {
            styledP {
                css {
                    color = Color.green
                }
                +"""Game over!"""
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
        div {
            p { +"You have ${state.points} points."}
            p { +"Your groups:" }
            for (group in state.groups) {
                p {
                    card { card = group.first }
                    card { card = group.second }
                    card { card = group.third }
                }
            }
        }
    }

    private fun claimSet() {
        val group = state.openSet.findGroup()
        if(group==null) {
            window.alert("There is no Group! You lose 5 points.")
            setState {
                points -= 5
            }
            return
        }
        val revealTime = state.revealTime
        val now = Date.now()
        setState {
            groups.add(group)
            points += 5 +(100/ln(1+now-revealTime)).roundToInt()
        }
    }

    private fun revealNextSet() {
        if (state.remainingCards.isEmpty()) {
            return
        }
        val remainingDeck = state.remainingCards
        setState {
            val split = if (random.nextBoolean())
                pickSetWithoutGroup(remainingDeck)
            else
                pickSetWith1Group(remainingDeck)
            openSet = split.first
            remainingCards = split.second
            revealTime = Date.now()
            round++
        }
    }
}

fun RBuilder.setsComponent(handler: GroupProps.() -> Unit) = child(GroupComponent::class) {
    this.attrs(handler)
}
