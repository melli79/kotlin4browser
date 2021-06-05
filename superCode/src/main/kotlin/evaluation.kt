import kotlinx.css.*
import react.*
import react.dom.*
import styled.*

sealed class Point(val color :Color, val value :Int, val abbr :Char) {
    object Wrong :Point(Color.lightGray, 0, 'X') {
        override fun toString() = "Wrong"
    }
    object Half :Point(Color.gray, 1, 'H') {
        override fun toString() = "Half"
    }
    object Correct :Point(Color.black, 2, 'V') {
        override fun toString() = "Correct"
    }
}

data class Evaluation(val points :Array<Point>) {
    constructor(first :Point, second :Point, third :Point, fourth :Point)
            :this(listOf(first, second, third, fourth).sortedBy { p -> -p.value }.toTypedArray())

    override fun equals(other :Any?) = other is Evaluation && points.contentEquals(other.points)
    override fun hashCode() = points.hashCode() +1
    fun totalPoints() = points.sumBy { p -> p.value }
}

data class HistoryEntry(val code :Code, val evaluation :Evaluation) {
    fun totalPoints() = evaluation.points.sumOf { p -> p.value }
}

external interface HistoryProps :RProps {
    var entries :List<HistoryEntry>
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class HistoryComponent(props :HistoryProps) :RComponent<HistoryProps, RState>(props) {
    override fun RBuilder.render() {
        styledOl {
            props.entries.map { entry ->
                li {
                    styledSpan {
                        css {
                            +CodeStyles.pins
                        }
                        entry.code.map { c ->
                            styledSpan {
                                css {
                                    color = c.color
                                }
                                +"${c.abbr}"
                            }
                        }
                    }
                    +" "
                    entry.evaluation.points.map { p ->
                        styledSpan {
                            css {
                                border = "1px solid black"
                                color = p.color
                            }
                            +"${p.abbr}"
                        }
                    }
                }
            }
        }
    }
}

fun RBuilder.history(handler :HistoryProps.() -> Unit) = child(HistoryComponent::class) {
    this.attrs(handler)
}

fun Code.evaluateGuess(guess :List<Pin?>) :Evaluation {
    val openChoices = mutableListOf<Pin>()
    val openOptions = mutableSetOf<Pin>()
    val result = mutableListOf<Point>()
    val first = this[0].compare(guess[0])
    if (first == Point.Wrong) {
        openChoices.add(guess[0]!!)
        openOptions.add(this[0])
    } else
        result.add(first)
    val second = this[1].compare(guess[1])
    if (second == Point.Wrong) {
        openChoices.add(guess[1]!!)
        openOptions.add(this[1])
    } else
        result.add(second)
    val third = this[2].compare(guess[2])
    if (third == Point.Wrong) {
        openChoices.add(guess[2]!!)
        openOptions.add(this[2])
    } else
        result.add(third)
    val fourth = this[3].compare(guess[3])
    if (fourth == Point.Wrong) {
        openChoices.add(guess[3]!!)
        openOptions.add(this[3])
    } else
        result.add(fourth)
    while (openChoices.isNotEmpty()) {
        val choice = openChoices.removeAt(openChoices.size - 1)
        result.add(
            if (openOptions.remove(choice))
                Point.Half else Point.Wrong
        )
    }
    return Evaluation(result[0], result[1], result[2], result[3])
}

private fun Pin.compare(color :Pin?) = when(color) {
    this -> Point.Correct
    else -> Point.Wrong
}
