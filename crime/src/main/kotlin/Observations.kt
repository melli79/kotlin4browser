import kotlinx.css.*
import react.*
import react.dom.*
import styled.*

data class Observation (val detective :String, val scene: Scenario, val noWitnesses :List<Int>, val witness :Int?, val alibiItem :String?) {
    fun withoutAlibiItem() = Observation(detective, scene, noWitnesses, witness, null)
}

external interface ObservationsProps :RProps {
    var observations :List<Observation>
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class ObservationsComponent :RComponent<ObservationsProps, RState>() {
    override fun RBuilder.render() {
        p {
            +"Observations:"
        }
        ol {
            for (observation in props.observations)
                styledLi {
                    css {
                        CrimeStyles.observationStyle
                        if (observation.witness==null)
                            color = Color.red
                    }
                    +"""${observation.detective}: ${observation.scene.question()}
                        |${if (observation.noWitnesses.isNotEmpty()) "no witnesses: "+observation.noWitnesses.joinToString()+", " else ""}""".trimMargin()
                    styledSpan {
                        css { color = Color.green }
                        +"""alibi-witness: ${if (observation.witness!=null) "${observation.witness}" else "nobody"}
                            | ${if (observation.alibiItem!=null) " alibi: "+observation.alibiItem else ""}""".trimMargin()
                    }
                }
        }
    }
}

fun RBuilder.observationsComponent(handler :ObservationsProps.() -> Unit) = child(ObservationsComponent::class) {
    this.attrs(handler)
}

fun findAlibi(detective :String, inquiry :Scenario, alibies :List<Scenario>, start :Int) :Observation {
    val noWitnesses = mutableListOf<Int>()
    for (i in 0 until alibies.size) {
        val witness = (start + i) % alibies.size
        val alibi = alibies[witness]
        if (inquiry.criminal==alibi.criminal)
            return Observation(detective, inquiry, noWitnesses, witness, alibi.criminal)
        if (inquiry.action==alibi.action)
            return Observation(detective, inquiry, noWitnesses, witness, alibi.action)
        if (inquiry.kind==alibi.kind)
            return Observation(detective, inquiry, noWitnesses, witness, alibi.kind)
        if (inquiry.weapon==alibi.weapon)
            return Observation(detective, inquiry, noWitnesses, witness, alibi.weapon)
        if (inquiry.motive==alibi.motive)
            return Observation(detective, inquiry, noWitnesses, witness, alibi.motive)
        noWitnesses += witness
    }
    return Observation(detective, inquiry, noWitnesses, null, null)
}
