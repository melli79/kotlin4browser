import react.*
import react.dom.*
import styled.*

data class Observation (val scene: Scenario, val noWitnesses :List<Int>, val witness :Int?, val alibiItem :String?)

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
                    css { CrimeStyles.observationStyle }
                    +"""${observation.scene.question()} 
                        |${if (observation.noWitnesses.isNotEmpty()) "no witnesses: "+observation.noWitnesses.joinToString()+", " else ""}
                        | counter-witness: ${if (observation.witness!=null) "${observation.witness}"+" alibi: "+observation.alibiItem else "nobody"}""".trimMargin()
                }
        }
    }
}

fun RBuilder.observationsComponent(handler :ObservationsProps.() -> Unit) = child(ObservationsComponent::class) {
    this.attrs(handler)
}

fun findAlibi(inquiry :Scenario, alibies :List<Scenario>, start :Int) :Observation {
    val noWitnesses = mutableListOf<Int>()
    for (i in 0 until alibies.size) {
        val witness = (start + i) % alibies.size
        val alibi = alibies[witness]
        if (inquiry.criminal==alibi.criminal)
            return Observation(inquiry, noWitnesses, witness, alibi.criminal)
        if (inquiry.action==alibi.action)
            return Observation(inquiry, noWitnesses, witness, alibi.action)
        if (inquiry.kind==alibi.kind)
            return Observation(inquiry, noWitnesses, witness, alibi.kind)
        if (inquiry.weapon==alibi.weapon)
            return Observation(inquiry, noWitnesses, witness, alibi.weapon)
        if (inquiry.motive==alibi.motive)
            return Observation(inquiry, noWitnesses, witness, alibi.motive)
        noWitnesses += witness
    }
    return Observation(inquiry, noWitnesses, null, null)
}
