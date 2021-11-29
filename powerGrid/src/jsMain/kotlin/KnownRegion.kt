import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import math.Matrix23
import org.w3c.xhr.XMLHttpRequest

enum class KnownRegion(prefix :String, val displayName :String, val first :String, val second :String, val third :String) {
    CHINA("China", "P.R. of China", "Shanghai", "Beijing", "Ürümqi"),
    EUROPE("Europe", "Europe", "Moscov", "London", "Athens"),
    GERMANY("Germany", "Germany", "Berlin", "Munich", "Freiburg i.B."),
    N_AMERICA("nAmerica", "Northern America", "Mexico City", "New York City", "Seattle");

    val n = name
    val mapImage = "$prefix.jpg"
    val mapDetails = "cities.$prefix.json"
    override fun toString() = displayName
}

@Serializable
internal data class CDist(val city :String, val dist :Int) {}

@Serializable
internal data class SCity(val name :String, val size :Int, val lat :Double, val lon :Double, val neighbors :Array<CDist>) {
}

@Serializable
internal data class SRegionalData(val map :Matrix23, val cities :Array<SCity>)

class City(val name :String, val size :Int, val lat :Double, val lon :Double) {
    val neighbors = mutableMapOf<City, Int>()

    override fun toString() = """$name $size @($lat, $lon), nn:
        |${neighbors.map { n -> n.key.name }.joinToString(", ")}""".trimMargin()
}

data class RegionalData(val e2s :Matrix23, val cities :List<City>) {}

suspend fun fetchCities(region :KnownRegion, cutoff :Int = 0) :RegionalData {
    val json = window.fetch("http://localhost:8080/static/${region.mapDetails}")
        .await()
        .text()
        .await()
    val rawData = Json.decodeFromString<SRegionalData>(json)
    val trimmedCities = if (cutoff>0) rawData.cities.slice(0 until cutoff)
        else rawData.cities.toList()
    val cities = trimmedCities
        .map { c -> City(c.name, c.size, c.lat, c.lon) }
        .map { c -> Pair(c.name, c) }
        .toMap()
    for (c in trimmedCities.reversed()) {
        val city = cities[c.name]!!
        for (neighbor in c.neighbors) {
            if (city.neighbors.size>=3)
                break
            val nCity = cities[neighbor.city]
            if (nCity!=null) {
                city.neighbors.put(nCity, neighbor.dist)
                nCity.neighbors.put(city, neighbor.dist)
            }
        }
    }
    return RegionalData(rawData.map, cities.values.toList())
}
