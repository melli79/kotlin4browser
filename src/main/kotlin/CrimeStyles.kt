import kotlinx.css.*
import styled.StyleSheet

object CrimeStyles :StyleSheet("CrimeStyles", isStatic=true) {
    val observationStyle by css {
        padding = "30px"
    }
    val alibiStyle by css {
        color = Color.green
    }
    val title by css {
        color = Color.red.lighten(20)
    }

    val inputStyle by css {
        margin = "10px"
        width = 250.px
    }
}
