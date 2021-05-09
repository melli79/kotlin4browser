
import kotlinx.browser.window
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*
import styled.*
import kotlin.js.Date
import kotlin.random.Random

external interface GroupProps :RProps {
    var name :String
}

external interface GroupState :RState {
    var newCards :MutableList<Card>
    var openCards :MutableList<Card>
    var groups :MutableList<Triple<Card, Card, Card>>
    var minusPoints :Int
}

@JsExport
class GroupComponent(props :GroupProps) :RComponent<GroupProps, GroupState>(props) {
    private lateinit var random :Random

    override fun GroupState.init(props :GroupProps) {
        random = Random(Date.now().toLong())
        reset()
    }

    private fun GroupState.reset() {
        groups = mutableListOf()
        minusPoints = 0
        val deck = createDeck()
        openCards = mutableListOf(deck.removeAt(0), deck.removeAt(0))
        newCards = deck
    }

    private fun createDeck() :MutableList<Card> {
        val result = mutableListOf<Card>()
        val input = allCards().toMutableList()
        while (input.isNotEmpty()) {
            val card = input.removeAt(random.nextInt(input.size))
            if (result.numGroupsWith(card)<=1)
                result.add(card)
        }
        console.log("Deck has ${result.size} cards.")
        return result
    }

    override fun RBuilder.render() {
        styledP {
            css {
                textAlign = TextAlign.justify
            }
            +"""Watch out for groups, i.e. triples of cards that cannot be split along one feature into 2 subgroups. 
                | If you see a group, press the Group button.""".trimMargin()
        }
        p {
            span { +"The open cards: " }
            for (c in state.openCards) {
                card {
                    card = c
                }
            }
        }
        if (state.newCards.isNotEmpty()) {
            button {
                attrs {
                    onClickFunction = {
                        revealNextCard()
                    }
                }
                +"Next"
            }
            button {
                attrs {
                    onClickFunction = {
                        claimSet()
                    }
                    +"Has Set"
                }
            }
        } else {
            styledP {
                css {
                    color = Color.green
                }
                +"""Game over!  You have ${state.groups.size-state.minusPoints} points."""
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
        val group = state.openCards.findGroup()
        if(group==null) {
            window.alert("There is no Group! You lose a point.")
            setState {
                minusPoints += 1
            }
            return
        }
        setState {
            groups.add(group)
            openCards.removeAll(group.toList())
        }
    }

    private fun revealNextCard() {
        if (state.newCards.isEmpty()) {
            return
        }
        if (state.openCards.size>4) {
            val previous = state.openCards.subList(0, state.openCards.size-2)
            val oldGroup = previous.findGroup()
            if (oldGroup!=null) {
                window.alert("You overlooked a group: $oldGroup.  I claim it.")
                setState {
                    openCards.removeAll(oldGroup.toList())
                }
            }
        }
        setState {
            val newCard = newCards.removeAt(0)
            openCards.add(newCard)
        }
    }
}

fun RBuilder.setsComponent(handler: GroupProps.() -> Unit) = child(GroupComponent::class) {
    this.attrs(handler)
}
