import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import math.*
import org.w3c.dom.HTMLImageElement
import react.*
import react.dom.*
import styled.*
import kotlin.math.roundToInt

external interface RegionProps :Props {
    var playerName :String
    var region :KnownRegion
}

data class RegionState(
    var e2s :Matrix23?, var cities :List<City>,
) :State {}

class RegionMap(props :RegionProps) :RComponent<RegionProps, RegionState>(props) {
    val mainScope = MainScope()

    override fun RegionState.init(props :RegionProps) {
        e2s = null
        cities = emptyList()
    }

    init {
        mainScope.launch {
            val regionalData = fetchCities(props.region, 36)
            setState {
                e2s = regionalData.e2s
                cities = regionalData.cities
            }
        }
    }

    override fun RBuilder.render() {
        drawMap()
        styledH1 {
            css { +PowerGridStyles.title }
            +"Power Grid in ${props.region.displayName}"
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

    private fun RBuilder.drawMap() {
        styledImg {
            css {
                position = Position.absolute
                left = 0.px
                top = 0.px
                opacity = 0.5
                zIndex = -1
            }
            attrs {
                src = "/static/${props.region.mapImage}"
                width = "100%"
                onClickFunction = { event ->
                    val bdy = (event.target as HTMLImageElement).getBoundingClientRect()
                    val org = SPoint(bdy.x, bdy.y)
                    maybeUpdateCityPoint(  // this is a Kotlin wrapper omission
                        SPoint(eval("event.clientX")-org.x, eval("event.clientY")-org.y)
                    )
                }
            }
        }
        if (state.e2s!=null && state.cities.isNotEmpty()) {
            val e2s = state.e2s!!
            for (city in state.cities)
                markCity(Pair(city, e2s.dot(Point(city.lon, city.lat))))
        }
    }

    private fun RBuilder.markCity(cityAndPoint :Pair<City, SPoint>) {
        styledSpan {
            val fs = cityAndPoint.first.size
            css {
                position = Position.absolute
                left = (cityAndPoint.second.x -0.5*fs).px
                top = (cityAndPoint.second.y -0.5*fs).px
                println("${cityAndPoint.first.name} @(${cityAndPoint.second.x.roundToInt()}, ${cityAndPoint.second.y.roundToInt()})")
                fontSize = fs.px
            }
            +"""* ${cityAndPoint.first.name}"""
        }
    }

    private fun maybeUpdateCityPoint(p :SPoint) {
        println(p)
//        if (state.city1==null)
//            setState {
//                city1 = Pair(state.cities.find(props.region.first)!!, p)
//            }
//        else if (state.city2==null)
//            setState {
//                city2 = Pair(state.cities.find(props.region.second)!!, p)
//            }
//        else if (state.city3==null)
//            setState {
//                city3 = Pair(state.cities.find(props.region.third)!!, p)
//                calcTrafo()
//            }
    }

    private fun List<City>.find(name :String) = firstOrNull { c -> name==c.name }

//    private fun RegionState.calcTrafo() {
//        val p0 = Point(city1!!.first.lon, city1!!.first.lat)
//        val p1 = Point(city2!!.first.lon, city2!!.first.lat).minus(p0)
//        val p2 = Point(city3!!.first.lon, city3!!.first.lat).minus(p0)
//        val origin = city1!!.second
//        val v1 = city2!!.second.minus(origin)
//        val v2 = city3!!.second.minus(origin)
//        setState {
//            e2s = Matrix24(p1, p2, v1, v2).linSolve(origin, p0)
//        }
//    }
}

fun RBuilder.mapComponent(handler :RegionProps.() -> Unit) = child(RegionMap::class) {
    this.attrs(handler)
}
