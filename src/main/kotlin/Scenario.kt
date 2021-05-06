import kotlinx.browser.window

data class Scenario(
    val criminal :String,
    val action :String,
    val kind :String,
    val weapon :String,
    val motive :String
) {
    override fun toString() = "$criminal, $action, $kind, $weapon, $motive."
    fun question() = "Did $criminal $action $kind with a(n) $weapon out of $motive?"
    fun statement() = "$criminal did $action $kind with a(n) $weapon out of $motive."
}

fun parseScenario(inquiry :String, components :CrimeSceneProps) :Scenario? {
    val criminal = extractItem(inquiry, components.criminals, "criminal") ?: return null
    val action = extractItem(inquiry, components.actions, "action") ?: return null
    val kind = extractItem(inquiry, components.kinds, "kind") ?: return null
    val weapon = extractItem(inquiry, components.weapons, "weapon") ?: return null
    val motive = extractItem(inquiry, components.motives, "motive") ?: return null

    return Scenario(criminal, action, kind, weapon, motive)
}

fun extractItem(inquiry :String, items :Array<String>, type :String) :String? {
    for (i in 0 until items.size) {
        if (items[i] in inquiry)
            return items[i]
    }
    window.alert("I did not understand the suspicious $type. Possible values are: ${items.joinToString()}")
    return null
}