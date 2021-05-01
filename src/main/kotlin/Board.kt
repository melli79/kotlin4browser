import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*

val Size = 4

data class Move(val row :Int, val col :Int, val player :Field)

external interface BoardState :RState {
    var fields :MutableList<Field>
    var human :Field
    var winner :Field
    var moves :MutableList<Move>
}

fun BoardState.move() = moves.size

class RBoard :RComponent<RProps, BoardState>() {
    override fun BoardState.init() {
        fields = (1..Size*Size).map{ Field.Empty }.toMutableList()
        moves = mutableListOf()
        human = Field.Empty
        winner = Field.Empty
    }
    override fun RBuilder.render() {
        div("board") { for (r in 0 until Size) {
            div("row") { for (c in 0 until Size) {
                field {
                    attrs {
                        row = r
                        col = c
                        value = state.fields[Size*r +c]
                        if (state.winner == Field.Empty)
                            onClicked = { row, col ->
                                setState {
                                    if (state.move() == 0)
                                        human = Field.X
                                    fields[row*Size +col] = human
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
        }
    }

    private fun isGameOver() = state.winner!=Field.Empty || state.move() >= Size*Size

    private fun nextMove() {
        val wMove = state.checkWinningMove()
        if (wMove!=null) {
            setState {
                fields[wMove.row*Size +wMove.col] = wMove.player
                moves.add(wMove)
            }
            return
        }
        val fMove = state.checkForcingMove()
        if (fMove!=null) {
            setState {
                fields[fMove.row*Size +fMove.col] = fMove.player
                moves.add(fMove)
            }
            return
        }
        if (state.move() < Size*Size)
            for (f in 0 until Size*Size) {
            if (state.fields[f] == Field.Empty) {
                setState {
                    val player = if (human == Field.X) Field.O else Field.X
                    fields[f] = player
                    moves.add(Move(f/Size, f%Size, player))
                }
                break
            }
        }
    }

    private fun checkGameOver() {
        val candidate = listOf(state.findRowWinner(), state.findColWinner(), state.findDiagWinner(),
                state.findMDiagWinner())
            .filterNotNull().firstOrNull()
        if (candidate != null) {
            setState {
                winner = candidate
            }
        }
    }
}

fun BoardState.checkForcingMove() :Move? {
    if (human == Field.Empty)
        return null
    return listOf(checkFittingRowMove(human), checkFittingColMove(human), checkFittingDiagMove(human),
            checkFittingMDiagMove(human))
        .filterNotNull().firstOrNull()
}

fun BoardState.checkWinningMove() :Move? {
    val player = if (human == Field.X) Field.O else Field.X
    if (human == Field.Empty)
        return null
    return listOf(checkFittingRowMove(player), checkFittingColMove(player), checkFittingDiagMove(player),
        checkFittingMDiagMove(player))
        .filterNotNull().firstOrNull()
}

private fun BoardState.checkFittingRowMove(color :Field) :Move? {
    val player = if (human == Field.X) Field.O else Field.X
    outer@ for (row in 0 until Size) {
        var candidate :Move? = null
        for (col in 0 until Size)
            when (fields[row * Size + col]) {
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

private fun BoardState.checkFittingColMove(color :Field) :Move? {
    val player = if (human==Field.X) Field.O else Field.X
    outer@for (col in 0 until Size) {
        var candidate :Move? = null
        for (row in 0 until Size)
            when (fields[row*Size +col]) {
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

fun BoardState.checkFittingDiagMove(color :Field) :Move? {
    val player = if (human==Field.X) Field.O else Field.X
    var candidate :Move? = null
    for (p in 0 until Size)
        when (fields[p*(Size+1)]) {
            Field.Empty -> if (candidate==null)
                candidate = Move(p, p, player)
            else
                return null
            color -> return null
            else -> continue
        }
    return candidate
}

fun BoardState.checkFittingMDiagMove(color :Field) :Move? {
    val player = if (human==Field.X) Field.O else Field.X
    var candidate :Move? = null
    for (p in 1..Size)
        when (fields[p*(Size-1)]) {
            Field.Empty -> if (candidate==null)
                candidate = Move(p-1, Size-p, player)
            else
                return null
            color -> return null
            else -> continue
        }
    return candidate
}

fun BoardState.findColWinner() :Field? {
    outer@for (c in 0 until Size) {
        val candidate = fields[0 *Size +c]
        if (candidate == Field.Empty)
            continue
        for (r in 1 until Size)
            if (candidate != fields[r*Size +c])
                continue@outer
        return candidate
    }
    return null
}

fun BoardState.findRowWinner() :Field? {
    outer@for (r in 0 until Size) {
        val candidate = fields[r*Size +0]
        if (candidate == Field.Empty)
            continue
        for (c in 1 until Size)
            if (candidate != fields[r*Size +c])
                continue@outer
        return candidate
    }
    return null
}

fun BoardState.findDiagWinner() :Field? {
    val candidate = fields[0 *(Size+1)]
    if (candidate == Field.Empty)
        return null
    for (f in 1 until Size) if (candidate != fields[f *(Size+1)])
        return null
    return candidate
}

fun BoardState.findMDiagWinner() :Field? {
    val candidate = fields[Size-1]
    if (candidate == Field.Empty)
        return null
    for (p in 2..Size) if (candidate != fields[p*(Size-1)])
        return null
    return candidate
}


fun RBuilder.board(handler :RProps.() -> Unit) = child(RBoard::class) {
    this.attrs(handler)
}
