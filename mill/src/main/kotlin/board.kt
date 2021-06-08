import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*
import styled.*

enum class Stone(val symbol :String) {
    EMPTY("+") {
        override fun opposite() = EMPTY
    }, BLACK("●") {
        override fun opposite() = WHITE
    }, WHITE("O") {
        override fun opposite() = BLACK
    };

    abstract fun opposite() :Stone
}

enum class Phase {
    PUT, MOVE, JUMP, END;
}

@JsExport
data class Board(
    val grid :Array<Array<Array<Stone>>> = (1..3).map{ (1..3).map{(1..3).map{ Stone.EMPTY }.toTypedArray()}.toTypedArray()}.toTypedArray(),
    var move :Int =0,
    var tie :Boolean =false
) :RProps {
    fun isValid(r :Int, l :Int, c :Int) :Boolean =
        0<=r&&r<3 && 0<=l&&l<3&&0<=c&&c<3 && (l!=1||c!=1) &&
                grid[r][l][c]==Stone.EMPTY

    fun put(r :Int, l :Int, c :Int, s :Stone) :Boolean {
        if (isValid(r, l, c)) {
            grid[r][l][c] = s
            return true
        }
        return false
    }

    fun hasMill(r :Int, l :Int, c :Int) =
        grid[r][l][c] != Stone.EMPTY && (
        grid[r][l][(c+1)%3]==grid[r][l][c]&&grid[r][l][(c+2)%3]==grid[r][l][c]
        || grid[r][(l+1)%3][c]==grid[r][l][c]&&grid[r][(l+2)%3][c]==grid[r][l][c]
        || (l==1||c==1)&&grid[(r+1)%3][l][c]==grid[r][l][c]&&grid[(r+2)%3][l][c]==grid[r][l][c])

    fun whoseMove() = if (move%2==0) Stone.BLACK else Stone.WHITE

    fun getPhase() = when {
        move < 14 -> Phase.PUT
        tie||countStones(Stone.WHITE)<3||countStones(Stone.BLACK)<3 -> Phase.END
        countStones(Stone.WHITE)==3||countStones(Stone.BLACK)==3 -> Phase.JUMP
        else -> Phase.MOVE
    }

    fun countStones(color :Stone) = grid.sumOf { sq ->
        sq.sumOf { l -> l.count { s -> s==color } }
    }

    override fun equals(other :Any?) :Boolean {
        if (this === other) return true
        if (other == null || other !is Board) return false

        return move == other.move && grid.contentDeepEquals(other.grid)
    }

    override fun hashCode() :Int {
        var result = grid.contentDeepHashCode()
        result = 31 * result + move
        return result
    }

    fun cannotMove() :Boolean {
        if (countStones(whoseMove())<=3) return false
        for (r in 0..2) {
            for (l in 0..2) {
                for (c in 0..2)
                    if (grid[r][l][c]==whoseMove()&&canMove(r,l,c))
                        return false
            }
        }
        return true
    }

    private fun canMove(r :Int, l :Int, c :Int) =
        grid[r][l][(c+1)%3]==Stone.EMPTY || grid[r][l][(c+2)%3]==Stone.EMPTY ||
        grid[r][(l+1)%3][c]==Stone.EMPTY || grid[r][(l+2)%3][c]==Stone.EMPTY ||
        grid[(r+1)%3][l][c]==Stone.EMPTY || grid[(r+2)%3][l][c]==Stone.EMPTY
}

external interface BoardProps :RProps {
    var board :Board
    var click :(r :Int, l :Int, c :Int) -> Unit
}

@JsExport
class BoardComponent :RComponent<BoardProps, RState>() {
    override fun RBuilder.render() {
        styledTable {
            css { +MillStyles.board }
            tbody {
                tr { // outer
                    td {
                        attrs { onClickFunction = { props.click(0, 0, 0) } }
                        +props.board.grid[0][0][0].symbol
                    }
                    td { +"–" }
                    td { +"–" }
                    td {
                        attrs { onClickFunction = { props.click(0, 0, 1) } }
                        +props.board.grid[0][0][1].symbol
                    }
                    td { +"–" }
                    td { +"–" }
                    td {
                        attrs { onClickFunction = { props.click(0, 0, 2) } }
                        +props.board.grid[0][0][2].symbol
                    }
                }
                tr { // middle
                    td { +"|" }
                    td {
                        attrs { onClickFunction = { props.click(1, 0, 0) } }
                        +props.board.grid[1][0][0].symbol
                    }
                    td { +"–" }
                    td {
                        attrs { onClickFunction = { props.click(1, 0, 1) } }
                        +props.board.grid[1][0][1].symbol
                    }
                    td { +"–" }
                    td {
                        attrs { onClickFunction = { props.click(1, 0, 2) } }
                        +props.board.grid[1][0][2].symbol
                    }
                    td { +"|" }
                }
                tr { // inner
                    td { +"|" }
                    td { +"|" }
                    td {
                        attrs { onClickFunction = { props.click(2, 0, 0) } }
                        +props.board.grid[2][0][0].symbol
                    }
                    td {
                        attrs { onClickFunction = { props.click(2, 0, 1) } }
                        +props.board.grid[2][0][1].symbol
                    }
                    td {
                        attrs { onClickFunction = { props.click(2, 0, 2) } }
                        +props.board.grid[2][0][2].symbol
                    }
                    td { +"|" }
                    td { +"|" }
                }
                tr { // center
                    td {
                        attrs { onClickFunction = { props.click(0, 1, 0) } }
                        +props.board.grid[0][1][0].symbol
                    }
                    td {
                        attrs { onClickFunction = { props.click(1, 1, 0) } }
                        +props.board.grid[1][1][0].symbol
                    }
                    td {
                        attrs { onClickFunction = { props.click(2, 1, 0) } }
                        +props.board.grid[2][1][0].symbol
                    }
                    td { +"\u00A0" }
                    td {
                        attrs { onClickFunction = { props.click(2, 1, 2) } }
                        +props.board.grid[2][1][2].symbol
                    }
                    td {
                        attrs { onClickFunction = { props.click(1, 1, 2) } }
                        +props.board.grid[1][1][2].symbol
                    }
                    td {
                        attrs { onClickFunction = { props.click(0, 1, 2) } }
                        +props.board.grid[0][1][2].symbol
                    }
                }
                tr { // inner
                    td { +"|" }
                    td { +"|" }
                    td {
                        attrs { onClickFunction = { props.click(2, 2, 0) } }
                        +props.board.grid[2][2][0].symbol
                    }
                    td {
                        attrs { onClickFunction = { props.click(2, 2, 1) } }
                        +props.board.grid[2][2][1].symbol
                    }
                    td {
                        attrs { onClickFunction = { props.click(2, 2, 2) } }
                        +props.board.grid[2][2][2].symbol
                    }
                    td { +"|" }
                    td { +"|" }
                }
                tr { // middle
                    td { +"|" }
                    td {
                        attrs { onClickFunction = { props.click(1, 2, 0) } }
                        +props.board.grid[1][2][0].symbol
                    }
                    td { +"–" }
                    td {
                        attrs { onClickFunction = { props.click(1, 2, 1) } }
                        +props.board.grid[1][2][1].symbol
                    }
                    td { +"–" }
                    td {
                        attrs { onClickFunction = { props.click(1, 2, 2) } }
                        +props.board.grid[1][2][2].symbol
                    }
                    td { +"|" }
                }
                tr { // outer
                    td {
                        attrs { onClickFunction = { props.click(0, 2, 0) } }
                        +props.board.grid[0][2][0].symbol
                    }
                    td { +"–" }
                    td { +"–" }
                    td {
                        attrs { onClickFunction = { props.click(0, 2, 1) } }
                        +props.board.grid[0][2][1].symbol
                    }
                    td { +"–" }
                    td { +"–" }
                    td {
                        attrs { onClickFunction = { props.click(0, 2, 2) } }
                        +props.board.grid[0][2][2].symbol
                    }
                }
            }
        }
    }
}

fun RBuilder.board(handler :BoardProps.() -> Unit) = child(BoardComponent::class) {
    this.attrs(handler)
}
