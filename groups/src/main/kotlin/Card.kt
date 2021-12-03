import kotlinx.css.*
import react.*
import styled.*

val knownSymbols = listOf("♣","♠", "︎♥", "︎♦︎")
val knownColors = listOf(Color.red, Color.orange, Color.green, Color.blue)
val knownShades = listOf("🀆", "🀫", "🃘", "🀘")
val knownRepetitions = listOf(1, 2, 3, 5)

data class Card(
    val symbol :String,
    val color :Color,
    val shade :String,
    val repetition :Int
)

fun allCards() = knownRepetitions.flatMap { repetition ->
    knownShades.flatMap { shade ->
        knownColors.flatMap { color ->
            knownSymbols.map { symbol -> Card(symbol, color, shade, repetition) }
        }
    }
}

fun List<Card>.findGroup() :Triple<Card, Card, Card>? {
    if (size<3)
        return null
    forEachIndexed { i1, c1 ->
        forEachIndexed { i2, c2 -> if (i2>i1)
            forEachIndexed { i3, c3 -> if (i3>i2) {
                val candidate = Triple(c1, c2, c3)
                if (candidate.isGroup())
                    return candidate
            } }
        }
    }
    return null
}

fun List<Card>.findGroup(c1 :Card) :Triple<Card, Card, Card>? {
    if (size<3)
        return null
    forEachIndexed { i2, c2 -> if (c2!=c1)
        forEachIndexed { i3, c3 -> if (i3>i2 && c3!=c1) {
            val candidate = Triple(c1, c2, c3)
            if (candidate.isGroup())
                return candidate
        } }
    }
    return null
}

fun Triple<Card, Card, Card>.isGroup() =
    (first.symbol==second.symbol&&first.symbol==third.symbol ||
        first.symbol!=second.symbol&&second.symbol!=third.symbol && third.symbol!=first.symbol) &&
    (first.color==second.color&&first.color==third.color ||
        first.color!=second.color && second.color!=third.color && third.color!=first.color) &&
    (first.shade==second.shade&&first.shade==third.shade ||
        first.shade!=second.shade && second.shade!=third.shade && third.shade!=first.shade) &&
    (first.repetition==second.repetition&&first.repetition==third.repetition ||
        first.repetition!=second.repetition && second.repetition!=third.repetition && third.repetition!=first.repetition)

fun List<Card>.numGroupsWith(card :Card) :Int {
    if (size<2)  return 0
    var solutions = 0
    forEachIndexed { i2, c2 ->
        forEachIndexed { i3, c3 -> if (i3>i2) {
            if (Triple(card, c2, c3).isGroup())
                solutions += 1
            if (solutions > 1)
                return solutions
        } }
    }
    return solutions
}

external interface CardProps :Props {
    var card :Card
}

class CardComponent(props :CardProps) :RComponent<CardProps, State>(props) {
    override fun RBuilder.render() {
        styledSpan {
            css {
                color = props.card.color
            }
            +((1..props.card.repetition)
                .map { props.card.symbol }
                .joinToString("")
                +"${props.card.shade}, ")
        }
    }
}

fun RBuilder.card(handler :CardProps.() -> Unit) = child(CardComponent::class) {
    this.attrs(handler)
}
