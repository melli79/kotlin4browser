import kotlinx.css.Position
import kotlinx.css.opacity
import kotlinx.css.position
import kotlinx.css.zIndex
import kotlinx.html.js.onClickFunction
import org.grutzmann.common.math.Point
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.dom.attrs
import react.dom.button
import react.dom.h2
import styled.css
import styled.styledH1
import styled.styledImg

external interface RegionProps :Props {
    var playerName :String
    var region :KnownRegion
}

data class RegionState(val origin :Point) :State {}

class RegionMap(props :RegionProps) :RComponent<RegionProps, RegionState>(props) {

    init {
        state = RegionState(
            Point(0.0, 0.0)
        )
    }

    override fun RBuilder.render() {
        styledImg {
            css {
                position = Position.absolute
                opacity = 0.75
                zIndex = -1
            }
            attrs {
                src = "/static/${props.region.mapImage}"
                width = "100%"
            }
        }
        styledH1 {
            css { +PowerGridStyles.title }
            +"Power Grid in ${props.region.displayName}"
        }
        h2 {
            +"${props.playerName}, your turn!"
        }
        button {
            attrs {
                onClickFunction = { event ->
                }
                disabled = true
            }
            +"Next"
        }
    }
}

fun RBuilder.mapComponent(handler :RegionProps.() -> Unit) = child(RegionMap::class) {
    this.attrs(handler)
}
