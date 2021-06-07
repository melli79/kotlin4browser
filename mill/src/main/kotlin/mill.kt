import react.*
import react.dom.*

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
                    +"Black won"
                else
                    +"White won"
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
        if (state.board.isValid(r, l, c)) setState {
            val stone = board.grid[selection.first][selection.second][selection.third]
            board.put(r, l, c, stone)
            board.grid[selection.first][selection.second][selection.third] = Stone.EMPTY
            selectedField = null
            board.move ++
        }
        if (state.board.hasMill(r, l, c))
            setState {
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
            if (board.put(r, l, c, if (board.move%2==0) Stone.BLACK else Stone.WHITE))
                board.move ++
        }
        if (state.board.hasMill(r, l, c)) setState {
            killStone = true
        }
    }
}
