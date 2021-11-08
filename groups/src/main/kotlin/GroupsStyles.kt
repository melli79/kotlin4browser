import kotlinx.css.*
import styled.StyleSheet

object GroupsStyles :StyleSheet("SetStyles", isStatic = true) {

    val title by css {
        color = Color.green
    }
    val textInput by css {
        margin(vertical = 5.px)

        fontSize = 14.px
    }
} 
