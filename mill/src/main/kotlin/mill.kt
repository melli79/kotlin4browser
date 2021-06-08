import react.*
import react.dom.*
import kotlin.math.abs

external interface MillState :RState {
    var board :Board
    var selectedField :Triple<Int, Int, Int>?
    var killStone :Boolean
}

class Mill :RComponent<RProps, MillState>() {
    override fun MillState.init() {
        board = Board()
        killStone = false
    }

    override fun RBuilder.render() {
        h1 {
            +"Mill"
        }
        h2 {
            +when (state.board.getPhase()) {
                Phase.PUT -> "Phase 1: put stones"
                Phase.MOVE -> "Phase 2: move stones"
                Phase.JUMP -> "Phase 3: end game"
                else -> "Game over!"
            }
        }
        board {
            board = state.board
            click = if (state.board.getPhase()!=Phase.END)
                this@Mill::onClick
              else
                {_,_,_ -> }
        }
        p {
            if (state.killStone)
               +"Kill a ${state.board.whoseMove()} stone."
            else if (state.selectedField!=null)
               +"Move the ${state.board.whoseMove()} stone."
            else if (state.board.getPhase()==Phase.END)
                if (state.board.countStones(Stone.WHITE)<3)
                    +"Black won!"
                else if (state.board.tie)
                    +"A tie."
                else
                    +"White won!"
            else {
                +"${state.board.whoseMove()}s move (${state.board.move + 1})"
                if (state.board.cannotMove()) {
                    setState {
                        board.move ++
                        if (state.board.cannotMove())
                            state.board.tie = true
                    }
                }
            }
        }
        p {
            +"""A mill is three stones in a row or column.  Whenever you get a mill, 
                |you can kill (i.e. remove) one of the opponent's stones.  You lost 
                |when you have fewer than 3 stones (because then you cannot kill any
                | opponent's stones anymore).""".trimMargin()
        }
        p {
            +"""In the first phase you put stones on the board.
                |In the second phase you move stones to neighboring fields.
                |If you have only 3 stones left, your stones may jump.""".trimMargin()
        }
    }

    private fun onClick(r :Int, l :Int, c :Int) = when {
        state.killStone -> killStone(r, l, c)
        state.selectedField!=null -> maybeMoveStone(r, l, c, state.selectedField!!)
        state.board.getPhase()==Phase.PUT -> putStone(r, l, c)
        else -> maybeSelectStone(r, l, c)
    }

    private fun killStone(r :Int, l :Int, c :Int) {
        if (state.board.grid[r][l][c]==state.board.whoseMove())
            setState {
                board.grid[r][l][c] = Stone.EMPTY
                killStone = false
            }
    }

    private fun maybeMoveStone(r :Int, l :Int, c :Int, selection :Triple<Int, Int, Int>) {
        if (state.board.isValid(r, l, c) && (selection.isNeighbor(r,l,c)||state.board.getPhase()==Phase.JUMP))
            setState {
                val stone = board.grid[selection.first][selection.second][selection.third]
                board.put(r, l, c, stone)
                board.grid[selection.first][selection.second][selection.third] = Stone.EMPTY
                selectedField = null
                board.move ++
                if (state.board.hasMill(r, l, c))
                    killStone = true
            }
    }

    private fun maybeSelectStone(r :Int, l :Int, c :Int) {
        if (state.board.grid[r][l][c]==state.board.whoseMove()) {
            setState {
                selectedField = Triple(r, l, c)
            }
        }
    }

    private fun putStone(r :Int, l :Int, c :Int) {
        setState {
            if (board.put(r, l, c, if (board.move%2==0) Stone.BLACK else Stone.WHITE)) {
                board.move++
                if (state.board.hasMill(r, l, c))
                    killStone = true
            }
        }
    }
}

private fun Triple<Int, Int, Int>.isNeighbor(r :Int, l :Int, c :Int) =
    first==r&&second==l&&abs(third-c)==1 ||
    first==r&&abs(second-l)==1&&third==c ||
    abs(first-r)==1&&second==l&&third==c&&(second==1||third==1)
