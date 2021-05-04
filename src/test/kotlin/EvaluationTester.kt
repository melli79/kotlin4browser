import kotlin.test.*

class EvaluationTester {
    @Test fun equal() {
        val ev1 = Evaluation(Point.Correct, Point.Half, Point.Half, Point.Wrong)
        val ev2 = Evaluation(Point.Half, Point.Half, Point.Wrong, Point.Correct)

        assertEquals(ev1, ev2)
    }
}
