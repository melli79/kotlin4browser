import kotlinx.css.Color
import kotlin.random.Random

sealed class Pin(val index :Int, val color :Color, val abbr :Char, val text :String) {
    object Red :Pin(0, Color.red, 'X', "Xred")
    object Orange :Pin(1, Color.orange, 'O', "Orange")
    object Brown :Pin(2, Color.brown, 'N', "browN")
    object Green :Pin(3, Color.green, 'G', "Green")
    object Blue :Pin(4, Color.blue, 'B', "Blue")
    object Purple :Pin(5, Color.purple, 'P', "Purple")
    companion object {
        val values = listOf(Red, Orange, Brown, Green, Blue, Purple)
        fun fromChar(c :Char) = values.firstOrNull { color -> c.toUpperCase() == color.abbr }
    }
}

fun Random.nextPin() = Pin.values[nextInt(Pin.values.size)]

data class Code(val first :Pin, val second :Pin, val third :Pin, val fourth :Pin) {
    operator fun get(i :Int) = when(i) {
        0 -> first
        1 -> second
        2 -> third
        3 -> fourth
        else -> throw IndexOutOfBoundsException("only 4 colors in the state")
    }
    fun <T> map(mapper :(Pin) -> T) = listOf(mapper(first), mapper(second), mapper(third), mapper(fourth))
}

fun Random.nextCode() = Code(nextPin(), nextPin(), nextPin(), nextPin())
