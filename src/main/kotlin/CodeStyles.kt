import kotlinx.css.*
import kotlinx.css.FontWeight.Companion.bold
import styled.StyleSheet

object CodeStyles :StyleSheet("WelcomeStyles", isStatic = true) {
    val choiceStyle by css {
        listStyleType = ListStyleType.disc
    }
    val headline by css {
        padding(5.px)
        color = Color.lightGreen.darken(20)
    }

    val pins by css {
        fontWeight = bold
        fontSize = 32.px
    }

    val guessInput by css {
        margin(vertical = 5.px)
        fontSize = 14.px
        width = 52.px
    }
} 
