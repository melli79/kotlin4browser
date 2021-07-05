import kotlinx.css.*
import styled.StyleSheet

object RelativesStyles :StyleSheet("RelativesStyles", isStatic = true) {
    val exansionButton by css {
        backgroundColor = Color.white
        borderRadius = 14.px

    }
    val ancestor by css {
        margin(vertical = 5.px)
        verticalAlign = VerticalAlign.bottom
    }
    val descendant by css {
        margin(vertical = 5.px)
        verticalAlign = VerticalAlign.top
    }
}
