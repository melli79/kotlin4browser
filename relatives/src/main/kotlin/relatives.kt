import kotlinx.html.TD
import kotlinx.html.TR
import kotlinx.html.js.onClickFunction
import kotlinx.html.progress
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
                selectPerson(p.children.first().children.first())
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
                    selectNode = { p -> selectPerson(p) }
                }
            }
        }}
    }

    fun selectPerson(p :Person) {
        val maxLinear = findAncestors(p)+findDescendants(p)
        setState {
            person = p
            expandeds = maxLinear.toMutableSet()
        }
    }

    private fun findAncestors(p :Person) :Set<Person> = setOf(p)+p.parents.flatMap { par -> findAncestors(par) }.toSet()
    private fun findDescendants(p :Person) :Set<Person> = setOf(p)+p.children.flatMap { c -> findDescendants(c) }.toSet()
}

fun RDOMBuilder<TR>.relative(colspan :Int =1, handle :RelativeProp.()->Unit) {
    styledTd { css { +RelativesStyles.ancestor }
        attrs { colSpan = colspan.toString() }
        child(RelativeComponent::class) { attrs(handle) }
    }
}

external interface RelativeProp :RProps {
    var p :Person
    var rel :Relative
    var expandeds :Set<Person>
    var expand :(Person) -> Unit
    var collapse :(Person) -> Unit
    var selectNode :(Person) -> Unit
}

@JsExport
class RelativeComponent(props :RelativeProp) :RComponent<RelativeProp, RState>(props) {
    override fun RBuilder.render() {
        if (props.rel in listOf(Relative.parent, Relative.person)) {
            if (props.isExpanded()) {
                renderWithParents()
            } else {
                showExpandParentsButton()
                describe()
                maybeShowExpandChildrenButton()
            }
        } else {
            describe()
            maybeShowExpandChildrenButton()
        }
        if (props.rel in listOf(Relative.child, Relative.person, Relative.sibling)) {
            if (props.isExpanded())
                renderChildren()
        }
    }

    private fun RBuilder.renderChildren() {
        table { tbody {
            tr {
                for (c in props.p.children)
                    relative {
                        p = c
                        rel = Relative.child
                        expandeds = props.expandeds
                        expand = props.expand
                        collapse = props.collapse
                        selectNode = props.selectNode
                    }
            }
        }}
    }

    private fun RBuilder.maybeShowExpandChildrenButton() {
        if (props.hasChildren())
            styledButton {
                css { +RelativesStyles.exansionButton }
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

    private fun RBuilder.showExpandParentsButton() {
        if (props.hasKnownParents())
            styledButton {
                css { +RelativesStyles.exansionButton }
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

    private fun RBuilder.renderWithParents() {
        table {
            tbody {
                tr {
                    var numSiblings = getSiblings().size
                    var first = true
                    for (par in props.p.parents) {
                        relative(numSiblings/2) {
                            p = par
                            rel = Relative.parent
                            expandeds = props.expandeds
                            expand = props.expand
                            collapse = props.collapse
                            selectNode = props.selectNode
                        }
                        if (first) {
                            numSiblings += numSiblings%2
                            first = false
                        }
                    }
                }
                tr {
                    td {
                        showExpandParentsButton()
                        describe()
                        maybeShowExpandChildrenButton()
                    }
                    renderSiblings()
                }
            }
        }
    }

    private fun RDOMBuilder<TR>.renderSiblings() {
        for (s in getSiblings()) if (s != props.p)
            relative {
                p = s
                rel = Relative.sibling
                expandeds = props.expandeds
                expand = props.expand
                collapse = props.collapse
                selectNode = props.selectNode
            }
    }

    private fun getSiblings() = props.p.parents
        .flatMap { p -> p.children }
        .toSet()


    private fun RBuilder.describe() {
        span {
            attrs {
                onClickFunction = {
                    props.selectNode(props.p)
                }
            }
            +props.p.describe(props.rel)
        }
    }
    private fun RDOMBuilder<TD>.describe() {
        span {
            attrs {
                onClickFunction = {
                    props.selectNode(props.p)
                }
            }
            +props.p.describe(props.rel)
        }
    }

    private fun RelativeProp.isExpanded() = p in expandeds

    private fun RelativeProp.hasKnownParents() = p.parents.isNotEmpty()
    private fun RelativeProp.hasChildren() = p.children.isNotEmpty()
}
