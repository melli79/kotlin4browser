import kotlinx.html.TR
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.xhr.XMLHttpRequest
import react.*
import react.dom.*
import styled.css
import styled.styledButton
import styled.styledTd

external interface RelativesProps :RProps {
    var url :String
}

data class RelativesState(var person :Person?, var expandeds :MutableSet<Person>) :RState

@JsExport
class RelativesComponent(props :RelativesProps) :RComponent<RelativesProps, RelativesState>(props) {
    override fun RelativesState.init(props :RelativesProps) {
        person = null
        expandeds = mutableSetOf()
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
                    expandeds.add(p.children.first().children.first())
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
                relative {
                    p = state.person!!
                    rel = Relative.person
                    expandeds = state.expandeds
                    expand = { person ->
                        setState {
                            expandeds.add(person)
                        }
                    }
                    collapse = { person ->
                        setState {
                            expandeds.remove(person)
                        }
                    }
                }
                relative {
                    p = state.person!!.parents.first().children.get(1)
                    rel = Relative.sibling
                    expandeds = state.expandeds
                    expand = { person ->
                        setState {
                            expandeds.add(person)
                        }
                    }
                    collapse = { person ->
                        setState {
                            expandeds.remove(person)
                        }
                    }
                }
            }
        }}
    }
}

fun RDOMBuilder<TR>.relative(handle :RelativeProp.()->Unit) {
    styledTd { css { +RelativesStyles.ancestor }
        child(RelativeComponent::class) { attrs(handle) }
    }
}

external interface RelativeProp :RProps {
    var p :Person
    var rel :Relative
    var expandeds :Set<Person>
    var expand :(Person) -> Unit
    var collapse :(Person) -> Unit
}

@JsExport
class RelativeComponent(props :RelativeProp) :RComponent<RelativeProp, RState>(props) {
    override fun RBuilder.render() {
        if (props.rel in listOf(Relative.parent, Relative.person)) {
            if (props.isExpanded()) {
                table { tbody {
                    tr {
                        for (par in props.p.parents)
                            relative {
                                p = par
                                rel = Relative.parent
                                expandeds = props.expandeds
                                expand = props.expand
                                collapse = props.collapse
                            }
                    }
                }}
            }
            if (props.hasKnownParents())
                styledButton { css { +RelativesStyles.exansionButton }
                    attrs {
                        onClickFunction = {
                            if (props.isExpanded())
                                props.collapse(props.p)
                            else
                                props.expand(props.p)
                        }
                    }
                    +if (props.isExpanded()) "-" else "+"
                }
        }
        +props.p.describe(props.rel)
        if (props.rel in listOf(Relative.child, Relative.person, Relative.sibling)) {
            if (props.hasChildren())
                styledButton { css { +RelativesStyles.exansionButton }
                    attrs {
                        onClickFunction = {
                            if (props.isExpanded())
                                props.collapse(props.p)
                            else
                                props.expand(props.p)
                        }
                    }
                    +if (props.isExpanded()) "-" else "+"
                }
            if (props.isExpanded()) {
                table { tbody {
                    tr {
                        for (c in props.p.children)
                            relative {
                                p = c
                                rel = Relative.child
                                expandeds = props.expandeds
                                expand = props.expand
                                collapse = props.collapse
                            }
                    }
                }}
            }
        }
    }

    private fun RelativeProp.isExpanded() = p in expandeds

    private fun RelativeProp.hasKnownParents() = p.parents.isNotEmpty()
    private fun RelativeProp.hasChildren() = p.children.isNotEmpty()
}
