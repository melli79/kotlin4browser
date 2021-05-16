import kotlinx.browser.window

data class Scenario(
    val criminal :String,
    val action :String,
    val kind :String,
    val weapon :String,
    val motive :String
) {
    override fun toString() = "$criminal, $action, $kind, $weapon, $motive."
    fun question() = "Did $criminal $action Liz Taylor $kind with $weapon out of $motive?"
    fun statement() = "$criminal did $action Liz Taylor $kind with $weapon out of $motive."
}
