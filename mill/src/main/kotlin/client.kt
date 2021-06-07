import kotlinx.browser.*
import react.dom.render

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            child(Mill::class) {}
        }
    }
}
