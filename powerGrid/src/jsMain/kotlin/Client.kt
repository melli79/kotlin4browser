import react.dom.render
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.currentTimeMillis
import kotlin.random.Random

val random = Random(currentTimeMillis())

enum class KnownMaps(prefix :String, val region :String) {
    CHINA("China", "P.R. of China"),
    EUROPE("Europe", "Europe"),
    GERMANY("Germany", "Germany"),
    N_AMERICA("nAmerica", "Northern America");

    val mapImage = "$prefix.jpg"
    val mapDetails = "cities.$prefix.json"
}

fun main() {
    window.onload = {
        render(document.getElementById("root")!!) {
            child(Welcome::class) {
                attrs {
                    knownMaps = KnownMaps.values()
                }
            }
        }
    }
}
