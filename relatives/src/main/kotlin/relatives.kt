import kotlinx.html.TR
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.xhr.XMLHttpRequest
import react.*
import react.dom.*
import styled.css
import styled.styledTd

external interface RelativesProps :RProps {
    var url :String
}

data class RelativesState(var person :Person?) :RState

@JsExport
class RelativesComponent(props :RelativesProps) :RComponent<RelativesProps, RelativesState>(props) {
    override fun RelativesState.init(props :RelativesProps) {
        person = null
        retrievePerson(props.url)
    }

    private fun retrievePerson(url :String) {
        val request = XMLHttpRequest()
        request.open("GET", url)
        request.send(null)
        request.onreadystatechange = {
            val json = request.responseText
            if (json.isNotBlank()) {
                val p = Json.decodeFromString<Person>(json)
                p.balance()
                setState {
                    person = p.children.first().children.first()
                }
            }
        }
    }

    override fun RBuilder.render() {
        h1 {
            +"Hello, ${state.person}"
        }
        if (state.person==null)
            return
        table { tbody {
            tr {
                for (p in state.person!!.parents)
                    renderParent(p)
            }
            tr {
                styledTd {
                    css { +RelativesStyles.descendant }
                    +state.person!!.describe()
                }
                for (s in state.person!!.parents.first().children) if (s!=state.person)
                    renderChild(s)
            }
        }}
    }

    private fun RDOMBuilder<TR>.renderChild(s :Person, depth :Int =2) {
        styledTd { css { +RelativesStyles.descendant }
            +s.describe(Relative.sibling)
            table { tbody {
                tr {
                    for (c in s.children)
                        td {
                            +c.describe()
                        }
                }
            }}
        }
    }

    private fun RDOMBuilder<TR>.renderParent(p :Person, depth :Int =2) {
        styledTd { css { +RelativesStyles.ancestor }
            table { tbody {
                tr {
                    for (g in p.parents)
                        td {
                            +g.describe()
                        }
                }
            }}
            +p.describe(Relative.parent)
        }
    }
}
