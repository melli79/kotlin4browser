import kotlinx.css.Color
import kotlinx.css.color
import kotlinx.css.fontSize
import kotlinx.css.margin
import kotlinx.css.px
import styled.StyleSheet

object PowerGridStyles : StyleSheet("PowerGridStyles", isStatic = true) {

    val title by css {
        color = Color.green
    }

    val textInput by css {
        margin(vertical = 5.px)
        fontSize = 14.px
    }
}