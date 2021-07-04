import kotlinx.css.*
import styled.StyleSheet

object RelativesStyles :StyleSheet("RelativesStyles", isStatic = true) {
    val ancestor by css {
        margin(vertical = 5.px)
        verticalAlign = VerticalAlign.top
    }
    val descendant by css {
        margin(vertical = 5.px)
        verticalAlign = VerticalAlign.top
    }
}
