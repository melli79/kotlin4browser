import react.dom.render
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.currentTimeMillis
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.setState
import kotlin.random.Random

val random = Random(currentTimeMillis())

data class AppState(var region :KnownRegion?, var playerName :String = "Player 001") :State {
}

class App :RComponent<Props, AppState>() {
    override fun RBuilder.render() {
        if (state.region==null)
            welcomeComponent {
                knownRegions = KnownRegion.values()
                setPlayerName = { name ->
                    setState {
                        playerName = name
                    }
                }
                setRegion = { region ->
                    setState {
                        this.region = region
                    }
                }
            }
        else
            mapComponent {
                playerName = state.playerName
                region = state.region!!
            }
    }
}

fun main() {
    window.onload = {
        render(document.getElementById("root")!!) {
            child(App::class) { }
        }
    }
}
