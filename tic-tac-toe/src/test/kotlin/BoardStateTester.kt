import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BoardStateTester {
    @Test fun findDiagonalWinner() {
        val result = stateOf(mutableListOf(Field.O, Field.Empty, Field.Empty,
            Field.Empty, Field.O, Field.Empty,
            Field.Empty, Field.Empty, Field.O))
            .findDiagWinner(3)
        assertEquals(Field.O, result)
    }

    @Test fun findMDiagonalWinner() {
        val result = stateOf(mutableListOf(
                Field.Empty, Field.Empty, Field.O,
                Field.Empty, Field.O,     Field.Empty,
                Field.O,     Field.Empty, Field.O))
            .findMDiagWinner(3)
        assertEquals(Field.O, result)
    }

    @Test fun findDiagonalWinner_empty() {
        val result = stateOf(mutableListOf(
                Field.Empty, Field.Empty, Field.Empty,
                Field.Empty, Field.O, Field.Empty,
                Field.Empty, Field.Empty, Field.O))
            .findDiagWinner(3)
        assertNull(result)
    }

    @Test fun findRowWinner_1row() {
        val result = stateOf(mutableListOf(
                Field.X, Field.X, Field.X,
                Field.Empty, Field.Empty, Field.Empty,
                Field.Empty, Field.Empty, Field.Empty))
            .findRowWinner(3)
        assertEquals(Field.X, result)
    }

    @Test fun findRowWinner_2row() {
        val result = stateOf(mutableListOf(
            Field.Empty, Field.Empty, Field.Empty,
            Field.X, Field.X, Field.X,
            Field.Empty, Field.Empty, Field.Empty))
            .findRowWinner(3)
        assertEquals(Field.X, result)
    }

    @Test fun findRowWinner_3row() {
        val result = stateOf(mutableListOf(
            Field.Empty, Field.Empty, Field.Empty,
            Field.Empty, Field.Empty, Field.Empty,
            Field.X,     Field.X,     Field.X))
            .findRowWinner(3)
        assertEquals(Field.X, result)
    }

    @Test fun findColWinner_1col() {
        val result = stateOf(mutableListOf(
            Field.X, Field.Empty, Field.Empty,
            Field.X, Field.Empty, Field.Empty,
            Field.X, Field.Empty, Field.Empty))
            .findColWinner(3)
        assertEquals(Field.X, result)
    }

    @Test fun findColWinner_2col() {
        val result = stateOf(mutableListOf(
                Field.Empty, Field.X, Field.Empty,
                Field.Empty, Field.X, Field.Empty,
                Field.Empty, Field.X, Field.Empty))
            .findColWinner(3)
        assertEquals(Field.X, result)
    }

    @Test fun findColWinner_3col() {
        val result = stateOf(mutableListOf(
            Field.Empty, Field.Empty, Field.X,
            Field.Empty, Field.Empty, Field.X,
            Field.Empty, Field.Empty, Field.X))
            .findColWinner(3)
        assertEquals(Field.X, result)
    }

    data class stateOf(override var fields :MutableList<Field>,
                  override var moves :MutableList<Move> = mutableListOf(),
                  override var human :Field =Field.Empty,
                  override var winner :Field =Field.Empty
    ) :BoardState
}
