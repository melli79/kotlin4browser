
import kotlin.test.*

class GroupTester {
    @Test fun identifiesSets() {
        val aSet = Triple(Card(knownSymbols[0], knownColors[0], knownShades[0], knownRepetitions[0]),
            Card(knownSymbols[1], knownColors[0], knownShades[0], knownRepetitions[0]),
            Card(knownSymbols[2], knownColors[0], knownShades[0], knownRepetitions[0]))
        assertTrue(aSet.isGroup())
    }

    @Test fun rejectsNonSets() {
        val aSet = Triple(Card(knownSymbols[0], knownColors[0], knownShades[0], knownRepetitions[0]),
            Card(knownSymbols[1], knownColors[0], knownShades[0], knownRepetitions[0]),
            Card(knownSymbols[1], knownColors[1], knownShades[0], knownRepetitions[0]))
        assertFalse(aSet.isGroup())
    }
}
