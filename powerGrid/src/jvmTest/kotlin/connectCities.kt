import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.google.gson.GsonBuilder
import java.io.BufferedWriter
import java.io.File
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class KNeighbor(val city :String, val dist :Int)
data class KCity(val name: String, val size :Int, val lat :Double, val lon :Double, val neighbors :List<KNeighbor> = listOf())

data class Neighbor(val city :City, val dist :Double)
data class City(val name: String, val size :Int, val lat :Double, val lon :Double, val neighbors :MutableList<Neighbor> = mutableListOf())

fun main() {
    for (region in listOf("Germany", "Europe", "China", "nAmerica")) {
        println("$region:")
        val cities = readCities(region)
        println("${cities.size}")
        findNeighbors(cities.subList(0, 3*8), 3)
        println("${cities[0].neighbors.size} neighbors,")
        findNeighbors(cities.subList(0, 5*8), 3)
        println("${cities[0].neighbors.size} neighbors,")
        findNeighbors(cities.subList(0, 7*8), 3)
        println("${cities[0].neighbors.size} neighbors,")
        findNeighbors(cities.subList(0, 9*8), 3)
        println("${cities[0].neighbors.size} neighbors,")
        writeCities(region, cities.toKCities())
    }
}

private fun List<City>.toKCities() = map { city ->
    KCity(city.name, city.size, city.lat, city.lon, city.neighbors.map { n ->
        KNeighbor(n.city.name, ln(n.dist).roundToInt()) })
}

fun findNeighbors(cities :List<City>, numAddNeighbors :Int) {
    for (city in cities) {
        val offset = city.neighbors.size
        for (city2 in cities) if (city!=city2 && city2 !in city.neighbors) {
            val d = dist(city, city2)
            city.neighbors.insertAndTrunc(Neighbor(city2, d), offset, offset+numAddNeighbors)
        }
    }
}

operator fun Collection<Neighbor>.contains(city :City) = any { n -> n.city==city }

private fun MutableList<Neighbor>.insertAndTrunc(neighbor :Neighbor, offset :Int, numNeighbors :Int) {
    var min = offset
    var max = size
    while (min<max) {
        val mid = (min+max)/2
        if (this[mid].dist<neighbor.dist)
            min = mid+1
        else
            max = mid
    }
    if (min< numNeighbors-1)
        add(min, neighbor)
    while (size>numNeighbors)
        removeAt(size-1)
}

fun sqr(x :Double) = x*x

val kmPerDeg = 1000.0/9

fun dist(city :City, city2 :City) = sqrt(sqr(
    sin((city.lat+city2.lat)*0.5)*(city2.lon-city.lon))
    +sqr(city2.lat-city.lat))*kmPerDeg

private fun readCities(region :String) :List<City> {
    val result = mutableListOf<City>()
    csvReader().open("cities.$region.csv") {
        readAllWithHeaderAsSequence().forEach { row ->
            val name = row["city"]
            val population = row["population"]?.toInt()
            val lat = row["latitude"]
            val lon = row["longitude"]
            if (name!=null && lat!=null && lon!=null) {
                val size = if (population!=null) ln(population.toDouble()).roundToInt()
                    else 0
                val city = City(name, size, lat.parseDeg(), lon.parseDeg())
                result.add(city)
            }
        }
    }
    return result
}

private fun String.parseDeg() :Double {
    val dInd = indexOf("°")
    if (dInd>=0) {
        val mInd = indexOf("′", dInd+2)
        if (mInd>=0) {
            val sInd = indexOf("”", mInd+2)
            val sec = if (sInd>=0)
                substring(mInd+1 until sInd).toDouble()
            else
                0.0
            return substring(0 until dInd).toDouble() +
                    (substring(dInd+1 until mInd).toDouble() +sec/60)/60
        }
        return substring(0 until dInd).toDouble()
    }
    return toDouble()
}

fun writeCities(region :String, cities :List<KCity>) {
    File("cities.$region.json").writeMultiple { writer ->
        writer.write("[ ")
        for (city in cities) {
            writer.write(city)
            writer.write(", ")
        }
        writer.write("]")
    }
}

class JsonWriter(private val writer :BufferedWriter) {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    fun write(str :String) {
        writer.write(str)
    }

    fun write(obj :Any) {
        writer.write(gson.toJson(obj))
    }

    fun close() = writer.close()
}

fun File.writeMultiple(handler :(writer :JsonWriter) -> Unit) {
    val writer = JsonWriter(bufferedWriter())
    try {
        handler(writer)
    } finally {
        writer.close()
    }
}
