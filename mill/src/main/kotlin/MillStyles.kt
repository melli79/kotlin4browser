import styled.StyleSheet
import kotlinx.css.*

object MillStyles :StyleSheet("MillStyles", isStatic=true) {
    val board by css {
        padding = "5px"
        fontFamily = "Courier New"
        backgroundColor = Color.sandyBrown
        alignItems = Align.center
    }
}
