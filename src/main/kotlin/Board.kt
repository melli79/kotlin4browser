import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*

data class Move(val row :Int, val col :Int, val player :Field)

external interface BoardProps :RProps {
    var size :Int
}

external interface BoardState :RState {
    var fields :MutableList<Field>
    var human :Field
    var winner :Field
    var moves :MutableList<Move>
}

fun BoardState.move() = moves.size

class RBoard(props :BoardProps) :RComponent<BoardProps, BoardState>(props) {
    override fun BoardState.init(props :BoardProps) {
        reset(props)
    }

    private fun BoardState.reset(props :BoardProps) {
        console.log(props.size)
        fields = (1..props.size * props.size).map { Field.Empty }.toMutableList()
        moves = mutableListOf()
        human = Field.Empty
        winner = Field.Empty
    }

    override fun RBuilder.render() {
        div("board") { for (r in 0 until props.size) {
            div("row") { for (c in 0 until props.size) {
                field {
                    attrs {
                        row = r
                        col = c
                        value = state.fields[props.size*r +c]
                        numButtons = props.size
                        if (state.winner == Field.Empty)
                            onClicked = { row, col ->
                                setState {
                                    if (state.move() == 0)
                                        human = Field.X
                                    fields[row*props.size +col] = human
                                    moves.add(Move(row, col, human))
                                    checkGameOver()
                                    nextMove()
                                    checkGameOver()
                                }
                            }
                    }
                }
            }}
        }}
        if (state.human==Field.Empty) {
            br {}
            button {
                attrs.onClickFunction = {
                    setState {
                        human = Field.O
                    }
                    nextMove()
                }
                +"pass"
            }
        }
        if (isGameOver()) {
            br {}
            p {
                when (state.winner) {
                    state.human -> +"You won the game!"
                    Field.Empty -> +"A tie."
                    else -> +"You lost the game!"
                }
            }
            button {
                attrs.onClickFunction = {
                    setState {
                        reset(props)
                    }
                }
                +"New Game?"
            }
        }
    }

    private fun isGameOver() = state.winner!=Field.Empty || state.move() >= props.size*props.size

    private fun nextMove() {
        val wMove = state.checkWinningMove(props.size)
        if (wMove!=null) {
            setState {
                fields[wMove.row*props.size +wMove.col] = wMove.player
                moves.add(wMove)
            }
            return
        }
        val fMove = state.checkForcingMove(props.size)
        if (fMove!=null) {
            setState {
                fields[fMove.row*props.size +fMove.col] = fMove.player
                moves.add(fMove)
            }
            return
        }
        if (state.move() < props.size*props.size)
            for (f in 0 until props.size*props.size) {
            if (state.fields[f] == Field.Empty) {
                setState {
                    val player = if (human == Field.X) Field.O else Field.X
                    fields[f] = player
                    moves.add(Move(f/props.size, f%props.size, player))
                }
                break
            }
        }
    }

    private fun checkGameOver() {
        val candidate = listOf(state.findRowWinner(props.size), state.findColWinner(props.size),
                state.findDiagWinner(props.size), state.findMDiagWinner(props.size))
            .filterNotNull().firstOrNull()
        if (candidate != null) {
            setState {
                winner = candidate
            }
        }
    }
}

fun BoardState.checkForcingMove(size :Int) :Move? {
    if (human == Field.Empty)
        return null
    return listOf(checkFittingRowMove(human, size), checkFittingColMove(human, size), checkFittingDiagMove(human, size),
            checkFittingMDiagMove(human, size))
        .filterNotNull().firstOrNull()
}

fun BoardState.checkWinningMove(size: Int) :Move? {
    val player = if (human == Field.X) Field.O else Field.X
    if (human == Field.Empty)
        return null
    return listOf(checkFittingRowMove(player, size), checkFittingColMove(player, size), checkFittingDiagMove(player, size),
        checkFittingMDiagMove(player, size))
        .filterNotNull().firstOrNull()
}

private fun BoardState.checkFittingRowMove(color :Field, size :Int) :Move? {
    val player = if (human == Field.X) Field.O else Field.X
    outer@ for (row in 0 until size) {
        var candidate :Move? = null
        for (col in 0 until size)
            when (fields[row*size +col]) {
                Field.Empty -> if (candidate == null)
                    candidate = Move(row, col, player)
                else continue@outer
                color -> continue@outer
                else -> continue
            }
        if (candidate != null)
            return candidate
    }
    return null
}

private fun BoardState.checkFittingColMove(color :Field, size :Int) :Move? {
    val player = if (human==Field.X) Field.O else Field.X
    outer@for (col in 0 until size) {
        var candidate :Move? = null
        for (row in 0 until size)
            when (fields[row*size +col]) {
                Field.Empty -> if (candidate==null)
                    candidate = Move(row, col, player)
                else continue@outer
                color -> continue@outer
                else -> continue
            }
        if (candidate!=null)
            return candidate
    }
    return null
}

fun BoardState.checkFittingDiagMove(color :Field, size :Int) :Move? {
    val player = if (human==Field.X) Field.O else Field.X
    var candidate :Move? = null
    for (p in 0 until size)
        when (fields[p*(size+1)]) {
            Field.Empty -> if (candidate==null)
                candidate = Move(p, p, player)
            else
                return null
            color -> return null
            else -> continue
        }
    return candidate
}

fun BoardState.checkFittingMDiagMove(color :Field, size :Int) :Move? {
    val player = if (human==Field.X) Field.O else Field.X
    var candidate :Move? = null
    for (p in 1..size)
        when (fields[p*(size-1)]) {
            Field.Empty -> if (candidate==null)
                candidate = Move(p-1, size-p, player)
            else
                return null
            color -> return null
            else -> continue
        }
    return candidate
}

fun BoardState.findColWinner(size :Int) :Field? {
    outer@for (c in 0 until size) {
        val candidate = fields[0 *size +c]
        if (candidate == Field.Empty)
            continue
        for (r in 1 until size)
            if (candidate != fields[r*size +c])
                continue@outer
        return candidate
    }
    return null
}

fun BoardState.findRowWinner(size :Int) :Field? {
    outer@for (r in 0 until size) {
        val candidate = fields[r*size +0]
        if (candidate == Field.Empty)
            continue
        for (c in 1 until size)
            if (candidate != fields[r*size +c])
                continue@outer
        return candidate
    }
    return null
}

fun BoardState.findDiagWinner(size :Int) :Field? {
    val candidate = fields[0 *(size+1)]
    if (candidate == Field.Empty)
        return null
    for (f in 1 until size) if (candidate != fields[f *(size+1)])
        return null
    return candidate
}

fun BoardState.findMDiagWinner(size :Int) :Field? {
    val candidate = fields[size-1]
    if (candidate == Field.Empty)
        return null
    for (p in 2..size) if (candidate != fields[p*(size-1)])
        return null
    return candidate
}


fun RBuilder.board(handler :BoardProps.() -> Unit) = child(RBoard::class) {
    this.attrs(handler)
}
