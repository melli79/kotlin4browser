import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*

enum class Field {
    Empty, X, O
}

external interface FieldProps :RProps {
    var value :Field
    var row :Int
    var col :Int
    var onClicked :(Int, Int) -> Unit
}

class RField(props :FieldProps) :RComponent<FieldProps, RState>(props) {
    override fun RBuilder.render() {
        button {
            if (props.value==Field.O)
                +"O"
            else if (props.value==Field.X)
                +"X"
            else if (props.value==Field.Empty) {
                attrs {
                    onClickFunction = {
                        props.onClicked(props.row, props.col)
                    }
                }
                +" "
            }
        }
    }
}

fun RBuilder.field(handler :FieldProps.() -> Unit) = child(RField::class) {
    this.attrs(handler)
}
